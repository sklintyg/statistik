package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class WidelineConverter {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineConverter.class);
    private static final LocalDate ERA = new LocalDate("2000-01-01");
    public static final int QUARTER = 25;
    public static final int HALF = 50;
    public static final int THREE_QUARTER = 75;
    public static final int FULL = 100;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Icd10 icd10;

    @Transactional
    public void accept(JsonNode intyg, JsonNode hsa, long logId) {
        WideLine line = new WideLine();

        List<String> errors = new ArrayList<>();

        String lkf = getLkf(hsa);
        checkField(errors, lkf, "LKF");

        String enhet = DocumentHelper.getEnhetId(intyg);
        checkField(errors, enhet, "Enhet");

        String patient = DocumentHelper.getPersonId(intyg);
        checkField(errors, patient, "Patient");

        int kon = DocumentHelper.getKon(intyg).indexOf('k');
        int alder = DocumentHelper.getAge(intyg);

        LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(intyg));
        LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(intyg));

        Kategori kategori = icd10.getKategori(icd10.normalize(DocumentHelper.getDiagnos(intyg)));
        checkField(errors, kategori.getId(), "Diagnoskategori");

        String diagnoskapitel = kategori.getAvsnitt().getKapitel().getId();
        String diagnosavsnitt = kategori.getAvsnitt().getId();
        String diagnoskategori = kategori.getId();

        int sjukskrivningsgrad = 100 - Integer.parseInt(DocumentHelper.getArbetsformaga(intyg).get(0));
        checkSjukskrivningsgrad(errors, sjukskrivningsgrad);

        int lakarkon = HSAServiceHelper.getLakarkon(hsa);
        int lakaralder = HSAServiceHelper.getLakaralder(hsa);
        String lakarbefattning = HSAServiceHelper.getLakarbefattning(hsa);

        line.setLakarintyg(logId);
        line.setLkf(lkf);
        line.setEnhet(enhet);

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

        if (errors.isEmpty()) {
            manager.persist(line);
        } else {
            StringBuilder errorBuilder = new StringBuilder("Faulty line detected: ");
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            errorBuilder.append('\n').append('\n').append("for intyg: ").append('\n');
            LOG.error(errorBuilder.toString());
        }
    }

    private void checkSjukskrivningsgrad(List<String> errors, int grad) {
        if (!(grad == 0 || grad == QUARTER || grad == HALF || grad == THREE_QUARTER || grad == FULL)) {
            errors.add("Illegal sjukskrivningsgrad: " + grad);
        }
    }

    private void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    @Transactional
    public void saveWideline(WideLine line) {
        manager.persist(line);
    }

    private String getLkf(JsonNode hsa) {
        String lkf = HSAServiceHelper.getKommun(hsa);
        if (lkf.isEmpty()) {
            lkf = HSAServiceHelper.getLan(hsa);
        }
        return lkf;
    }

    private int toDay(LocalDate dayDate) {
        return Days.daysBetween(ERA, dayDate).getDays();
    }

    public int count() {
        return manager.createQuery("SELECT wl FROM WideLine wl").getResultList().size();
    }
}
