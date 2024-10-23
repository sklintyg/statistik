package se.inera.auth;

public enum LoginMethod {
    SITHS, FAKE;

    public String value() {
        return name();
    }

}