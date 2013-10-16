package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;

public interface SickLeaveLength {
    String HSA_NATIONELL = "nationell";

    SickLeaveLengthResponse getStatistics(String hsaId, Range range);

}
