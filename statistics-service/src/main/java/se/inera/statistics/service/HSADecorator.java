package se.inera.statistics.service;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.helper.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;

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

    protected HSAKey extractHSAKey(String doc) {
        JsonNode root = JSONParser.parse(doc);
        String vardgivareId = getVardgivareId(root);
        String enhetId = getEnhetId(root);
        String lakareId = getLakarId(root);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
