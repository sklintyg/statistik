package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthKey;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class CasesPerMonthPersistenceHandler implements CasesPerMonth {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String hsaId, String period, Verksamhet typ, Sex sex) {
        CasesPerMonthRow existingRow = manager.find(CasesPerMonthRow.class, new CasesPerMonthKey(period, hsaId), LockModeType.PESSIMISTIC_READ);
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            CasesPerMonthRow row = new CasesPerMonthRow(period, hsaId, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public SimpleDualSexResponse<SimpleDualSexDataRow> getCasesPerMonth(String hsaId, Range range) {
        TypedQuery<CasesPerMonthRow> query = manager.createQuery("SELECT c FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to", CasesPerMonthRow.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return translateForOutput(range, query.getResultList());
    }

    private SimpleDualSexResponse<SimpleDualSexDataRow> translateForOutput(Range range, List<CasesPerMonthRow> list) {
        List<SimpleDualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        Map<String, DualSexField> map = new DefaultHashMap<>(new DualSexField(0, 0));
        for (CasesPerMonthRow row: list) {
            map.put(row.getPeriod(), new DualSexField(row.getFemale(), row.getMale()));
        }

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            DualSexField dualSexField = map.get(period);
            translatedCasesPerMonthRows.add(new SimpleDualSexDataRow(displayDate, dualSexField.getFemale(), dualSexField.getMale()));
        }

        return new SimpleDualSexResponse<SimpleDualSexDataRow>(translatedCasesPerMonthRows, range.getMonths());
    }
}
