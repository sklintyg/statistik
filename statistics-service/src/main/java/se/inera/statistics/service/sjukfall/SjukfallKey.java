package se.inera.statistics.service.sjukfall;

import org.joda.time.LocalDate;

public class SjukfallKey {

    private final String personId;
    private final String vardgivareId;
    private final LocalDate start;
    private final LocalDate end;

    public SjukfallKey(String personId, String vardgivareId, LocalDate start, LocalDate end) {
        this.personId = personId;
        this.vardgivareId = vardgivareId;
        this.start = start;
        this.end = end;
    }

    public String getPersonId() {
        return personId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}

