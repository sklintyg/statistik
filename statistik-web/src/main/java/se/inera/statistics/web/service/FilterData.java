/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import java.util.Collections;
import java.util.List;

public class FilterData {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private List<String> diagnoser;
    private List<String> enheter;
    private List<String> verksamhetstyper;
    private String fromDate;
    private String toDate;
    private boolean useDefaultPeriod;

    //To be used by json converter
    FilterData() {
    }

    public FilterData(List<String> diagnoser, List<String> enheter, List<String> verksamhetstyper, String fromDate, String toDate, boolean useDefaultPeriod) {
        this.diagnoser = diagnoser == null ? Collections.<String>emptyList() : Collections.unmodifiableList(diagnoser);
        this.enheter = enheter == null ? Collections.<String>emptyList() : Collections.unmodifiableList(enheter);
        this.verksamhetstyper = verksamhetstyper == null ? Collections.<String>emptyList() : Collections.unmodifiableList(verksamhetstyper);
        this.useDefaultPeriod = useDefaultPeriod;
        this.toDate = toDate;
        this.fromDate = fromDate;
    }

    public static FilterData empty() {
        return new FilterData(Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList(), null, null, true);
    }

    public static FilterData createForDxsOnly(List<String> diagnoser) {
        return new FilterData(diagnoser, Collections.<String>emptyList(), Collections.<String>emptyList(), null, null, true);
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

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public boolean isUseDefaultPeriod() {
        return useDefaultPeriod;
    }
}
