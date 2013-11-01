package se.inera.statistics.service.sjukfall;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.LocalDate;

@Entity
@NamedQueries(value = { @NamedQuery(name = "SjukfallByPersonIdAndVardgivareId", query = "SELECT s FROM Sjukfall s WHERE s.personId = :personId AND s.vardgivareId = :vardgivareId ORDER BY s.start DESC ") })
@Table(name = "sjukfall")
public class Sjukfall {

    @Id
    private String id;

    private String personId;
    private String vardgivareId;
    private String start;
    private String end;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public Sjukfall() {
    }

    public Sjukfall(String personId, String vardgivareId, LocalDate start, LocalDate end) {
        id = UUID.randomUUID().toString();
        this.personId = personId;
        this.vardgivareId = vardgivareId;
        this.start = start.toString();
        this.end = end.toString();
    }

    public String getPersonId() {
        return personId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public LocalDate getStart() {
        return new LocalDate(start);
    }

    public LocalDate getEnd() {
        return new LocalDate(end);
    }

    public void setEnd(LocalDate end) {
        this.end = end.toString();
    }

    public String getId() {
        return id;
    }
}
