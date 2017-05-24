package se.inera.statistics.web.model;

import se.inera.statistics.service.report.model.Icd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticFilterData {

    private final Map<String, String> sjukskrivningLengths;
    private final Map<String, String> ageGroups;
    private final List<Icd> dxs;

    public StaticFilterData(Map<String, String> sjukskrivningLengths, Map<String, String> ageGroups, List<Icd> dxs) {
        this.sjukskrivningLengths = new HashMap<>(sjukskrivningLengths);
        this.ageGroups = new HashMap<>(ageGroups);
        this.dxs = new ArrayList<>(dxs);
    }

    public Map<String, String> getSjukskrivningLengths() {
        return sjukskrivningLengths;
    }

    public Map<String, String> getAgeGroups() {
        return ageGroups;
    }

    public List<Icd> getDxs() {
        return dxs;
    }

}
