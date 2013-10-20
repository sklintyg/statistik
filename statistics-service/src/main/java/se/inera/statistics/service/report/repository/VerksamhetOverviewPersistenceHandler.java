package se.inera.statistics.service.report.repository;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewSexProportion;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class VerksamhetOverviewPersistenceHandler implements VerksamhetOverview {
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Transactional
    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {
        OverviewSexProportion sexProportion = getSexProportion(verksamhetId, range);
        OverviewSexProportion prevSexProportion = getSexProportion(verksamhetId, ReportUtil.getPreviousPeriod(range));
        List<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(verksamhetId, range);
        List<OverviewChartRowExtended> ageGroups = getAgeGroups(verksamhetId, range);
        List<OverviewChartRowExtended> degreeOfSickLeaveGroups = getDegreeOfSickLeaveGroups(verksamhetId, range);
        List<OverviewChartRow> sickLeaveLengthGroups = getSickLeaveLengthGroups(verksamhetId, range);
        int casesPerMonth = getCasesPerMonth(verksamhetId, range);

        return new VerksamhetOverviewResponse(casesPerMonth, sexProportion, prevSexProportion, diagnosisGroups, ageGroups, degreeOfSickLeaveGroups, sickLeaveLengthGroups, 0, 0);
    }

    private List<OverviewChartRow> getSickLeaveLengthGroups(String verksamhetId, Range range) {
        List<OverviewChartRow> result = new ArrayList<>();
        result.add(new OverviewChartRow("10-20", 2));
        return result;
    }

    private List<OverviewChartRowExtended> getDegreeOfSickLeaveGroups(String verksamhetId, Range range) {
        List<OverviewChartRowExtended> result = new ArrayList<>();
        result.add(new OverviewChartRowExtended("10-20", 2, 1));
        return result;
    }

    private OverviewSexProportion getSexProportion(String verksamhetId, Range range) {
        Query query = manager.createQuery("SELECT SUM(c.male), SUM(c.female) FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", inputFormatter.print(range.getFrom()));
        query.setParameter("to", inputFormatter.print(range.getTo()));
        Object[] row = (Object[]) query.getSingleResult();
        if (row == null || row[0] == null || row[1] == null) {
            return new OverviewSexProportion(0, 0, range);
        }
        return new OverviewSexProportion(((Long) row[0]).intValue(), ((Long) row[1]).intValue(), range);
    }

    private int getCasesPerMonth(String verksamhetId, Range range) {
        TypedQuery<Long> query = manager.createQuery("SELECT SUM(c.male) + SUM(c.female) FROM CasesPerMonthRow c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND:to", Long.class);
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", inputFormatter.print(range.getFrom()));
        query.setParameter("to", inputFormatter.print(range.getTo()));
        return query.getSingleResult().intValue();
    }

    @SuppressWarnings("unchecked")
    private List<OverviewChartRowExtended> getDiagnosisGroups(String verksamhetId, Range range) {
        Query query = manager.createQuery("SELECT c.key.diagnosgrupp, sum(c.female) + sum(c.male), c.key.diagnosgrupp  FROM DiagnosisGroupData c WHERE c.key.hsaId = :hsaId AND c.key.period BETWEEN :from AND :to  GROUP BY c.key.diagnosgrupp");
        query.setParameter("hsaId", verksamhetId);
        query.setParameter("from", inputFormatter.print(range.getFrom()));
        query.setParameter("to", inputFormatter.print(range.getTo()));

        List<Object[]> queryResult;
        queryResult = (List<Object[]>) query.getResultList();

        ArrayList<OverviewChartRowExtended> result = new ArrayList<>();
        for (Object[] row : queryResult) {
            result.add(new OverviewChartRowExtended((String) row[0], ((Long) row[1]).intValue(), 0));
        }

        return result;
    }

    private List<OverviewChartRowExtended> getAgeGroups(String verksamhetId, Range range) {
        List<OverviewChartRowExtended> result = new ArrayList<>();
        result.add(new OverviewChartRowExtended("10-20", 2, 1));
        return result;
    }

}
