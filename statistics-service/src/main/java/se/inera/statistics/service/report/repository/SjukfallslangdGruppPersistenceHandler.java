package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.db.SickLeaveLengthKey;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.db.SickLeaveLengthRow;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukfallslangdGruppPersistenceHandler implements SjukfallslangdGrupp {
    private static final int LONG_SICKLEAVE_CUTOFF = 91;
    private static final int LONG_SICKLEAVE_DISPLAY_LENGTH = 18;

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
        return getHistoricalStatistics(hsaId, new LocalDate(), RollingLength.SINGLE_MONTH);
    }

    @Override
    public SimpleDualSexResponse<SimpleDualSexDataRow> getLongSickLeaves(String hsaId, Range range) {
        TypedQuery<SimpleDualSexDataRow> query = manager.createQuery("SELECT new se.inera.statistics.service.report.model.SimpleDualSexDataRow(r.key.period, SUM(r.female), SUM(r.male)) FROM SickLeaveLengthRow r WHERE r.key.periods = :periods AND r.key.hsaId = :hsaId AND r.key.period BETWEEN :from AND :to AND r.key.grupp IN :grupper group by r.key.period", SimpleDualSexDataRow.class);

        List<Ranges.Range> ranges = SjukfallslangdUtil.RANGES.lookupRangesLongerThan(LONG_SICKLEAVE_CUTOFF);
        List<String> names = new ArrayList<>(ranges.size());
        for (se.inera.statistics.service.report.util.Ranges.Range r: ranges) {
            names.add(r.getName());
        }
        query.setParameter("periods", 1);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        query.setParameter("grupper", names);

        List<SimpleDualSexDataRow> rows = query.getResultList();
        List<SimpleDualSexDataRow> sexRows = new ArrayList<>();
        LocalDate date = range.getFrom();
        while (!date.isAfter(range.getTo())) {
            String key = ReportUtil.toPeriod(date);
            SimpleDualSexDataRow row = lookupSimpleDualSexDataRow(key, rows);
            sexRows.add(row);
            date = date.plusMonths(1);
        }

        return new SimpleDualSexResponse<>(sexRows, LONG_SICKLEAVE_DISPLAY_LENGTH);
    }

    private SimpleDualSexDataRow lookupSimpleDualSexDataRow(String key, List<SimpleDualSexDataRow> rows) {
        for (SimpleDualSexDataRow row : rows) {
            if (key.equals(row.getName())) {
                return row;
            }
        }
        return new SimpleDualSexDataRow(key, 0, 0);
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
        SickLeaveLengthRow existingRow = manager.find(SickLeaveLengthRow.class, new SickLeaveLengthKey(period, hsaId, group, length.getPeriods()));
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
