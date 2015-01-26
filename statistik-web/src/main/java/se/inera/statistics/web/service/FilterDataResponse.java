package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FilterDataResponse {
    private List<String> diagnoser;
    private List<String> enheter;

    //To be used by json converter
    FilterDataResponse() {
    }

    public FilterDataResponse(Collection<String> diagnoser, Collection<String> enheter) {
        this.diagnoser = diagnoser == null ? null : Collections.unmodifiableList(new ArrayList<>(diagnoser));
        this.enheter = enheter == null ? null : Collections.unmodifiableList(new ArrayList<>(enheter));
    }

    public List<String> getDiagnoser() {
        return diagnoser;
    }

    public List<String> getEnheter() {
        return enheter;
    }

}
