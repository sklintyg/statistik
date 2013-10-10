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

import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

public class DiagnossubgroupPersistenceHandler implements DiagnosisSubGroups {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final Locale SWEDEN = new Locale("SV", "se");
    private DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("MMM yyyy").withLocale(SWEDEN);
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @Transactional
    public void count(String period, String diagnosgrupp, String undergrupp, Sex sex) {
        DiagnosisSubGroupData existingRow = manager.find(DiagnosisSubGroupData.class, new DiagnosisSubGroupData.Key(period, "nationell", diagnosgrupp, undergrupp));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            DiagnosisSubGroupData row = new DiagnosisSubGroupData(period, "nationell", diagnosgrupp, undergrupp, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public DiagnosisGroupResponse getDiagnosisGroups(LocalDate from, LocalDate to, String group) {
        TypedQuery<DiagnosisSubGroupData> query = manager.createQuery("SELECT c FROM DiagnosisSubGroupData c WHERE c.diagnosisGroupKey.hsaId = :hsaId AND c.diagnosisGroupKey.diagnosgrupp = :group and c.diagnosisGroupKey.period >= :from AND c.diagnosisGroupKey.period <= :to", DiagnosisSubGroupData.class);
        query.setParameter("hsaId", "nationell");
        query.setParameter("group", group);
        query.setParameter("from", inputFormatter.print(from));
        query.setParameter("to", inputFormatter.print(to));

        System.err.println("Search " + group);
        List<DiagnosisGroup> header = DiagnosisGroupsUtil.getSubGroups(group);
        return new DiagnosisGroupResponse(header, translateForOutput(from, to, header, query.getResultList()));
    }

    private List<DiagnosisGroupRow> translateForOutput(LocalDate from, LocalDate to, List<DiagnosisGroup> header, List<DiagnosisSubGroupData> list) {
        List<DiagnosisGroupRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, DualSexField> map = map(list);

        for (LocalDate currentPeriod = from; !currentPeriod.isAfter(to); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = outputFormatter.print(currentPeriod);
            String period = inputFormatter.print(currentPeriod);
            List<DualSexField> values = new ArrayList<>(header.size());
            for (DiagnosisGroup group: header) {
                values.add(map.get(period + group.getId()));
            }
            translatedCasesPerMonthRows.add(new DiagnosisGroupRow(displayDate, values));
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
