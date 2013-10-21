package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SickLeaveLengthResponse;
import se.inera.statistics.service.report.util.Verksamhet;

public interface SjukfallslangdGrupp {
    SickLeaveLengthResponse getStatistics(String hsaId, LocalDate when, int periods);
    void count(String period, String hsaId, String group, int periods, Verksamhet typ, Sex sex);
}
