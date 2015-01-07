package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.Icd;

import java.util.List;
import java.util.Map;

class DiagnosisKapitelAndAvsnittResponse {
    private final Map<String, List<Icd>> avsnitts;
    private final List<Icd> kapitels;

    public DiagnosisKapitelAndAvsnittResponse(Map<String, List<Icd>> avsnitts, List<Icd> kapitels) {
        this.avsnitts = avsnitts;
        this.kapitels = kapitels;
    }

    public Map<String, List<Icd>> getAvsnitts() {
        return avsnitts;
    }

    public List<Icd> getKapitels() {
        return kapitels;
    }
}
