package se.inera.statistics.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static se.inera.statistics.service.helper.DocumentHelper.*;

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
        String vardgivareId = getVardgivareId(root);
        String enhetId = getEnhetId(root);
        String lakareId = getLakarId(root);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
