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

import java.util.Collections;
import java.util.List;

public class FilterData {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private List<String> diagnoser;
    private List<String> enheter;
    private List<String> sjukskrivningslangd;
    private List<String> aldersgrupp;
    private List<String> intygstyper;
    private String fromDate;
    private String toDate;
    private boolean useDefaultPeriod;

    // To be used by json converter
    FilterData() {
    }

    // CHECKSTYLE:OFF ParameterNumber
    public FilterData(List<String> diagnoser, List<String> enheter, List<String> sjukskrivningslangd,
        List<String> aldersgrupp, List<String> intygstyper, String fromDate, String toDate, boolean useDefaultPeriod) {
        this.diagnoser = diagnoser == null ? Collections.<String>emptyList() : Collections.unmodifiableList(diagnoser);
        this.enheter = enheter == null ? Collections.<String>emptyList() : Collections.unmodifiableList(enheter);
        this.sjukskrivningslangd = sjukskrivningslangd == null ? Collections.<String>emptyList()
            : Collections.unmodifiableList(sjukskrivningslangd);
        this.aldersgrupp = aldersgrupp == null ? Collections.<String>emptyList() : Collections.unmodifiableList(aldersgrupp);
        this.intygstyper = intygstyper == null ? Collections.<String>emptyList() : Collections.unmodifiableList(intygstyper);
        this.useDefaultPeriod = useDefaultPeriod;
        this.toDate = toDate;
        this.fromDate = fromDate;
    }
    // CHECKSTYLE:ON ParameterNumber

    public static FilterData empty() {
        return new FilterData(Collections.<String>emptyList(), Collections.<String>emptyList(),
            Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList(), null, null, true);
    }

    public static FilterData createForDxsOnly(List<String> diagnoser) {
        return new FilterData(diagnoser, Collections.<String>emptyList(),
            Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList(), null, null, true);
    }

    public List<String> getDiagnoser() {
        return diagnoser;
    }

    public List<String> getEnheter() {
        return enheter;
    }

    public List<String> getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public List<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public List<String> getIntygstyper() {
        return intygstyper;
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
