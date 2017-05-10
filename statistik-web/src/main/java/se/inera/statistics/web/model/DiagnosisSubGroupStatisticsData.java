package se.inera.statistics.web.model;

import se.inera.statistics.service.report.util.Icd10;

public class DiagnosisSubGroupStatisticsData extends DualSexStatisticsData {

    private final String dxGroup;

    public DiagnosisSubGroupStatisticsData(DualSexStatisticsData data, Icd10.Id dxGroup) {
        super(data.getTableData(), data.getMaleChart(), data.getFemaleChart(),
                data.getPeriod(), data.getFilter(), data.getMessages());
        this.dxGroup = dxGroup != null ? dxGroup.getVisibleId() + " " + dxGroup.getName() : "";
    }

    public String getDxGroup() {
        return dxGroup;
    }

}
