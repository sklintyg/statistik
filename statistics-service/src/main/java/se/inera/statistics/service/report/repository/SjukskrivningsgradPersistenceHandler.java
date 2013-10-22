package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukskrivningsgradPersistenceHandler implements DegreeOfSickLeave {

    private static List<String> HEADERS = Arrays.asList("Antal sjukfall per 25%", "Antal sjukfall per 50%", "Antal sjukfall per 75%", "Antal sjukfall per 100%");
    private static List<Integer> GRAD = Arrays.asList(25, 50, 75, 100);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    public void count(String hsaId, String period, int degree, Verksamhet typ, Sex sex) {
        SjukskrivningsgradData existingRow = manager.find(SjukskrivningsgradData.class, new SjukskrivningsgradKey(period, hsaId, degree));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            SjukskrivningsgradData row = new SjukskrivningsgradData(period, hsaId, degree, typ, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Transactional
    @Override
    public DegreeOfSickLeaveResponse getStatistics(String hsaId, Range range) {
        TypedQuery<SjukskrivningsgradData> query = manager.createQuery("SELECT c FROM SjukskrivningsgradData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to", SjukskrivningsgradData.class);
        query.setParameter("hsaId", hsaId);
        query.setParameter("from", ReportUtil.toPeriod(range.getFrom()));
        query.setParameter("to", ReportUtil.toPeriod(range.getTo()));

        return new DegreeOfSickLeaveResponse(HEADERS, translateForOutput(range, query.getResultList()));
    }

    private List<DualSexDataRow> translateForOutput(Range range, List<SjukskrivningsgradData> list) {
        List<DualSexDataRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        Map<String, DualSexField> map = map(list);

        for (LocalDate currentPeriod = range.getFrom(); !currentPeriod.isAfter(range.getTo()); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = ReportUtil.toDiagramPeriod(currentPeriod);
            String period = ReportUtil.toPeriod(currentPeriod);
            List<DualSexField> values = new ArrayList<>(HEADERS.size());
            for (Integer group: GRAD) {
                values.add(map.get(period + group));
            }
            translatedCasesPerMonthRows.add(new DualSexDataRow(displayDate, values));
        }
        return translatedCasesPerMonthRows;
    }

    private static Map<String, DualSexField> map(List<SjukskrivningsgradData> list) {
        Map<String, DualSexField> resultMap = new DefaultHashMap<>(new DualSexField(0, 0));

        for (SjukskrivningsgradData item: list) {
            resultMap.put(item.getPeriod() + item.getGrad(), new DualSexField(item.getFemale(), item.getMale()));
        }
        return resultMap;
    }
}
