package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;

public interface DiagnosisSubGroups {

    public DiagnosisGroupResponse getDiagnosisSubGroups(String diagnosisGroupId);
    
}
