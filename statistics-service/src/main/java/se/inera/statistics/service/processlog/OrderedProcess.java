package se.inera.statistics.service.processlog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class OrderedProcess {
    private static final Logger LOG = LoggerFactory.getLogger(OrderedProcess.class);

    @Autowired
    private Processor processor;

    private List<IntygRecord> records = new LinkedList<IntygRecord>();

    private Object listLockObject = new Object();
    private Object processLockObject = new Object();

    public void register(JsonNode intyg, String dokumentId) {
        synchronized (listLockObject) {
            records.add(new IntygRecord(dokumentId, intyg));
        }
    }

    public void updateSlot(JsonNode info, String documentId) {
        synchronized (processLockObject) {
            List<IntygRecord> completed;
            synchronized (listLockObject) {
                IntygRecord record = findRecord(documentId);
                if (record != null) {
                    record.setInfo(info);
                } else {
                    LOG.error("Not found");
                }

                completed = extractRecordsForProcessing();
            }
            processRecords(completed);
        }
    }

    private void processRecords(List<IntygRecord> completed) {
        for (IntygRecord record: completed) {
            processor.accept(record.getIntyg(), record.getInfo());
        }
    }

    private List<IntygRecord> extractRecordsForProcessing() {
        List<IntygRecord> completed = new ArrayList<>();
        while (records.size() > 0 && records.get(0).isComplete()) {
            completed.add(records.remove(0));
        }
        return completed;
    }

    private IntygRecord findRecord(String documentId) {
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
