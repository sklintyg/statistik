package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

public interface ProcessorListener {

    void accept(SjukfallInfo sjukfallInfo, JsonNode anonymous, JsonNode hsa, long logId);
}
