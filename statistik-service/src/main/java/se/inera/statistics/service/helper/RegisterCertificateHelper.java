/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.apache.neethi.builders.converters.ConverterException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.inera.statistics.service.processlog.Arbetsnedsattning;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v2.Svar;

@Component
public class RegisterCertificateHelper {

    public static final String DIAGNOS_SVAR_ID_6 = "6";
    public static final String DIAGNOS_DELSVAR_ID_6 = "6.2";
    public static final String BEHOV_AV_SJUKSKRIVNING_SVAR_ID_32 = "32";
    public static final String BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32 = "32.1";
    public static final String BEHOV_AV_SJUKSKRIVNING_PERIOD_DELSVARSVAR_ID_32 = "32.2";

    private static final Logger LOG = LoggerFactory.getLogger(RegisterCertificateHelper.class);

    public RegisterCertificateHelper() {
    }

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
        return intyg.getIntyg().getTyp().getCode();
    }

    public boolean isEnkeltIntyg(RegisterCertificateType intyg) {
        final String code = intyg.getIntyg().getTyp().getCode();
        return IntygType.LIS.name().equalsIgnoreCase(code);
    }

    private String getIntygsTyp(RegisterCertificateType certificateType) {
        return certificateType.getIntyg().getTyp().getCode().toLowerCase();
    }

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

    private Arbetsnedsattning getArbetsnedsattning(Svar svar) throws ConverterException {
        int nedsattning = -1;
        DatePeriodType datePeriod = new DatePeriodType();

        for (Svar.Delsvar delsvar : svar.getDelsvar()) {
            switch (delsvar.getId()) {
            case BEHOV_AV_SJUKSKRIVNING_NIVA_DELSVARSVAR_ID_32:
                String sjukskrivningsnivaString = getCVSvarContent(delsvar).getCode();
                final SjukskrivningsGrad sjukskrivningsGrad = SjukskrivningsGrad.fromId(Integer.parseInt(sjukskrivningsnivaString));
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
        final String patientIdRaw = getPatientId(intyg);
        final String personId = DocumentHelper.getUnifiedPersonId(patientIdRaw);
        int alder;
        try {
            alder = ConversionHelper.extractAlder(personId, getSistaNedsattningsdag(intyg));
        } catch (Exception e) {
            LOG.error("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: {}", personId);
            alder = ConversionHelper.NO_AGE;
        }
        String kon = ConversionHelper.extractKon(personId);

        return new Patientdata(alder, kon);
    }

    public LocalDate getSistaNedsattningsdag(RegisterCertificateType document) {
        final List<Arbetsnedsattning> arbetsnedsattnings = getArbetsnedsattning(document);
        final int startYear = 2000;
        LocalDate date = new LocalDate(startYear, 1, 1);
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

    private enum SjukskrivningsGrad {
        /**
         * Helt nedsatt (id 1).
         */
        HELT_NEDSATT(1, "Helt nedsatt", 100),
        /**
         * Nedsatt till 3/4 (id 2).
         */
        NEDSATT_3_4(2, "Nedsatt med 3/4", 75),
        /**
         * Nedsatt till hälften (id 3).
         */
        NEDSATT_HALFTEN(3, "Nedsatt med hälften", 50),
        /**
         * Nedsatt till 1/4 (id 4).
         */
        NEDSATT_1_4(4, "Nedsatt med 1/4", 25);

        private final int id;
        private final String label;
        private final int nedsattning;

        SjukskrivningsGrad(int id, String label, int nedsattning) {
            this.id = id;
            this.label = label;
            this.nedsattning = nedsattning;
        }

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public int getNedsattning() {
            return nedsattning;
        }

        public static SjukskrivningsGrad fromId(int id) {
            for (SjukskrivningsGrad typ : values()) {
                if (typ.id == id) {
                    return typ;
                }
            }
            throw new IllegalArgumentException();
        }

    }

    public RegisterCertificateType unmarshalRegisterCertificateXml(String data) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(RegisterCertificateType.class).createUnmarshaller();
        jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        final StringReader reader = new StringReader(data);
        RegisterCertificateType unmarshal = (RegisterCertificateType) JAXBIntrospector.getValue(jaxbUnmarshaller.unmarshal(reader));
        return (RegisterCertificateType) unmarshal;
    }

    /**
     * Attempt to parse a CVType from a Delsvar.
     *
     * @param delsvar The Delsvar to parse.
     * @return CVType
     * @throws ConverterException
     */
    public CVType getCVSvarContent(Svar.Delsvar delsvar) throws ConverterException {
        for (Object o : delsvar.getContent()) {
            if (o instanceof Node) {
                CVType cvType = new CVType();
                Node node = (Node) o;
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    String textContent = list.item(i).getTextContent();
                    switch (list.item(i).getNodeName()) {
                    case "ns3:code":
                        cvType.setCode(textContent);
                        break;
                    case "ns3:codeSystem":
                        cvType.setCodeSystem(textContent);
                        break;
                    case "ns3:codeSystemVersion":
                        cvType.setCodeSystemVersion(textContent);
                        break;
                    case "ns3:codeSystemName":
                        cvType.setCodeSystemName(textContent);
                        break;
                    case "ns3:displayName":
                        cvType.setDisplayName(textContent);
                        break;
                    case "ns3:originalText":
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
    public DatePeriodType getDatePeriodTypeContent(Svar.Delsvar delsvar) throws ConverterException {
        for (Object o : delsvar.getContent()) {
            if (o instanceof Node) {
                DatePeriodType datePeriodType = new DatePeriodType();
                Node node = (Node) o;
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    String textContent = list.item(i).getTextContent();
                    switch (list.item(i).getNodeName()) {
                    case "ns3:start":
                        datePeriodType.setStart(new LocalDate(textContent));
                        break;
                    case "ns3:end":
                        datePeriodType.setEnd(new LocalDate(textContent));
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

}
