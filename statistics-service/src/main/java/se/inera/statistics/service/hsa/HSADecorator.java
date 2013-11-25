package se.inera.statistics.service.hsa;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.processlog.HSAStore;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private HSAService service;

    @Async
    @Transactional
    public void decorate(JsonNode doc, String documentId) {
        syncDecorate(doc, documentId);
    }

    public JsonNode syncDecorate(JsonNode doc, String documentId) {
        JsonNode info = getHSAInfo(documentId);
        if (info == null) {
            HSAKey key = extractHSAKey(doc);
            LOG.debug(key.toString());
            info = service.getHSAInfo(key);
            storeHSAInfo(documentId, info);
        }
        return info;
    }

    protected void storeHSAInfo(String documentId, JsonNode info) {
        if (info != null) {
            HSAStore entity = new HSAStore(documentId, info.toString());
            manager.persist(entity);
        }
    }

    public JsonNode getHSAInfo(String documentId) {
        HSAStore hsaStore = manager.find(HSAStore.class, documentId);
        if (hsaStore != null) {
            return JSONParser.parse(hsaStore.getData());
        } else {
            return null;
        }
    }

    protected HSAKey extractHSAKey(JsonNode document) {
        String vardgivareId = getVardgivareId(document);
        String enhetId = getEnhetId(document);
        String lakareId = getLakarId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
