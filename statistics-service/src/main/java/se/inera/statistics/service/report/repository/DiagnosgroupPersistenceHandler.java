package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

public class DiagnosgroupPersistenceHandler implements DiagnosisGroups {
    private static final List<DiagnosisGroup> HEADERS = DiagnosisGroupsUtil.getAllDiagnosisGroups();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static final Locale SWEDEN = new Locale("SV", "se");
    private DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("MMM yyyy").withLocale(SWEDEN);
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @Transactional
    public void count(String period, String diagnosgrupp, Sex sex) {
        DiagnosisGroupData existingRow = manager.find(DiagnosisGroupData.class, new DiagnosisGroupData.DiagnosisGroupKey(period, "nationell", diagnosgrupp));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            DiagnosisGroupData row = new DiagnosisGroupData(period, "nationell", diagnosgrupp, female, male);
            manager.persist(row);
        } else {
            existingRow.setFemale(existingRow.getFemale() + female);
            existingRow.setMale(existingRow.getMale() + male);
            manager.merge(existingRow);
        }
    }

    @Override
    @Transactional
    public DiagnosisGroupResponse getDiagnosisGroups(LocalDate from, LocalDate to) {
        TypedQuery<DiagnosisGroupData> query = manager.createQuery("SELECT c FROM DiagnosisGroupData c WHERE c.diagnosisGroupKey.hsaId = :hsaId AND c.diagnosisGroupKey.period >= :from AND c.diagnosisGroupKey.period <= :to ORDER BY c.diagnosisGroupKey.period, c.diagnosisGroupKey.diagnosgrupp", DiagnosisGroupData.class);
        query.setParameter("hsaId", "nationell");
        query.setParameter("from", inputFormatter.print(from));
        query.setParameter("to", inputFormatter.print(to));

        return new DiagnosisGroupResponse(HEADERS, translateForOutput(from, to, query.getResultList()));
    }

    private List<DiagnosisGroupRow> translateForOutput(LocalDate from, LocalDate to, List<DiagnosisGroupData> list) {
        List<DiagnosisGroupRow> translatedCasesPerMonthRows = new ArrayList<>();

        // Span all
        DataHolder holder = new DataHolder(list);
        for (LocalDate currentPeriod = from; !currentPeriod.isAfter(to); currentPeriod = currentPeriod.plusMonths(1)) {
            String displayDate = outputFormatter.print(currentPeriod);
            String period = inputFormatter.print(currentPeriod);
            List<DualSexField> values = new ArrayList<>(HEADERS.size());
            for (DiagnosisGroup group: HEADERS) {
                DiagnosisGroupData data = holder.getData(period, group.getId());
                values.add(new DualSexField(data.getFemale(), data.getMale()));
            }
            translatedCasesPerMonthRows.add(new DiagnosisGroupRow(displayDate, values));
        }
        return translatedCasesPerMonthRows;
    }

    private static final class DataHolder {
        private final Iterator<DiagnosisGroupData> iterator;
        private DiagnosisGroupData currentData;
        private static final DiagnosisGroupData ZERO = new DiagnosisGroupData("", "", "", 0, 0); 

        public DataHolder(List<DiagnosisGroupData> list) {
            iterator = list.iterator();
            currentData = iterator.hasNext() ? iterator.next() : ZERO;
        }

        private DiagnosisGroupData next() {
            DiagnosisGroupData prev = currentData;
            currentData = iterator.hasNext() ? iterator.next() : ZERO;
            return prev;
        }

        public DiagnosisGroupData getData(String period, String group) {
            if (period.equals(currentData.getPeriod()) && group.equals(currentData.getGroup())) {
                return next();
            } else {
                return ZERO;
            }
        }
    }
}
