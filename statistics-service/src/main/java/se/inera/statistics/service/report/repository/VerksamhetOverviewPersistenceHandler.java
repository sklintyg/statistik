package se.inera.statistics.service.report.repository;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class VerksamhetOverviewPersistenceHandler implements VerksamhetOverview {
    private DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {


        List<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(verksamhetId, range);
        List<OverviewChartRowExtended> ageGroups = getAgeGroups(verksamhetId, range);

        return new VerksamhetOverviewResponse(0, 0, 0, diagnosisGroups, ageGroups, null, null, 0, 0, null);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private List<OverviewChartRowExtended> getDiagnosisGroups(String verksamhetId, Range range) {
        Query query = manager.createQuery("SELECT c.diagnosisGroupKey.diagnosgrupp, sum(c.female) + sum(c.male), c.diagnosisGroupKey.diagnosgrupp  FROM DiagnosisGroupData c WHERE c.diagnosisGroupKey.hsaId = :hsaId AND c.diagnosisGroupKey.period >= :from AND c.diagnosisGroupKey.period <= :to  GROUP BY c.diagnosisGroupKey.diagnosgrupp");
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

    @Transactional
    private List<OverviewChartRowExtended> getAgeGroups(String verksamhetId, Range range) {
        return null;
    }

}
