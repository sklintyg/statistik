/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.Collection;

public class Filter {

    private FilterPredicates predicate;
    private Collection<HsaIdEnhet> enheter;
    private Collection<String> diagnoser;
    private Collection<String> sjukskrivningslangd;
    private Collection<String> aldersgrupp;
    private final String hash;
    private Collection<String> intygstyper;

    Filter(FilterPredicates predicate, Collection<HsaIdEnhet> enheter, Collection<String> diagnoser, Collection<String> sjukskrivningslangd,
            Collection<String> aldersgrupp, String hashValue, Collection<String> intygstyper) {
        this.predicate = predicate;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
        this.sjukskrivningslangd = sjukskrivningslangd;
        this.aldersgrupp = aldersgrupp;
        this.hash = hashValue;
        this.intygstyper = intygstyper;
    }

    public static Filter empty() {
        return new Filter(SjukfallUtil.ALL_ENHETER, null, null, null, null, null, null);
    }

    FilterPredicates getPredicate() {
        return predicate;
    }

    Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    Collection<String> getDiagnoser() {
        return diagnoser;
    }

    public Collection<String> getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public Collection<String> getAldersgrupp() {
        return aldersgrupp;
    }

    String getFilterHash() {
        return hash;
    }

    public Collection<String> getIntygstyper() {
        return intygstyper;
    }

    public void setIntygstyper(Collection<String> intygstyper) {
        this.intygstyper = intygstyper;
    }
}
