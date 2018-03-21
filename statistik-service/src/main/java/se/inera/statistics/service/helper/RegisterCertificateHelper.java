/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.apache.neethi.builders.converters.ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.Arbetsnedsattning;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Svar;

@Component
public class RegisterCertificateHelper {

    public static final String DIAGNOS_SVAR_ID_6 = "6";
    public static final String DIAGNOS_DELSVAR_ID_6 = "6.2";
    public static final String FUNKTIONSNEDSATTNING_SVAR = "35";
    public static final String FUNKTIONSNEDSATTNING_DELSVAR = "35.1";
    public static final String AKTIVITETSBEGRANSNING_SVAR = "17";
    public static final String AKTIVITETSBEGRANSNING_DELSVAR = "17.1";
    public static final String BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32 = "32";
    public static final String BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32 = "32.1";
    public static final String BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32 = "32.2";

    private static final Logger LOG = LoggerFactory.getLogger(RegisterCertificateHelper.class);
    private Unmarshaller jaxbUnmarshaller;

    public String getEnhetId(RegisterCertificateType utlatande) {
        return utlatande.getIntyg().getSkapadAv().getEnhet().getEnhetsId().getExtension();
    }

    public String getLakareId(RegisterCertificateType document) {
        return document.getIntyg().getSkapadAv().getPersonalId().getExtension();
    }

    public String getVardgivareId(RegisterCertificateType document) {
        return document.getIntyg().getSkapadAv().getEnhet().getVardgivare().getVardgivareId().getExtension();
    }

    public String getPatientId(RegisterCertificateType intyg) {
        return intyg.getIntyg().getPatient().getPersonId().getExtension();
    }

    public String getIntygId(RegisterCertificateType intyg) {
        return intyg.getIntyg().getIntygsId().getExtension();
    }

    public String getIntygtyp(RegisterCertificateType intyg) {
        return intyg.getIntyg().getTyp().getCode().trim();
    }

