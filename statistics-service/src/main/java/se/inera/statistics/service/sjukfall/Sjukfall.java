package se.inera.statistics.service.sjukfall;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries(value = { @NamedQuery(name = "SjukfallByPersonIdAndVardgivareId", query = "SELECT s FROM Sjukfall s WHERE s.personId = :personId and s.vardgivareId = :vardgivareId") })
public class Sjukfall {

    @Id
    private String id;

    private String personId;
    private String vardgivareId;
    @Temporal(TemporalType.DATE)
    private Date start;
    @Temporal(TemporalType.DATE)
    private Date end;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public Sjukfall() {
    }

    public Sjukfall(String personId, String vardgivareId, Date start, Date end) {
        id = UUID.randomUUID().toString();
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

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }
}
