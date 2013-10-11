package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;

public interface DegreeOfSickLeave {
    public static final String HSA_NATIONELL = "nationell";

    DegreeOfSickLeaveResponse getStatistics(String hsaId);

}
