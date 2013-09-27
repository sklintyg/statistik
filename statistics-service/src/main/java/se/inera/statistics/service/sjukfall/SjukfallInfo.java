package se.inera.statistics.service.sjukfall;

import org.joda.time.LocalDate;

public class SjukfallInfo {

    private String id;
    private LocalDate prevEnd;

    public SjukfallInfo(String id, LocalDate prevEnd) {
        this.id = id;
        this.prevEnd = prevEnd;
    }

    public String getId() {
        return id;
    }

    public LocalDate getPrevEnd() {
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
