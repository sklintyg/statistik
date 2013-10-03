package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.JsonNode;

public interface HSAService {

    JsonNode getHSAInfo(HSAKey key);
}
