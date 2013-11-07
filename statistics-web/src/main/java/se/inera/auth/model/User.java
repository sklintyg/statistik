package se.inera.auth.model;

import se.inera.statistics.hsa.model.Vardenhet;

import java.util.Collections;
import java.util.List;

public class User {

    private final String hsaId;
    private final String name;
    private List<Vardenhet> vardenhetList;

    public User(String hsaId, String name, List<Vardenhet> vardenhetsList) {
        this.hsaId = hsaId;
        this.name = name;
        this.vardenhetList = Collections.unmodifiableList(vardenhetsList);
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public List<Vardenhet> getVardenhetList() {
        return vardenhetList;
    }
}
