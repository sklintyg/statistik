package se.inera.statistics.hsa.model;

import java.io.Serializable;

/**
 * @author rlindsjo
 */
public class Vardenhet implements Serializable {

    private final String id;
    private final String namn;

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
