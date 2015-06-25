/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.warehouse.SjukfallFilter;

import java.util.Collection;

class Filter {

    private SjukfallFilter predicate;
    private Collection<HsaIdEnhet> enheter;
    private Collection<String> diagnoser;

    Filter(SjukfallFilter predicate, Collection<HsaIdEnhet> enheter, Collection<String> diagnoser) {
        this.predicate = predicate;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
    }

    static Filter empty() {
        return new Filter(null, null, null);
    }

    SjukfallFilter getPredicate() {
        return predicate;
    }

    Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    Collection<String> getDiagnoser() {
        return diagnoser;
    }

}
