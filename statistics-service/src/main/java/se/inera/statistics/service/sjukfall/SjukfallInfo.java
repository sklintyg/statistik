package se.inera.statistics.service.sjukfall;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class SjukfallInfo {

    private String id;
    private LocalDate start;
    private LocalDate end;
    private LocalDate prevEnd;

    public SjukfallInfo(String id, LocalDate start, LocalDate end, LocalDate prevEnd) {
        this.id = id;
        this.start = start;
        this.end = end;
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

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public int getLangd() {
        return Days.daysBetween(start, end).getDays() + 1;
    }

    @Override
    public String toString() {
        return "SjukfallInfo{" + "id='" + id + '\'' + ", start=" + start + ", end=" + end + ", prevEnd=" + prevEnd + '}';
    }
}
