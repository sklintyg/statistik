package se.inera.auth.model;

import java.io.Serializable;

public class VerksamhetMapperObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;

    public VerksamhetMapperObject() {
    }

    public VerksamhetMapperObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
