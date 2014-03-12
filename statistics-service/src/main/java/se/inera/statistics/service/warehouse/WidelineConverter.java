package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class WidelineConverter {

    private static final LocalDate ERA = new LocalDate("2000-01-01");

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void accept(SjukfallInfo sjukfallInfo, JsonNode intyg, JsonNode hsa, long logId) {
        WideLine line = new WideLine();

        String lkf = getLkf(hsa);
        String enhet = DocumentHelper.getEnhetId(intyg);

        int lakarintyg = DocumentHelper.getLakarIntyg(intyg);

        String patient = DocumentHelper.getPersonId(intyg);
        int kon = DocumentHelper.getKon(intyg).indexOf('k');
        int alder = DocumentHelper.getAge(intyg);

        LocalDate kalenderStart = new LocalDate(DocumentHelper.getForstaNedsattningsdag(intyg));
        LocalDate kalenderEnd = new LocalDate(DocumentHelper.getSistaNedsattningsdag(intyg));

        String diagnoskapitel = DocumentHelper.getDiagnos(intyg);
        String diagnosavsnitt = DocumentHelper.getDiagnos(intyg);
        String diagnoskategori = DocumentHelper.getDiagnos(intyg);

        int sjukskrivningsgrad = 100 - Integer.parseInt(DocumentHelper.getArbetsformaga(intyg).get(0));

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

        line.setPatientid(patient);
        line.setAlder(alder);
        line.setKon(kon);

        line.setLakaralder(lakaralder);
        line.setLakarkon(lakarkon);
        line.setLakarbefattning(lakarbefattning);

        manager.persist(line);
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
