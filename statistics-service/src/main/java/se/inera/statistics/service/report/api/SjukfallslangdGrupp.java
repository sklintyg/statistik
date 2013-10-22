package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;

public interface SjukfallslangdGrupp {
    SickLeaveLengthResponse getCurrentStatistics(String hsaId);
    SickLeaveLengthResponse getHistoricalStatistics(String hsaId, LocalDate when, RollingLength rollingLength);
    void count(String period, String hsaId, String group, RollingLength rollingLength, Verksamhet typ, Sex sex);
    SimpleDualSexResponse<SimpleDualSexDataRow> getLongSickLeaves(String decodeId, Range range);
}
