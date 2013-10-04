package se.inera.statistics.service.hsa;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.processlog.OrderedProcess;

import java.util.HashMap;
import java.util.Map;

@Component
public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @Autowired
    private HSAService service;

    @Autowired
    private OrderedProcess orderedProcess;

    private Map<String, JsonNode> storedInfo = new HashMap<>();

    @Async
    public void decorate(JsonNode doc, String documentId) {
        HSAKey key = extractHSAKey(doc);
        LOG.debug(key.toString());
        JsonNode info = service.getHSAInfo(key);
        storeHSAInfo(documentId, info);
        orderedProcess.updateSlot(doc, info, documentId);
    }

    protected void storeHSAInfo(String documentId, JsonNode info) {
        //storedInfo.put(documentId, info);
    }

    protected HSAKey extractHSAKey(JsonNode document) {
        String vardgivareId = getVardgivareId(document);
        String enhetId = getEnhetId(document);
        String lakareId = getLakarId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
