package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Enhet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vardgivareId;

    private String vardgivareNamn;

    private String enhetId;

    private String namn;

    private String lansId;

    private String kommunId;

    public Enhet() {
    }

    public long getId() {
        return id;
    }

    public Enhet(String vardgivareId, String vardgivareNamn, String enhetId, String namn, String lansId, String kommunId) {
        this.vardgivareId = vardgivareId;
        this.vardgivareNamn = vardgivareNamn;
        this.enhetId = enhetId;
        this.namn = namn;
        this.lansId = lansId;
        this.kommunId = kommunId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String namn) {
        this.vardgivareNamn = namn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public void setEnhetId(String enhetId) {
        this.enhetId = enhetId;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getLansId() { return lansId; }

    public void setLansId(String lansId) { this.lansId = lansId; }

    public String getKommunId() { return kommunId; }

    public void setKommunId(String kommunId) { this.kommunId = kommunId; }

}
