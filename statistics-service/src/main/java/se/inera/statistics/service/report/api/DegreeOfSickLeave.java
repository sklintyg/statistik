package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;

public interface DegreeOfSickLeave {
    String HSA_NATIONELL = "nationell";

    DegreeOfSickLeaveResponse getStatistics(String hsaId);

}
