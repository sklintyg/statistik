package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.CasesPerCounty;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.model.db.SjukfallPerLanKey;
import se.inera.statistics.service.report.model.db.SjukfallPerLanRow;
import se.inera.statistics.service.report.util.ReportUtil;

public class SjukfallPerLanPersistenceHandler implements CasesPerCounty {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private Lan lans;

    @Override
    @Transactional
    public SimpleDualSexResponse<SimpleDualSexDataRow> getStatistics(Range range) {
        TypedQuery<SjukfallPerLanRow> query = manager.createQuery("SELECT new SjukfallPerLanRow(c.key.period, 'dummy', c.key.lanId, c.key.periods, SUM (c.female), sum (c.male)) FROM SjukfallPerLanRow c WHERE c.key.period = :to and c.key.periods = :periods GROUP BY c.key.period, c.key.lanId", SjukfallPerLanRow.class);
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));
        query.setParameter("periods", range.getMonths());

        return translateForOutput(range, query.getResultList());
    }

    @Override
    @Transactional
    public void count(String period, String enhetId, String lanId, RollingLength length, Sex kon) {
        SjukfallPerLanRow existingRow = manager.find(SjukfallPerLanRow.class, new SjukfallPerLanKey(period, enhetId, lanId, length.getPeriods()));
        int female = Sex.Female.equals(kon) ? 1 : 0;
        int male = Sex.Male.equals(kon) ? 1 : 0;

        if (existingRow == null) {
            SjukfallPerLanRow row = new SjukfallPerLanRow(period, enhetId, lanId, length.getPeriods(), female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale((int) (existingRow.getFemale() + female));
            existingRow.setMale((int) (existingRow.getMale() + male));
            manager.merge(existingRow);
        }
    }

    private SimpleDualSexResponse<SimpleDualSexDataRow> translateForOutput(Range range, List<SjukfallPerLanRow> list) {
        List<SimpleDualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        Map<String, DualSexField> map = new DefaultHashMap<>(new DualSexField(0, 0));
        for (SjukfallPerLanRow row: list) {
            map.put(row.getLanId(), new DualSexField((int) row.getFemale(), (int) row.getMale()));
        }

        for (String lanId : lans) {
            String displayLan = lans.getNamn(lanId);
            DualSexField dualSexField = map.get(lanId);
            translatedCasesPerMonthRows.add(new SimpleDualSexDataRow(displayLan, dualSexField.getFemale(), dualSexField.getMale()));
        }

        return new SimpleDualSexResponse<>(translatedCasesPerMonthRows, range.getMonths());
    }

}
