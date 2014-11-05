package se.inera.statistics.service.processlog;

public final class Vardgivare {
    private final String id;
    private final String namn;

    public Vardgivare(String id, String namn) {
        this.id = id;
        this.namn = namn;
    }

    public String getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }
}
