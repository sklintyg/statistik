/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSelections {

    @JsonProperty(ResponseKeys.ALL_AVAILABLE_DXS_SELECTED_IN_FILTER)
    private boolean allAvailableDxsSelectedInFilter;

    @JsonProperty(ResponseKeys.ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER)
    private boolean allAvailableEnhetsSelectedInFilter;

    @JsonProperty(ResponseKeys.ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER)
    private boolean allAvailableSjukskrivningslangdsSelectedInFilter;

    @JsonProperty(ResponseKeys.ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER)
    private boolean allAvailableAgeGroupsSelectedInFilter;

    @JsonProperty(ResponseKeys.ALL_AVAILABLE_INTYGTYPES_SELECTED_IN_FILTER)
    private boolean allAvailableIntygTypesSelectedInFilter;

    @JsonProperty(ResponseKeys.FILTERED_ENHETS)
    private List<String> enhetNames;

    FilterSelections(boolean allAvailableDxsSelectedInFilter,
        boolean allAvailableEnhetsSelectedInFilter,
        boolean allAvailableSjukskrivningslangdsSelectedInFilter,
        boolean allAvailableAgeGroupsSelectedInFilter,
        boolean allAvailableIntygTypesSelectedInFilter,
        List<String> enhetNames) {
        this.allAvailableDxsSelectedInFilter = allAvailableDxsSelectedInFilter;
        this.allAvailableEnhetsSelectedInFilter = allAvailableEnhetsSelectedInFilter;
        this.allAvailableSjukskrivningslangdsSelectedInFilter = allAvailableSjukskrivningslangdsSelectedInFilter;
        this.allAvailableAgeGroupsSelectedInFilter = allAvailableAgeGroupsSelectedInFilter;
        this.allAvailableIntygTypesSelectedInFilter = allAvailableIntygTypesSelectedInFilter;
        this.enhetNames = enhetNames == null ? Collections.emptyList() : new ArrayList<>(enhetNames);
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

    public boolean isAllAvailableIntygTypesSelectedInFilter() {
        return allAvailableIntygTypesSelectedInFilter;
    }

    public List<String> getEnhetNames() {
        return Collections.unmodifiableList(enhetNames);
    }

}
