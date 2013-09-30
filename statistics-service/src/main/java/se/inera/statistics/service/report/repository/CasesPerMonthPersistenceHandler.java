package se.inera.statistics.service.report.repository;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.CasesPerMonthKey;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CasesPerMonthPersistenceHandler implements CasesPerMonth {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    private static Locale SWEDEN = new Locale("SV_se");
    private DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("MMM yyyy").withLocale(SWEDEN);
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @Transactional
    public void count(String period, Sex sex) {
        CasesPerMonthRow existingRow = manager.find(CasesPerMonthRow.class, new CasesPerMonthKey(period, "nationell"));
        int female = Sex.Female.equals(sex) ? 1 : 0;
        int male = Sex.Male.equals(sex) ? 1 : 0;

        if (existingRow == null) {
            CasesPerMonthRow row = new CasesPerMonthRow(period, "nationell", female, male);
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
    public List<CasesPerMonthRow> getCasesPerMonth() {
        TypedQuery<CasesPerMonthRow> query = manager.createQuery("SELECT c FROM CasesPerMonthRow c ORDER BY c.casesPerMonthKey.period", CasesPerMonthRow.class);

        return translateForOutput(query.getResultList());
    }

    private List<CasesPerMonthRow> translateForOutput(List<CasesPerMonthRow> list) {
        List<CasesPerMonthRow> translatedCasesPerMonthRows = new ArrayList<>();

        for (CasesPerMonthRow row: list) {
            String displayDate = outputFormatter.print(inputFormatter.parseLocalDate(row.getPeriod()));
            translatedCasesPerMonthRows.add(new CasesPerMonthRow(displayDate, row.getFemale(), row.getMale()));
        }

        return translatedCasesPerMonthRows;
    }
}
