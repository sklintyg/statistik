package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnosgroupPersistenceHandler implements DiagnosisGroups {
    private static final List<DiagnosisGroup> HEADERS = DiagnosisGroupsUtil.getAllDiagnosisGroups();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final Locale SWEDEN = new Locale("SV", "se");
    private DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("MMM yyyy").withLocale(SWEDEN);
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @Transactional
    public void count(String hsaId, String period, String diagnosgrupp, Verksamhet typ, Sex sex) {
        DiagnosisGroupData existingRow = manager.find(DiagnosisGroupData.class, new DiagnosisGroupData.DiagnosisGroupKey(period, hsaId, diagnosgrupp));
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
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range) {
        TypedQuery<DiagnosisGroupData> query = manager.createQuery("SELECT c FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to", DiagnosisGroupData.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", inputFormatter.print(range.getFrom()));
        query.setParameter("to", inputFormatter.print(range.getTo()));

        return new DiagnosisGroupResponse(HEADERS, translateForOutput(range, query.getResultList()));
    }

    private List<DiagnosisGroupRow> translateForOutput(Range range, List<DiagnosisGroupData> list) {
        List<DiagnosisGroupRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, DualSexField> map = map(list);

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = outputFormatter.print(currentPeriod);
            String period = inputFormatter.print(currentPeriod);
            List<DualSexField> values = new ArrayList<>(HEADERS.size());
            for (DiagnosisGroup group: HEADERS) {
                values.add(map.get(period + group.getId()));
            }
            translatedCasesPerMonthRows.add(new DiagnosisGroupRow(displayDate, values));
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
