package se.inera.statistics.web.model;

public class Business {

    private final String id;
    private final String name;

    public Business(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
