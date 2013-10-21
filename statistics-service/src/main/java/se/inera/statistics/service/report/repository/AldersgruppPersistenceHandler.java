package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.AldersgruppKey;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.AldersgroupUtil.Group;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class AldersgruppPersistenceHandler implements AgeGroups {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public AgeGroupsResponse getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rolling) {
        TypedQuery<AgeGroupsRow> query = manager.createQuery("SELECT a FROM AgeGroupsRow a WHERE a.key.hsaId = :hsaId AND a.key.period = :when a.key.periods = :periods ", AgeGroupsRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("when", ReportUtil.toPeriod(when));
        query.setParameter("periods", rolling.getPeriods());

        return translateForOutput(query.getResultList(), rolling.getPeriods());
    }
    

    @Override
    public AgeGroupsResponse getCurrentAgeGroups(String hsaId) {
        return getHistoricalAgeGroups(hsaId, new LocalDate().withDayOfMonth(1), RollingLength.SINGLE_MONTH);
    }

    private AgeGroupsResponse translateForOutput(List<AgeGroupsRow> list, int periods) {
        List<AgeGroupsRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (Group s: AldersgroupUtil.GROUPS) {
            String group = s.getGroupName();
            for (AgeGroupsRow r: list) {
                if (group.equals(r.getGroup())) {
                    translatedCasesPerMonthRows.add(r);
                }
            }
        }

        return new AgeGroupsResponse(translatedCasesPerMonthRows, periods);
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Sex sex) {
        AgeGroupsRow existingRow = manager.find(AgeGroupsRow.class, new AldersgruppKey(period, hsaId, group, length.getPeriods()));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            AgeGroupsRow row = new AgeGroupsRow(period, hsaId, group, length.getPeriods(), typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

}
