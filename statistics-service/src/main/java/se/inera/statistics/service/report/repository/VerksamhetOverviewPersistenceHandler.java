package se.inera.statistics.service.report.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.api.VerksamhetOverview;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

public class VerksamhetOverviewPersistenceHandler implements VerksamhetOverview {
    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    DiagnosgroupPersistenceHandler diagnosgroupPersistenceHandler;

    @Override
    public VerksamhetOverviewResponse getOverview(String verksamhetId, Range range) {


        ArrayList<OverviewChartRowExtended> diagnosisGroups = getDiagnosisGroups(verksamhetId, range);

        return new VerksamhetOverviewResponse(0, 0, 0, diagnosisGroups, null, null, null, 0, 0, null);
    }

    @Transactional
    private ArrayList<OverviewChartRowExtended> getDiagnosisGroups(String verksamhetId, Range range) {
        return null;
    }
}
