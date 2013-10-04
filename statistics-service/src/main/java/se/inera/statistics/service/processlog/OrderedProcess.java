package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class OrderedProcess {
    private static final Logger LOG = LoggerFactory.getLogger(OrderedProcess.class);

    @Autowired
    private Processor processor;

    private List<IntygRecord> records = new LinkedList<IntygRecord>();

    synchronized public void register(JsonNode intyg, String dokumentId) {
        putIntygRecord(intyg, dokumentId);
    }

    synchronized public void updateSlot(JsonNode intyg, JsonNode info, String documentId) {
        // Try to register to avoid race condition when updateSlot() is called before register()
        register(intyg, documentId);

        IntygRecord record = getIntygRecord(documentId);
        record.setInfo(info);
        sendUnlockedRecords();
    }

    private void sendUnlockedRecords() {
        while (records.size() > 0 && records.get(0).isComplete()) {
            IntygRecord record = records.remove(0);
            processor.accept(record.getIntyg(), record.getInfo());
        }
    }

    private void putIntygRecord(JsonNode intyg, String dokumentId) {
        if(getIntygRecord(dokumentId) == null) {
            records.add(new IntygRecord(dokumentId, intyg));
        }
    }

    private IntygRecord getIntygRecord(String documentId) {
        for (IntygRecord record: records) {
            if (record.getDocumentId().equals(documentId)) {
                return record;
            }
        }
        return null;
    }

    private static class IntygRecord {
        private final String documentId;
        private JsonNode intyg;
        private JsonNode info;

        public IntygRecord(String documentId, JsonNode intyg) {
            this.documentId = documentId;
            this.intyg = intyg;
        }

        public void setInfo(JsonNode info) {
            this.info = info;
        }

        private String getDocumentId() {
            return documentId;
        }

        private JsonNode getIntyg() {
            return intyg;
        }

        private JsonNode getInfo() {
            return info;
        }

        public boolean isComplete() {
            return intyg != null && info != null;
        }
    }
}
