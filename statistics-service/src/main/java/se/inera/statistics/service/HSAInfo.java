package se.inera.statistics.service;

public class HSAInfo {
    // Enhetstyp, verksamhet, kommun, befattning, kön, ålder, specialitet*

    private String lan;

    public HSAInfo(String lan) {
        this.lan = lan;
    }

    public String getLan() {
        return lan;
    }
}
