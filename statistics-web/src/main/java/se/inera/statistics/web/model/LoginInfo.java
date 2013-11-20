package se.inera.statistics.web.model;

import java.util.List;

public class LoginInfo {

    private String hsaId;
    private final String name;
    private final boolean loggedIn;
    private final List<Verksamhet> businesses;

    public LoginInfo(String hsaId, String name, boolean loggedIn, List<Verksamhet> businesses) {
        this.hsaId = hsaId;
        this.name = name;
        this.loggedIn = loggedIn;
        this.businesses = businesses;
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public List<Verksamhet> getBusinesses() {
        return businesses;
    }

}
