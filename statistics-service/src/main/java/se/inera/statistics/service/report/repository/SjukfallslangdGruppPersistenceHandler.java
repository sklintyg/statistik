package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthKey;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SickLeaveLengthRow;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppPersistenceHandler implements SjukfallslangdGrupp {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public SickLeaveLengthResponse getHistoricalStatistics(String hsaId, LocalDate when, RollingLength length) {
        TypedQuery<SickLeaveLengthRow> query = manager.createQuery("SELECT a FROM SickLeaveLengthRow a WHERE a.key.hsaId = :hsaId AND a.key.period = :when AND a.key.periods = :periods ", SickLeaveLengthRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("when", ReportUtil.toPeriod(when));
        query.setParameter("periods", length.getPeriods());

        return translateForOutput(query.getResultList(), length.getPeriods());
    }

    @Override
    public SickLeaveLengthResponse getCurrentStatistics(String hsaId) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getLongSickLeaves(String decodeId, Range range) {
        throw new RuntimeException("Not yet implemented");
    }

    private SickLeaveLengthResponse translateForOutput(List<SickLeaveLengthRow> list, int periods) {
        List<SickLeaveLengthRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (se.inera.statistics.service.report.util.Ranges.Range s: SjukfallslangdUtil.RANGES) {
            String group = s.getName();
            for (SickLeaveLengthRow r: list) {
                if (group.equals(r.getGroup())) {
                    translatedCasesPerMonthRows.add(r);
                }
            }
        }

        return new SickLeaveLengthResponse(translatedCasesPerMonthRows, periods);
    }

    @Transactional
    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Sex sex) {
        SickLeaveLengthRow existingRow = manager.find(SickLeaveLengthRow.class, new SickLeaveLengthKey(period, hsaId, group, length.getPeriods()), LockModeType.PESSIMISTIC_READ);
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            SickLeaveLengthRow row = new SickLeaveLengthRow(period, hsaId, group, length.getPeriods(), typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

}
