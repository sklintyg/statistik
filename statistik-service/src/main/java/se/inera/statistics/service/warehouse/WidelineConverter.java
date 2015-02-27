/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import java.util.ArrayList;
import java.util.List;

@Component
public class WidelineConverter {

    private static final LocalDate ERA = new LocalDate("2000-01-01");
    public static final int QUARTER = 25;
    public static final int HALF = 50;
    public static final int THREE_QUARTER = 75;
    public static final int FULL = 100;
    public static final int MAX_SJUKSKRIVNING = 100;
    public static final int MAX_LENGTH_VGID = 50;
    public static final int MAX_LENGTH_ENHETNAME = 255;
    public static final int MAX_LENGTH_VARDGIVARE_NAMN = 255;
    public static final int MAX_LENGTH_LAN_ID = 50;
    public static final int MAX_LENGTH_KOMMUN_ID = 50;
    public static final int MAX_LENGTH_VERKSAMHET_TYP = Integer.MAX_VALUE;
    public static final int MAX_LENGTH_LAKARE_ID = 128;
    public static final int MAX_LENGTH_TILLTALSNAMN = 128;
    public static final int MAX_LENGTH_EFTERNAMN = 128;
    private static final int DATE20100101 = 3653; // 365*10 + 3
    private static final int MAX_YEARS_INTO_FUTURE = 5;

    @Autowired
    private Icd10 icd10;

    public List<WideLine> toWideline(JsonNode intyg, JsonNode hsa, long logId, String correlationId, EventType type) {
        String lkf = getLkf(hsa);

        String enhet = HSAServiceHelper.getEnhetId(hsa);
        String vardgivare = HSAServiceHelper.getVardgivarId(hsa);
        if (enhet == null) {
            enhet = DocumentHelper.getEnhetId(intyg);
        }

        String patient = DocumentHelper.getPersonId(intyg);

        int kon = Kon.valueOf(DocumentHelper.getKon(intyg)).getNumberRepresentation();
        int alder = DocumentHelper.getAge(intyg);

        String diagnos = DocumentHelper.getDiagnos(intyg);
        Kategori kategori = "unknown".equals(diagnos) ? null : icd10.findKategori(diagnos);

        String diagnoskapitel;
        String diagnosavsnitt;
        String diagnoskategori;
        if (kategori != null) {
            diagnoskapitel = kategori.getAvsnitt().getKapitel().getId();
            diagnosavsnitt = kategori.getAvsnitt().getId();
            diagnoskategori = kategori.getId();
        } else if ("unknown".equals(diagnos)) {
            diagnoskapitel = null;
            diagnosavsnitt = null;
            diagnoskategori = "unknown";
        } else {
            diagnoskapitel = null;
            diagnosavsnitt = null;
            diagnoskategori = null;
        }

        int lakarkon = HSAServiceHelper.getLakarkon(hsa);
        int lakaralder = HSAServiceHelper.getLakaralder(hsa);
        String lakarbefattning = HSAServiceHelper.getLakarbefattning(hsa);
        String lakareid = HSAServiceHelper.getLakareId(hsa);

        List<WideLine> lines = new ArrayList<>();

        for (JsonNode arbetsformaga : DocumentHelper.getArbetsformaga(intyg)) {

            WideLine line = new WideLine();

            int sjukskrivningsgrad = varde(arbetsformaga);

            LocalDate kalenderStart = new LocalDate(arbetsformaga.path("observationsperiod").path("from").asText());
            LocalDate kalenderEnd = new LocalDate(arbetsformaga.path("observationsperiod").path("tom").asText());

            line.setCorrelationId(correlationId);
            line.setLakarintyg(logId);
            line.setIntygTyp(type);
            line.setLkf(lkf);
            line.setEnhet(enhet);
            line.setVardgivareId(vardgivare);

            line.setStartdatum(toDay(kalenderStart));
            line.setSlutdatum(toDay(kalenderEnd));
            line.setDiagnoskapitel(diagnoskapitel);
            line.setDiagnosavsnitt(diagnosavsnitt);
            line.setDiagnoskategori(diagnoskategori);
            line.setSjukskrivningsgrad(sjukskrivningsgrad);

            line.setPatientid(patient);
            line.setAlder(alder);
            line.setKon(kon);

            line.setLakaralder(lakaralder);
            line.setLakarkon(lakarkon);
            line.setLakarbefattning(lakarbefattning);
            line.setLakareId(lakareid);
            lines.add(line);
        }
        return lines;
    }

    public int varde(JsonNode arbetsformaga) {
        for (JsonNode varde: arbetsformaga.path("varde")) {
            return MAX_SJUKSKRIVNING - varde.path("quantity").asInt();
        }
        return MAX_SJUKSKRIVNING;
    }

    private void checkSjukskrivningsgrad(List<String> errors, int grad) {
        if (!(grad == QUARTER || grad == HALF || grad == THREE_QUARTER || grad == FULL)) {
            errors.add("Illegal sjukskrivningsgrad: " + grad);
        }
    }

    private void checkStartdatum(List<String> errors, int startdatum) {
        if (startdatum < DATE20100101 || startdatum > toDay(LocalDate.now().plusYears(MAX_YEARS_INTO_FUTURE))) {
            errors.add("Illegal startdatum: " + startdatum);
        }
    }

    private void checkSlutdatum(List<String> errors, int slutdatum) {
        if (slutdatum > toDay(LocalDate.now().plusYears(MAX_YEARS_INTO_FUTURE))) {
            errors.add("Illegal slutdatum: " + slutdatum);
        }
    }

    private void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    private void checkField(List<String> errors, String field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    private String getLkf(JsonNode hsa) {
        String lkf = HSAServiceHelper.getKommun(hsa);
        if (lkf.isEmpty()) {
            lkf = HSAServiceHelper.getLan(hsa);
        } else if (lkf.length() == 2) {
            lkf = HSAServiceHelper.getLan(hsa) + lkf;
        }
        return lkf;
    }

    public static int toDay(LocalDate dayDate) {
        return Days.daysBetween(ERA, dayDate).getDays();
    }

    public List<String> validate(WideLine line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getLkf(), "LKF");
        checkField(errors, line.getVardgivareId(), "Vårdgivare", MAX_LENGTH_VGID);
        checkField(errors, line.getEnhet(), "Enhet");
        checkField(errors, line.getPatientid(), "Patient");
        checkField(errors, line.getLakareId(), "LäkarID");
        checkSjukskrivningsgrad(errors, line.getSjukskrivningsgrad());
        checkStartdatum(errors, line.getStartdatum());
        checkSlutdatum(errors, line.getSlutdatum());
        return errors;
    }
}
