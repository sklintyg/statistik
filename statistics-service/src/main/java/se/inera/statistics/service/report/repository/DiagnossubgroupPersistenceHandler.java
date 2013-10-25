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

import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnossubgroupPersistenceHandler implements DiagnosisSubGroups {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String hsaId, String period, String diagnosgrupp, String undergrupp, Verksamhet typ, Sex sex) {
        DiagnosisSubGroupData existingRow = manager.find(DiagnosisSubGroupData.class, new DiagnosisSubGroupData.Key(period, hsaId, diagnosgrupp, undergrupp), LockModeType.PESSIMISTIC_READ);
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            DiagnosisSubGroupData row = new DiagnosisSubGroupData(period, hsaId, diagnosgrupp, undergrupp, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range, String group) {
        TypedQuery<DiagnosisSubGroupData> query = manager.createQuery("SELECT c FROM DiagnosisSubGroupData c WHERE c.key.hsaId = :hsaId AND c.key.diagnosgrupp = :group and c.key.period BETWEEN :from AND :to", DiagnosisSubGroupData.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("group", group);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        List<DiagnosisGroup> header = DiagnosisGroupsUtil.getSubGroups(group);
        return new DiagnosisGroupResponse(header, translateForOutput(range, header, query.getResultList()));
    }

    private List<DualSexDataRow> translateForOutput(Range range, List<DiagnosisGroup> header, List<DiagnosisSubGroupData> list) {
        List<DualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, DualSexField> map = map(list);

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            List<DualSexField> values = new ArrayList<>(header.size());
            for (DiagnosisGroup group: header) {
                values.add(map.get(period + group.getId()));
            }
            translatedCasesPerMonthRows.add(new DualSexDataRow(displayDate, values));
        }
        return translatedCasesPerMonthRows;
    }

    private static Map<String, DualSexField> map(List<DiagnosisSubGroupData> list) {
        Map<String, DualSexField> resultMap = new DefaultHashMap<>(new DualSexField(0, 0));

        for (DiagnosisSubGroupData item: list) {
            resultMap.put(item.getPeriod() + item.getSubGroup(), new DualSexField(item.getFemale(), item.getMale()));
        }
        return resultMap;
    }

}
