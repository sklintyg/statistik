package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.web.model.DiagnosisGroupsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

@Service("chartService")
public class ChartDataService {

    private CasesPerMonth datasourceCasesPerMonth;

    @Autowired
    private DiagnosisGroups datasourceDiagnosisGroups;

    @Autowired
    private DiagnosisSubGroups datasourceDiagnosisSubGroups;

    @Autowired
    private Overview datasourceOverview;

    public ChartDataService(CasesPerMonth casesPerMonthPersistenceHandler) {
        datasourceCasesPerMonth = casesPerMonthPersistenceHandler;
    }

    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({ MediaType.APPLICATION_JSON })
    public TableData getNumberOfCasesPerMonth() {
        List<CasesPerMonthRow> casesPerMonth = datasourceCasesPerMonth.getCasesPerMonth();
        return new CasesPerMonthConverter().convertCasesPerMonthData(casesPerMonth);
    }

    @GET
    @Path("getDiagnosisGroups")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<DiagnosisGroup> getDiagnosisGroups() {
        return DiagnosisGroupsUtil.getAllDiagnosisGroups();
    }

    @GET
    @Path("getDiagnosisGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisGroupStatistics() {
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisGroups.getDiagnosisGroups();
        return new DiagnosisGroupsConverter().convert(diagnosisGroups);
    }


    @GET
    @Path("getDiagnosisSubGroupStatistics")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisGroupsData getDiagnosisSubGroupStatistics(@QueryParam("groupId") String groupId) {
        DiagnosisGroupResponse diagnosisGroups = datasourceDiagnosisSubGroups.getDiagnosisSubGroups(groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups);
    }

    @GET
    @Path("getOverview")
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        OverviewResponse response = datasourceOverview.getOverview();
        return new OverviewConverter().convert(response);
    }

}
