package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.AldersgruppKey;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppPersistenceHandler implements SjukfallslangdGrupp {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public SickLeaveLengthResponse getStatistics(String hsaId, LocalDate when, int periods) {
        TypedQuery<SickLeaveLengthRow> query = manager.createQuery("SELECT a FROM AgeGroupsRow a WHERE a.key.hsaId = :hsaId AND a.key.period = :when a.key.size = :size ", SickLeaveLengthRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("when", ReportUtil.toPeriod(when));
        query.setParameter("size", periods);

        return translateForOutput(query.getResultList(), periods);
    }

    private SickLeaveLengthResponse translateForOutput(List<SickLeaveLengthRow> list, int periods) {
        List<SickLeaveLengthRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (se.inera.statistics.service.report.util.SjukfallslangdUtil.Group s: SjukfallslangdUtil.GROUPS) {
            String group = s.getGroupName();
            for (SickLeaveLengthRow r: list) {
                if (group.equals(r.getName())) {
                    translatedCasesPerMonthRows.add(r);
                }
            }
        }

        return new SickLeaveLengthResponse(translatedCasesPerMonthRows, periods);
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, int periods, Verksamhet typ, Sex sex) {
        AgeGroupsRow existingRow = manager.find(AgeGroupsRow.class, new AldersgruppKey(period, hsaId, group, periods));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            AgeGroupsRow row = new AgeGroupsRow(period, hsaId, group, periods, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }
}
