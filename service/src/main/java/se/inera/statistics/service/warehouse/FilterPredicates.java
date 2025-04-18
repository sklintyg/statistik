/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;

public class FilterPredicates {

    public static final String HASH_EMPTY_FILTER = "EMPTY_FILTER";

    private Predicate<Fact> intygFilter;
    private Predicate<Sjukfall> sjukfallFilter;
    private boolean sjukfallangdfilterActive;
    private String hash;

    public FilterPredicates(Predicate<Fact> intygFilter, Predicate<Sjukfall> sjukfallFilter, String hash,
        boolean sjukfallangdfilterActive) {
        if (intygFilter == null) {
            throw new IllegalArgumentException("Intygfilter must not be null");
        }
        if (sjukfallFilter == null) {
            throw new IllegalArgumentException("Sjukfallfilter must not be null");
        }
        if (hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("Hash must not be empty");
        }

        this.intygFilter = intygFilter;
        this.sjukfallFilter = sjukfallFilter;
        this.hash = hash;
        this.sjukfallangdfilterActive = sjukfallangdfilterActive;
    }

    public Predicate<Fact> getIntygFilter() {
        return intygFilter;
    }

    public Predicate<Sjukfall> getSjukfallFilter() {
        return sjukfallFilter;
    }

    public String getHash() {
        return hash;
    }

    public boolean isSjukfallangdfilterActive() {
        return sjukfallangdfilterActive;
    }

    public static String getHashValueForEnhets(Collection<HsaIdEnhet> enhetIds) {
        return "_" + enhetIds.stream().map(HsaIdAny::getId).sorted().collect(Collectors.joining("_"));
    }

}
