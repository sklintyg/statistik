package se.inera.statistics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @Autowired
    private HSAService service;

    public void decorate(String doc, long documentId) throws IOException {
        HSAKey key = extractHSAKey(doc);
        LOG.debug(key.toString());
        HSAInfo info = service.getHSAInfo(key);
        storeHSAInfo(documentId, info);
    }

    protected void storeHSAInfo(long documentId, HSAInfo info) {

    }

    protected HSAKey extractHSAKey(String doc) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(doc);
        String vardgivareId = root.path("skapadAv").path("vardenhet").path("vardgivare").path("id").path("extension").textValue();
        String enhetId = root.path("skapadAv").path("vardenhet").path("id").path("extension").textValue();
        String lakareId = root.path("skapadAv").path("id").path("extension").textValue();
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
