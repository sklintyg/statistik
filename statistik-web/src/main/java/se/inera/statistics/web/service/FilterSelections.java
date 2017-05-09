package se.inera.statistics.web.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSelections {

    @JsonProperty(ResponseHandler.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER)
    private boolean allAvailableDxsSelectedInFilter;

    @JsonProperty(ResponseHandler.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER)
    private boolean allAvailableEnhetsSelectedInFilter;

    @JsonProperty(ResponseHandler.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER)
    private boolean allAvailableSjukskrivningslangdsSelectedInFilter;

    @JsonProperty(ResponseHandler.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER)
    private boolean allAvailableAgeGroupsSelectedInFilter;

    @JsonProperty(ResponseHandler.FILTERED_ENHETS)
    private List<String> enhetNames;

    FilterSelections(boolean allAvailableDxsSelectedInFilter,
                     boolean allAvailableEnhetsSelectedInFilter,
                     boolean allAvailableSjukskrivningslangdsSelectedInFilter,
                     boolean allAvailableAgeGroupsSelectedInFilter,
                     List<String> enhetNames) {
        this.allAvailableDxsSelectedInFilter = allAvailableDxsSelectedInFilter;
        this.allAvailableEnhetsSelectedInFilter = allAvailableEnhetsSelectedInFilter;
        this.allAvailableSjukskrivningslangdsSelectedInFilter = allAvailableSjukskrivningslangdsSelectedInFilter;
        this.allAvailableAgeGroupsSelectedInFilter = allAvailableAgeGroupsSelectedInFilter;
        this.enhetNames = new ArrayList<>(enhetNames);
    }

    public boolean isAllAvailableDxsSelectedInFilter() {
        return allAvailableDxsSelectedInFilter;
    }

    public boolean isAllAvailableEnhetsSelectedInFilter() {
        return allAvailableEnhetsSelectedInFilter;
    }

    public boolean isAllAvailableSjukskrivningslangdsSelectedInFilter() {
        return allAvailableSjukskrivningslangdsSelectedInFilter;
    }

    public boolean isAllAvailableAgeGroupsSelectedInFilter() {
        return allAvailableAgeGroupsSelectedInFilter;
    }

    public List<String> getEnhetNames() {
        return Collections.unmodifiableList(enhetNames);
    }

}
