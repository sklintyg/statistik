package se.inera.statistics.service.sjukfall;

import java.util.Date;

public class SjukfallInfo {

    private String id;
    private Date prevEnd;

    public SjukfallInfo(String id, Date prevEnd) {
        this.id = id;
        this.prevEnd = prevEnd;
    }

    public String getId() {
        return id;
    }

    public Date getPrevEnd() {
        return prevEnd;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SjukfallInfo) {
            return id.equals(((SjukfallInfo) obj).getId());
        } else {
            return false;
        }
    }
}
