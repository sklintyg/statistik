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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.neethi.builders.converters.ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.inera.statistics.service.processlog.Arbetsnedsattning;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Svar;

@Component
public class RegisterCertificateHelper extends IntygHelper<RegisterCertificateType> {

    public static final String DIAGNOS_SVAR_ID_6 = "6";
    public static final String DIAGNOS_DELSVAR_ID_6 = "6.2";
    public static final String BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32 = "32";
    public static final String BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32 = "32.1";
    public static final String BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32 = "32.2";

    private static final Logger LOG = LoggerFactory.getLogger(RegisterCertificateHelper.class);

    @Override
    protected Class<RegisterCertificateType> getIntygClass() {
        return RegisterCertificateType.class;
    }

    @Override
    public synchronized RegisterCertificateType unmarshalXml(String data) throws JAXBException {
        String convertedData = convertToV3(data);

        return super.unmarshalXml(convertedData);
    }

    @Override
    public String getEnhetId(RegisterCertificateType utlatande) {
        return utlatande.getIntyg().getSkapadAv().getEnhet().getEnhetsId().getExtension();
    }

    @Override
    public String getLakareId(RegisterCertificateType document) {
        return document.getIntyg().getSkapadAv().getPersonalId().getExtension();
    }

    @Override
    public String getVardgivareId(RegisterCertificateType document) {
        return document.getIntyg().getSkapadAv().getEnhet().getVardgivare().getVardgivareId().getExtension();
    }

    @Override
    public String getPatientId(RegisterCertificateType intyg) {
        return intyg.getIntyg().getPatient().getPersonId().getExtension();
    }

    @Override
    public String getIntygId(RegisterCertificateType intyg) {
        return intyg.getIntyg().getIntygsId().getExtension();
    }

    @Override
    public IntygType getIntygtyp(RegisterCertificateType intyg) {
        return IntygType.getByItIntygType(intyg.getIntyg().getTyp().getCode().trim());
    }

    @Override
    public LocalDateTime getSigneringsTidpunkt(RegisterCertificateType intyg) {
        return intyg.getIntyg().getSigneringstidpunkt();
    }

    @Override
    public LocalDate getDateForPatientAge(RegisterCertificateType intyg) {
        return getSistaNedsattningsdag(intyg);
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

    @Override
    public IntygDTO convertToDTO(RegisterCertificateType intyg) {
        IntygDTO dto = super.convertToDTO(intyg);

        if (dto == null) {
            return null;
        }

        String diagnos = getDx(intyg);
        List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(intyg);

        dto.setDiagnoskod(diagnos);
        dto.setArbetsnedsattnings(arbetsnedsattnings);

        return dto;
    }

}