    public HSAKey extractHSAKey(RegisterCertificateType document) {
        String vardgivareId = getVardgivareId(document);
        String enhetId = getEnhetId(document);
        String lakareId = getLakareId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

    private String getFunktionsnedsattning(RegisterCertificateType intyg) {
        return getDelsvarString(intyg, FUNKTIONSNEDSATTNING_SVAR, FUNKTIONSNEDSATTNING_DELSVAR);
    }

    private String getAktivitetsbegransning(RegisterCertificateType intyg) {
        return getDelsvarString(intyg, AKTIVITETSBEGRANSNING_SVAR, AKTIVITETSBEGRANSNING_DELSVAR);
    }

    public LocalDateTime getSigneringsTidpunkt(RegisterCertificateType intyg) {
        return intyg.getIntyg().getSigneringstidpunkt();
    }

    @SuppressWarnings("squid:S134") //I can't see a better way to write this with fewer nested statements
    private String getDelsvarString(RegisterCertificateType intyg, String svarId, String delsvarId) {
        for (Svar svar : intyg.getIntyg().getSvar()) {
            if (svarId.equals(svar.getId())) {
                for (Svar.Delsvar delsvar : svar.getDelsvar()) {
                    if (delsvarId.equals(delsvar.getId())) {
                        for (Object content : delsvar.getContent()) {
                            if (content instanceof String) {
                                return (String) content;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("squid:S134") //I can't see a better way to write this with fewer nested statements
    public String getDx(RegisterCertificateType intyg) {
        for (Svar svar : intyg.getIntyg().getSvar()) {
            if (DIAGNOS_SVAR_ID_6.equals(svar.getId())) {
                for (Svar.Delsvar delsvar : svar.getDelsvar()) {
                    if (DIAGNOS_DELSVAR_ID_6.equals(delsvar.getId())) {
                        CVType diagnos = getCVSvarContent(delsvar);
                        return diagnos.getCode();
                    }
                }
            }
        }
        return null;
    }

    private Arbetsnedsattning getArbetsnedsattning(Svar svar) {
        int nedsattning = -1;
        DatePeriodType datePeriod = new DatePeriodType();

        for (Svar.Delsvar delsvar : svar.getDelsvar()) {
            switch (delsvar.getId()) {
                case BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32:
                    String sjukskrivningsnivaString = getCVSvarContent(delsvar).getCode();
                    final SjukskrivningsGrad sjukskrivningsGrad = SjukskrivningsGrad.valueOf(sjukskrivningsnivaString);
                    nedsattning = sjukskrivningsGrad.getNedsattning();
                    break;
                case BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32:
                    datePeriod = getDatePeriodTypeContent(delsvar);
                    break;
                default:
                    break;
            }
        }
        return new Arbetsnedsattning(nedsattning, datePeriod.getStart(), datePeriod.getEnd());
    }

    public Patientdata getPatientData(RegisterCertificateType intyg) {
        String patientIdRaw;
        try {
            patientIdRaw = getPatientId(intyg);
        } catch (Exception ignore) {
            patientIdRaw = "?Unknown?";
        }

        int alder = ConversionHelper.NO_AGE;
        Kon kon = Kon.UNKNOWN;
        try {
            final String personId = DocumentHelper.getUnifiedPersonId(patientIdRaw);
            try {
                final LocalDate sistaNedsattningsdag = getSistaNedsattningsdag(intyg);
                if (sistaNedsattningsdag != null) {
                    alder = ConversionHelper.extractAlder(personId, sistaNedsattningsdag);
                }
            } finally {
                kon = Kon.parse(ConversionHelper.extractKon(personId));
            }
        } catch (Exception e) {
            LOG.error("Could not extract age and/or gender: '{}'", patientIdRaw);
            LOG.debug("Could not extract age and/or gender: '{}'", patientIdRaw, e);
        }
        return new Patientdata(alder, kon);

    }

    private LocalDate getSistaNedsattningsdag(RegisterCertificateType document) {
        final List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(document);
        LocalDate to = null;
        for (Arbetsnedsattning arbetsnedsattning : arbetsnedsattnings) {
            final LocalDate candidate = arbetsnedsattning.getSlut();
            if (to == null || candidate.isAfter(to)) {
                to = candidate;
            }
        }
        return to;
    }

    public List<Arbetsnedsattning> getArbetsnedsattning(RegisterCertificateType intyg) {
        final ArrayList<Arbetsnedsattning> arbetsnedsattnings = new ArrayList<>();
        for (Svar svar : intyg.getIntyg().getSvar()) {
            if (BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32.equals(svar.getId())) {
                final Arbetsnedsattning arbetsnedsattning = getArbetsnedsattning(svar);
                arbetsnedsattnings.add(arbetsnedsattning);
            }
        }
        return arbetsnedsattnings;
    }

    synchronized public RegisterCertificateType unmarshalRegisterCertificateXml(String data) throws JAXBException {
        final StringReader reader = new StringReader(convertToV3(data));
        return (RegisterCertificateType) JAXBIntrospector.getValue(getUnmarshaller().unmarshal(reader));
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller == null) {
            jaxbUnmarshaller = JAXBContext.newInstance(RegisterCertificateType.class).createUnmarshaller();
            jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        }
        return jaxbUnmarshaller;
    }

    /**
     * Nothing that ST uses has been changed between v2 and v3 of the schema, therefore it should be enough to use
     * this simple transformation to make sure no v2 namespaces are still used.
     */
    public static String convertToV3(String data) {
        return data.
                replaceAll("urn:riv:clinicalprocess:healthcond:certificate:2",
                        "urn:riv:clinicalprocess:healthcond:certificate:3").
                replaceAll("urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2",
                        "urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3").
                replaceAll("urn:riv:clinicalprocess:healthcond:certificate:types:2",
                        "urn:riv:clinicalprocess:healthcond:certificate:types:3");
    }

    /**
     * Attempt to parse a CVType from a Delsvar.
     *
     * @param delsvar The Delsvar to parse.
     * @return CVType
     * @throws ConverterException
     */
    //This code is copied from intygsprojektet and is best to keep unchanged
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S134", "squid:UselessParenthesesCheck"})
    public CVType getCVSvarContent(Svar.Delsvar delsvar) {
        for (Object o : delsvar.getContent()) {
            if (o instanceof Node) {
                CVType cvType = new CVType();
                Node node = (Node) o;
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    if (Node.ELEMENT_NODE != list.item(i).getNodeType()) {
                        continue;
                    }
                    String textContent = list.item(i).getTextContent();
                    switch (list.item(i).getLocalName()) {
                        case "code":
                            cvType.setCode(textContent);
                            break;
                        case "codeSystem":
                            cvType.setCodeSystem(textContent);
                            break;
                        case "codeSystemVersion":
                            cvType.setCodeSystemVersion(textContent);
                            break;
                        case "codeSystemName":
                            cvType.setCodeSystemName(textContent);
                            break;
                        case "displayName":
                            cvType.setDisplayName(textContent);
                            break;
                        case "originalText":
                            cvType.setOriginalText(textContent);
                            break;
                        default:
                            LOG.debug("Unexpected element found while parsing CVType");
                            break;
                    }
                }
                if (cvType.getCode() == null || cvType.getCodeSystem() == null) {
                    throw new ConverterException("Error while converting CVType, missing mandatory field");
                }
                return cvType;
            } else if (o instanceof JAXBElement) {
                @SuppressWarnings("unchecked")
                JAXBElement<CVType> jaxbCvType = ((JAXBElement<CVType>) o);
                return jaxbCvType.getValue();
            }
        }
        throw new ConverterException("Unexpected outcome while converting CVType");
    }

    /**
     * Attempt to parse a {@link DatePeriodType} from a {@link Svar.Delsvar}.
     *
     * @param delsvar
     * @throws ConverterException
     */
    //This code is copied from intygsprojektet and is best to keep unchanged
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S134", "squid:UselessParenthesesCheck"})
    public DatePeriodType getDatePeriodTypeContent(Svar.Delsvar delsvar) {
        for (Object o : delsvar.getContent()) {
            if (o instanceof Node) {
                DatePeriodType datePeriodType = new DatePeriodType();
                Node node = (Node) o;
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    if (Node.ELEMENT_NODE != list.item(i).getNodeType()) {
                        continue;
                    }
                    String textContent = list.item(i).getTextContent();
                    switch (list.item(i).getLocalName()) {
                        case "start":
                            datePeriodType.setStart(LocalDate.parse(textContent));
                            break;
                        case "end":
                            datePeriodType.setEnd(LocalDate.parse(textContent));
                            break;
                        default:
                            LOG.debug("Unexpected element found while parsing DatePeriodType");
                            break;
                    }
                }
                if (datePeriodType.getStart() == null || datePeriodType.getEnd() == null) {
                    throw new ConverterException("Error while converting DatePeriodType, missing mandatory field");
                }
                return datePeriodType;
            } else if (o instanceof JAXBElement) {
                @SuppressWarnings("unchecked")
                JAXBElement<DatePeriodType> jaxbCvType = ((JAXBElement<DatePeriodType>) o);
                return jaxbCvType.getValue();
            }
        }
        throw new ConverterException("Unexpected outcome while converting DatePeriodType");
    }

    public IntygDTO convertToDTO(RegisterCertificateType intyg) {
        if (intyg == null) {
            return null;
        }

        IntygDTO dto = new IntygDTO();

        String enhet = getEnhetId(intyg);
        String patient = getPatientId(intyg);
        Patientdata patientData = getPatientData(intyg);

        String diagnos = getDx(intyg);
        String lakareid = getLakareId(intyg);
        String intygsId = getIntygId(intyg);
        String intygTyp = getIntygtyp(intyg);
        List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(intyg);
        LocalDate signeringsDatum = getSigneringsTidpunkt(intyg).toLocalDate();

        dto.setEnhet(enhet);
        dto.setDiagnoskod(diagnos);
        dto.setIntygid(intygsId);
        dto.setIntygtyp(intygTyp);
        dto.setLakareId(lakareid);
        dto.setPatientid(patient);
        dto.setPatientData(patientData);
        dto.setSigneringsdatum(signeringsDatum);
        dto.setArbetsnedsattnings(arbetsnedsattnings);

        return dto;
    }

}
