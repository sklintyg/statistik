package se.inera.statistics.service.report.repository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.model.CasesPerMonthKey;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class CasesPerMonthPersistenceHandler {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String period, String enhet, Sex sex) {
        CasesPerMonthRow existingRow = manager.find(CasesPerMonthRow.class, new CasesPerMonthKey(period, enhet));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if(existingRow == null) {
            CasesPerMonthRow row = new CasesPerMonthRow(period, enhet, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }
}
