package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.JSONParser;

@Component
public class HSAServiceImpl implements HSAService {
    @Override
    public JsonNode getHSAInfo(HSAKey key) {
        return JSONParser.parse("{}");
    }
}
