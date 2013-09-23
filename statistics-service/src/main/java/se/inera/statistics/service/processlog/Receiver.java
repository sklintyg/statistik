package se.inera.statistics.service.processlog;

import javax.jms.Message;
import javax.jms.MessageListener;

public class Receiver implements MessageListener {

    private ProcessLog processLog;

    public void accept(EventType type, String data) {
        processLog.store(type, data);
    }

    public void onMessage(Message rawData) {
        System.out.println("Received " + rawData + " .");
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

}
