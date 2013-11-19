package se.inera.statistics.hsa.model;

import java.io.Serializable;

/**
 * @author rlindsjo
 */
public class Vardenhet implements Serializable {

    private String id;
    private String namn;

    public Vardenhet() {
    }

    public Vardenhet(String id, String namn) {
        this.id = id;
        this.namn = namn;
    }

    public String getNamn() {
        return namn;
    }

    public String getId() {
        return id;
    }

}
