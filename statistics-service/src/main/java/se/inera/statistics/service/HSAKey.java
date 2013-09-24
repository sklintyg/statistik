package se.inera.statistics.service;

public class HSAKey {
    private final String vardgivareId;
    private final String enhetId;
    private final String lakareId;

    public HSAKey(String vardgivareId, String enhetId, String lakareId) {
        this.vardgivareId = vardgivareId;
        this.enhetId = enhetId;
        this.lakareId = lakareId;
    }

    public String getLakareId() {
        return lakareId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getEnhetId() {
        return enhetId;
    }

    @Override
    public String toString() {
        return "HSAKey{" + "vardgivareId='" + vardgivareId + '\'' + ", enhetId='" + enhetId + '\'' + ", lakareId='" + lakareId + '\'' + '}';
    }
}
