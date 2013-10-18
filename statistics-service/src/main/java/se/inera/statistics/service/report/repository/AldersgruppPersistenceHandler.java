package se.inera.statistics.service.report.repository;

import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.AldersgruppKey;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AldersgruppPersistenceHandler implements AgeGroups {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    public AgeGroupsResponse getAgeGroups(String hsaId, Range range) {
        return null;
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, Sex sex) {
        AgeGroupsRow existingRow = manager.find(AgeGroupsRow.class, new AldersgruppKey(period, hsaId, group));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            CasesPerMonthRow row = new CasesPerMonthRow(period, hsaId, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }
}
