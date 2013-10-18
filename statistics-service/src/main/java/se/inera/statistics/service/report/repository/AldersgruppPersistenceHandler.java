package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.listener.AldersGruppListener;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.AldersgruppKey;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public class AldersgruppPersistenceHandler implements AgeGroups {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    @Override
    @Transactional
    public AgeGroupsResponse getAgeGroups(String hsaId, Range range) {
        TypedQuery<AgeGroupsRow> query = manager.createQuery("SELECT c FROM AgeGroupsRow c WHERE c.aldersgruppKey.hsaId = :hsaId AND c.aldersgruppKey.period >= :from AND c.aldersgruppKey.period <= :to", AgeGroupsRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", INPUT_FORMATTER.print(range.getFrom()));
        query.setParameter("to", INPUT_FORMATTER.print(range.getTo()));

        return translateForOutput(range, query.getResultList());
    }

    private AgeGroupsResponse translateForOutput(Range range, List<AgeGroupsRow> list) {
        List<AgeGroupsRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (AldersGruppListener.GroupTableRow s: AldersGruppListener.GROUPS) {
            int female = 0;
            int male = 0;
            String group = s.getGroupName();
            for (AgeGroupsRow r: list) {
                if (group.equals(r.getGroup())) {
                    female += r.getFemale();
                    male += r.getMale();
                }
            }
            translatedCasesPerMonthRows.add(new AgeGroupsRow(null, group, female, male));
        }

        return new AgeGroupsResponse(translatedCasesPerMonthRows, range.getMonths());
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, Verksamhet typ, Sex sex) {
        AgeGroupsRow existingRow = manager.find(AgeGroupsRow.class, new AldersgruppKey(period, hsaId, group));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            AgeGroupsRow row = new AgeGroupsRow(period, hsaId, group, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }
}
