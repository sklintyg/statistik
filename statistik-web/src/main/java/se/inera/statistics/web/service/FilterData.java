package se.inera.statistics.web.service;

import java.util.Collections;
import java.util.List;

public class FilterData {
    private List<String> diagnoser;
    private List<String> enheter;
    private List<String> verksamhetstyper;

    //To be used by json converter
    FilterData() {
    }

    public FilterData(List<String> diagnoser, List<String> enheter, List<String> verksamhetstyper) {
        this.diagnoser = Collections.unmodifiableList(diagnoser);
        this.enheter = Collections.unmodifiableList(enheter);
        this.verksamhetstyper = Collections.unmodifiableList(verksamhetstyper);
    }

    public List<String> getDiagnoser() {
        return diagnoser;
    }

    public List<String> getEnheter() {
        return enheter;
    }

    public List<String> getVerksamhetstyper() {
        return verksamhetstyper;
    }

}
