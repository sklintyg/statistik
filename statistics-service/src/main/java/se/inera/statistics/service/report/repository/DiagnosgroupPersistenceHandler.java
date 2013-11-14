package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnosgroupPersistenceHandler implements DiagnosisGroups {
    private static final List<DiagnosisGroup> HEADERS = DiagnosisGroupsUtil.getAllDiagnosisGroups();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String hsaId, String period, String diagnosgrupp, Verksamhet typ, Sex sex) {
        DiagnosisGroupData existingRow = manager.find(DiagnosisGroupData.class, new DiagnosisGroupKey(period, hsaId, diagnosgrupp));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            DiagnosisGroupData row = new DiagnosisGroupData(period, hsaId, diagnosgrupp, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public void countAll(Map<DiagnosisGroupKey, SjukfallPerDiagnosgruppListener.DiagnosgruppValue> cache) {
        for (Map.Entry<DiagnosisGroupKey, SjukfallPerDiagnosgruppListener.DiagnosgruppValue> entry : cache.entrySet()) {
            count(entry.getKey(), entry.getValue());
        }
        cache.clear();
    }

    private void count(DiagnosisGroupKey key, SjukfallPerDiagnosgruppListener.DiagnosgruppValue value) {
        DiagnosisGroupData existingRow = manager.find(DiagnosisGroupData.class, key);

        if (existingRow == null) {
            DiagnosisGroupData row = new DiagnosisGroupData(key.getPeriod(), key.getHsaId(), key.getDiagnosgrupp(), value.getVerksamhet(), value.getFemale(), value.getMale());
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + value.getFemale());
            existingRow.setMale(existingRow.getMale() + value.getMale());
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range) {
        TypedQuery<DiagnosisGroupData> query = manager.createQuery("SELECT c FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to", DiagnosisGroupData.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return new DiagnosisGroupResponse(HEADERS, translateForOutput(range, query.getResultList()));
    }

    private List<DualSexDataRow> translateForOutput(Range range, List<DiagnosisGroupData> list) {
        List<DualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, DualSexField> map = map(list);

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            List<DualSexField> values = new ArrayList<>(HEADERS.size());
            for (DiagnosisGroup group: HEADERS) {
                values.add(map.get(period + group.getId()));
            }
            translatedCasesPerMonthRows.add(new DualSexDataRow(displayDate, values));
        }
        return translatedCasesPerMonthRows;
    }

    private static Map<String, DualSexField> map(List<DiagnosisGroupData> data) {
        Map<String, DualSexField> resultMap = new DefaultHashMap<>(new DualSexField(0, 0));

        for (DiagnosisGroupData item: data) {
            resultMap.put(item.getPeriod() + item.getGroup(), new DualSexField(item.getFemale(), item.getMale()));
        }
        return resultMap;
    }

}
