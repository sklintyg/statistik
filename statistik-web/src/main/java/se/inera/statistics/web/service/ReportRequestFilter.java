package se.inera.statistics.web.service;

import java.util.List;

public class ReportRequestFilter {
    private List<String> kapitels;
    private List<String> avsnitts;
    private List<String> kategoris;
    private List<String> enhets;

    public List<String> getKategoris() {
        return kategoris;
    }

    public List<String> getKapitels() {
        return kapitels;
    }

    public List<String> getAvsnitts() {
        return avsnitts;
    }

    public List<String> getEnhets() {
        return enhets;
    }
}
