package se.inera.statistics.service.processlog;

public class Receiver {

    private ProcessLog processLog;

    public void accept(EventType type, String data) {
        processLog.store(type, data);
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

}
