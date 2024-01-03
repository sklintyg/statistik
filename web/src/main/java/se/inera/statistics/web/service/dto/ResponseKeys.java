/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.dto;

public final class ResponseKeys {

    public static final String ALL_AVAILABLE_DXS_SELECTED_IN_FILTER = "allAvailableDxsSelectedInFilter";
    public static final String ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER = "allAvailableEnhetsSelectedInFilter";
    public static final String ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER = "allAvailableSjukskrivningslangdsSelectedInFilter";
    public static final String ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER = "allAvailableAgeGroupsSelectedInFilter";
    public static final String ALL_AVAILABLE_INTYGTYPES_SELECTED_IN_FILTER = "allAvailableIntygTypesSelectedInFilter";

    public static final String FILTERED_ENHETS = "filteredEnhets";

    private ResponseKeys() {
    }
}
