package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
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

    @Autowired
    private Icd10 icd10;

    public WideLine toWideline(JsonNode intyg, JsonNode hsa, long logId) {
        WideLine line = new WideLine();

        String lkf = getLkf(hsa);

        String enhet = DocumentHelper.getEnhetId(intyg);
        String vardgivare = DocumentHelper.getVardgivareId(intyg);

        String patient = DocumentHelper.getPersonId(intyg);

        int kon = DocumentHelper.getKon(intyg).indexOf('k');
        int alder = DocumentHelper.getAge(intyg);

        LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(intyg));
        LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(intyg));

        Kategori kategori = icd10.findKategori(DocumentHelper.getDiagnos(intyg));

        String diagnoskapitel;
        String diagnosavsnitt;
        String diagnoskategori;
        if (kategori != null) {
            diagnoskapitel = kategori.getAvsnitt().getKapitel().getId();
            diagnosavsnitt = kategori.getAvsnitt().getId();
            diagnoskategori = kategori.getId();
        } else {
            diagnoskapitel = null;
            diagnosavsnitt = null;
            diagnoskategori = null;
        }

        int sjukskrivningsgrad = 100 - DocumentHelper.getArbetsformaga(intyg).get(0);

        int lakarkon = HSAServiceHelper.getLakarkon(hsa);
        int lakaralder = HSAServiceHelper.getLakaralder(hsa);
        String lakarbefattning = HSAServiceHelper.getLakarbefattning(hsa);

        line.setLakarintyg(logId);
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
        return line;
    }

    private void checkSjukskrivningsgrad(List<String> errors, int grad) {
        if (!(grad == QUARTER || grad == HALF || grad == THREE_QUARTER || grad == FULL)) {
            errors.add("Illegal sjukskrivningsgrad: " + grad);
        }
    }

    private void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    private String getLkf(JsonNode hsa) {
        String lkf = HSAServiceHelper.getKommun(hsa);
        if (lkf.isEmpty()) {
            lkf = HSAServiceHelper.getLan(hsa);
        }
        return lkf;
    }

    public static int toDay(LocalDate dayDate) {
        return Days.daysBetween(ERA, dayDate).getDays();
    }

    public List<String> validate(WideLine line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getLkf(), "LKF");
        checkField(errors, line.getEnhet(), "Enhet");
        checkField(errors, line.getPatientid(), "Patient");
        checkField(errors, line.getDiagnoskategori(), "Diagnoskategori");
        checkSjukskrivningsgrad(errors, line.getSjukskrivningsgrad());
        return errors;
    }
}
