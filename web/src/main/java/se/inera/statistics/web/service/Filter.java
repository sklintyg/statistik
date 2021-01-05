/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.util.Collection;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public class Filter {

    private FilterPredicates predicate;
    private Collection<HsaIdEnhet> enheter;
    private Collection<String> diagnoser;
    private Collection<String> sjukskrivningslangd;
    private Collection<String> aldersgrupp;
    private final String hash;
    private Collection<String> intygstyper;
    private boolean useDefaultPeriod;

    //CHECKSTYLE:OFF ParameterNumber
    Filter(FilterPredicates predicate, Collection<HsaIdEnhet> enheter, Collection<String> diagnoser, Collection<String> sjukskrivningslangd,
        Collection<String> aldersgrupp, String hashValue, Collection<String> intygstyper, boolean useDefaultPeriod) {
        this.predicate = predicate;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
        this.sjukskrivningslangd = sjukskrivningslangd;
        this.aldersgrupp = aldersgrupp;
        this.hash = hashValue;
        this.intygstyper = intygstyper;
        this.useDefaultPeriod = useDefaultPeriod;
    }
    //CHECKSTYLE:On ParameterNumber

    public Filter(Filter filter, boolean useDefaultPeriod) {
        this.predicate = filter.predicate;
        this.enheter = filter.enheter;
        this.diagnoser = filter.diagnoser;
        this.sjukskrivningslangd = filter.sjukskrivningslangd;
        this.aldersgrupp = filter.aldersgrupp;
        this.hash = filter.hash;
        this.intygstyper = filter.intygstyper;
        this.useDefaultPeriod = useDefaultPeriod;
    }

    public static Filter empty() {
        return new Filter(SjukfallUtil.ALL_ENHETER, null, null, null, null, null, null, true);
    }

    public FilterPredicates getPredicate() {
        return predicate;
    }

    public Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    public Collection<String> getDiagnoser() {
        return diagnoser;
    }

    public Collection<String> getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public Collection<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public String getFilterHash() {
        return hash;
    }

    public Collection<String> getIntygstyper() {
        return intygstyper;
    }

    public void setIntygstyper(Collection<String> intygstyper) {
        this.intygstyper = intygstyper;
    }

    public boolean isUseDefaultPeriod() {
        return useDefaultPeriod;
    }
}
