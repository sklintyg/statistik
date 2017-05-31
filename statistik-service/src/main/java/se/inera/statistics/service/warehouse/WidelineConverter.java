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
package se.inera.statistics.service.warehouse;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.Arbetsnedsattning;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WidelineConverter {

    private static final LocalDate ERA = LocalDate.parse("2000-01-01");
    public static final int QUARTER = 25;
    public static final int HALF = 50;
    public static final int THREE_QUARTER = 75;
    public static final int FULL = 100;
    public static final int MAX_LENGTH_VGID = 50;
    public static final int MAX_LENGTH_ENHETNAME = 255;
    public static final int MAX_LENGTH_VARDGIVARE_NAMN = 255;
    public static final int MAX_LENGTH_LAN_ID = 50;
    public static final int MAX_LENGTH_KOMMUN_ID = 4;
    public static final int MAX_LENGTH_VERKSAMHET_TYP = Integer.MAX_VALUE;
    public static final int MAX_LENGTH_LAKARE_ID = 128;
    public static final int MAX_LENGTH_TILLTALSNAMN = 128;
    public static final int MAX_LENGTH_EFTERNAMN = 128;
    private static final int DATE20100101 = 3653; // 365*10 + 3
    private static final int MAX_YEARS_INTO_FUTURE = 5;
    private static final int MAX_LENGTH_CORRELATION_ID = 50;
    public static final String UNKNOWN_DIAGNOS = "unknown";

    @Autowired
    private Icd10 icd10;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    @Autowired
    private Clock clock;

    public List<WideLine> toWideline(JsonNode intyg, Patientdata patientData, HsaInfo hsa, long logId, String correlationId, EventType type) {
        String lkf = getLkf(hsa);

        String enhet = HSAServiceHelper.getEnhetId(hsa);
        HsaIdVardgivare vardgivare = HSAServiceHelper.getVardgivarId(hsa);

        final DocumentHelper.IntygVersion version = DocumentHelper.getIntygVersion(intyg);

        if (enhet == null) {
            enhet = DocumentHelper.getEnhetId(intyg, version);
        }

        String patient = DocumentHelper.getPersonId(intyg, version);

        int kon = patientData.getKon().getNumberRepresentation();
        int alder = patientData.getAlder();

        final boolean enkeltIntyg = DocumentHelper.isEnkeltIntyg(intyg, version);

        String diagnos = DocumentHelper.getDiagnos(intyg, version);
        final Diagnos dx = parseDiagnos(diagnos);

        int lakarkon = HSAServiceHelper.getLakarkon(hsa);
        int lakaralder = HSAServiceHelper.getLakaralder(hsa);
        String lakarbefattning = HSAServiceHelper.getLakarbefattning(hsa);
        HsaIdLakare lakareid = new HsaIdLakare(DocumentHelper.getLakarId(intyg, version));

        List<WideLine> lines = new ArrayList<>();

        for (Arbetsnedsattning arbetsnedsattning : DocumentHelper.getArbetsnedsattning(intyg, version)) {
            WideLine line = createWideLine(logId, correlationId, type, lkf, enhet, vardgivare, patient, kon, alder, enkeltIntyg, dx, lakarkon, lakaralder, lakarbefattning, lakareid, arbetsnedsattning);
            lines.add(line);
        }
        return lines;
    }

    public List<WideLine> toWideline(RegisterCertificateType intyg, Patientdata patientData, HsaInfo hsa, long logId, String correlationId, EventType type) {
        String lkf = getLkf(hsa);

        String enhet = HSAServiceHelper.getEnhetId(hsa);
        HsaIdVardgivare vardgivare = HSAServiceHelper.getVardgivarId(hsa);

        if (enhet == null) {
            enhet = registerCertificateHelper.getEnhetId(intyg);
        }

        String patient = registerCertificateHelper.getPatientId(intyg);

        int kon = patientData.getKon().getNumberRepresentation();
        int alder = patientData.getAlder();

        final boolean enkeltIntyg = registerCertificateHelper.isEnkeltIntyg(intyg);

        String diagnos = registerCertificateHelper.getDx(intyg);
        final Diagnos dx = parseDiagnos(diagnos);

        int lakarkon = HSAServiceHelper.getLakarkon(hsa);
        int lakaralder = HSAServiceHelper.getLakaralder(hsa);
        String lakarbefattning = HSAServiceHelper.getLakarbefattning(hsa);
        HsaIdLakare lakareid = new HsaIdLakare(registerCertificateHelper.getLakareId(intyg));

        List<WideLine> lines = new ArrayList<>();

        for (Arbetsnedsattning arbetsnedsattning : registerCertificateHelper.getArbetsnedsattning(intyg)) {
            WideLine line = createWideLine(logId, correlationId, type, lkf, enhet, vardgivare, patient, kon, alder, enkeltIntyg, dx, lakarkon, lakaralder, lakarbefattning, lakareid, arbetsnedsattning);
            lines.add(line);
        }
        return lines;
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    private WideLine createWideLine(long logId, String correlationId, EventType type, String lkf, String enhet, HsaIdVardgivare vardgivare, String patient, int kon, int alder, boolean enkeltIntyg, Diagnos dx, int lakarkon, int lakaralder, String lakarbefattning, HsaIdLakare lakareid, Arbetsnedsattning arbetsnedsattning) {
        WideLine line = new WideLine();

        int sjukskrivningsgrad = arbetsnedsattning.getNedsattning();

        LocalDate kalenderStart = arbetsnedsattning.getStart();
        LocalDate kalenderEnd = arbetsnedsattning.getSlut();

        line.setCorrelationId(correlationId);
        line.setLakarintyg(logId);
        line.setIntygTyp(type);
        line.setLkf(lkf);
        line.setEnhet(new HsaIdEnhet(enhet));
        line.setVardgivareId(vardgivare);

        line.setStartdatum(toDay(kalenderStart));
        line.setSlutdatum(toDay(kalenderEnd));
        line.setDiagnoskapitel(dx.diagnoskapitel);
        line.setDiagnosavsnitt(dx.diagnosavsnitt);
        line.setDiagnoskategori(dx.diagnoskategori);
        line.setDiagnoskod(dx.diagnoskod);
        line.setSjukskrivningsgrad(sjukskrivningsgrad);
        line.setEnkelt(enkeltIntyg);

        line.setPatientid(patient);
        line.setAlder(alder);
        line.setKon(kon);

        line.setLakaralder(lakaralder);
        line.setLakarkon(lakarkon);
        line.setLakarbefattning(lakarbefattning);
        line.setLakareId(lakareid);
        return line;
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    private Diagnos parseDiagnos(String diagnos) {
        boolean isUnknownDiagnos = diagnos == null || UNKNOWN_DIAGNOS.equals(diagnos);
        final Icd10.Kod kod = isUnknownDiagnos ? null : icd10.findKod(diagnos);
        final Kategori kategori = kod != null ? kod.getKategori() : isUnknownDiagnos ? null : icd10.findKategori(diagnos);
        final Diagnos dx = new Diagnos();
        dx.diagnoskod = kod != null ? kod.getId() : null;

        if (kategori != null) {
            dx.diagnoskapitel = kategori.getAvsnitt().getKapitel().getId();
            dx.diagnosavsnitt = kategori.getAvsnitt().getId();
            dx.diagnoskategori = kategori.getId();
        } else if (UNKNOWN_DIAGNOS.equals(diagnos)) {
            dx.diagnoskapitel = null;
            dx.diagnosavsnitt = null;
            dx.diagnoskategori = UNKNOWN_DIAGNOS;
        } else {
            dx.diagnoskapitel = null;
            dx.diagnosavsnitt = null;
            dx.diagnoskategori = null;
        }
        return dx;
    }

    private void checkSjukskrivningsgrad(List<String> errors, int grad) {
        if (!(grad == QUARTER || grad == HALF || grad == THREE_QUARTER || grad == FULL)) {
            errors.add("Illegal sjukskrivningsgrad: " + grad);
        }
    }

    private void checkStartdatum(List<String> errors, int startdatum) {
        if (startdatum < DATE20100101 || startdatum > toDay(LocalDate.now(clock).plusYears(MAX_YEARS_INTO_FUTURE))) {
            errors.add("Illegal startdatum: " + startdatum);
        }
    }

    private void checkSlutdatum(List<String> errors, int slutdatum) {
        if (slutdatum > toDay(LocalDate.now(clock).plusYears(MAX_YEARS_INTO_FUTURE))) {
            errors.add("Illegal slutdatum: " + slutdatum);
        }
    }

    private void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    private void checkField(List<String> errors, HsaIdAny field, String fieldName) {
        if (field == null || field.getId().isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    private void checkField(List<String> errors, String field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    private void checkField(List<String> errors, HsaIdAny field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.getId().length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    private String getLkf(HsaInfo hsa) {
        String lkf = HSAServiceHelper.getKommun(hsa);
        if (lkf.isEmpty()) {
            lkf = HSAServiceHelper.getLan(hsa);
        } else if (lkf.length() == 2) {
            lkf = HSAServiceHelper.getLan(hsa) + lkf;
        }
        return lkf;
    }

    public static int toDay(LocalDate dayDate) {
        return (int) ChronoUnit.DAYS.between(ERA, dayDate);
    }

    public static LocalDate toDate(int day) {
        return ERA.plusDays(day);
    }

    public List<String> validate(WideLine line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getLkf(), "LKF");
        checkField(errors, line.getVardgivareId(), "Vårdgivare", MAX_LENGTH_VGID);
        checkField(errors, line.getEnhet(), "Enhet");
        checkField(errors, line.getPatientid(), "Patient");
        checkAge(errors, line.getAlder());
        checkField(errors, line.getCorrelationId(), "CorrelationId", MAX_LENGTH_CORRELATION_ID);
        checkSjukskrivningsgrad(errors, line.getSjukskrivningsgrad());
        checkDates(errors, line.getStartdatum(), line.getSlutdatum());
        return errors;
    }

    private void checkDates(List<String> errors, int startdatum, int slutdatum) {
        checkStartdatum(errors, startdatum);
        checkSlutdatum(errors, slutdatum);
        if (startdatum > slutdatum) {
            errors.add("Illegal dates. Start date (" + startdatum + ") must be before end date (" + slutdatum + ")");
        }
    }

    private void checkAge(List<String> errors, int alder) {
        if (alder == ConversionHelper.NO_AGE) {
            errors.add("Error in patient age");
        }
    }

    private class Diagnos {
        private String diagnoskod;
        private String diagnoskapitel;
        private String diagnosavsnitt;
        private String diagnoskategori;
    }

}
