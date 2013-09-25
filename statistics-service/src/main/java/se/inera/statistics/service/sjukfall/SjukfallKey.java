package se.inera.statistics.service.sjukfall;

import java.util.Date;

public class SjukfallKey {

    private final String personId;
    private final String vardgivareId;
    private final Date start;
    private final Date end;

    public SjukfallKey(String personId, String vardgivareId, Date start, Date end) {
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

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}

