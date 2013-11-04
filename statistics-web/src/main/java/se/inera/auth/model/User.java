package se.inera.auth.model;

import se.inera.statistics.hsa.model.Vardgivare;

import java.util.Collections;
import java.util.List;

public class User {

    private final String hsaId;
    private final String name;
    private List<Vardgivare> vardgivareList;

    public User(String hsaId, String name, List<Vardgivare> vardgivareList) {
        this.hsaId = hsaId;
        this.name = name;
        this.vardgivareList = Collections.unmodifiableList(vardgivareList);
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public List<Vardgivare> getVardgivareList() {
        return vardgivareList;
    }
}
