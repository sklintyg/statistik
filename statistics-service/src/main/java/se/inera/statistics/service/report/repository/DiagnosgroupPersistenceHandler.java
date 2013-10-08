package se.inera.statistics.service.report.repository;

import java.util.ArrayList;
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
import se.inera.statistics.service.report.model.CasesPerMonthKey;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
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

    @Transactional
    public CasesPerMonthRow getCasesPerMonthRow(String period) {
        return manager.find(CasesPerMonthRow.class, new CasesPerMonthKey(period, "nationell"));
    }

    @Override
    @Transactional
    public DiagnosisGroupResponse getDiagnosisGroups(LocalDate from, LocalDate to) {
        TypedQuery<DiagnosisGroupData> query = manager.createQuery("SELECT c FROM CasesPerMonthRow c WHERE c.casesPerMonthKey.period >= :from AND c.casesPerMonthKey.period <= :to ORDER BY c.casesPerMonthKey.period", DiagnosisGroupData.class);
        query.setParameter("from", inputFormatter.print(from));
        query.setParameter("to", inputFormatter.print(to));

        return new DiagnosisGroupResponse(HEADERS, translateForOutput(query.getResultList()));
    }

    private List<DiagnosisGroupRow> translateForOutput(List<DiagnosisGroupData> list) {
        List<DiagnosisGroupRow> translatedCasesPerMonthRows = new ArrayList<>();
    
        for (DiagnosisGroupData row: list) {
            String displayDate = outputFormatter.print(inputFormatter.parseLocalDate(row.getPeriod()));
//            translatedCasesPerMonthRows.add(new DiagnosisGroupRow(displayDate, ));
        }
    
        return translatedCasesPerMonthRows;
    }

}
