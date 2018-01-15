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
package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

import java.time.LocalDate;
import java.util.Collection;

public class MessagesFilter {

    private HsaIdVardgivare vardgivarId;
    private final LocalDate from;
    private final int numberOfMonths;
    private final Collection<HsaIdEnhet> enheter;
    private final Collection<String> aldersgrupp;
    private final Collection<String> diagnoser;
    private Collection<String> intygstyper;

    public MessagesFilter(HsaIdVardgivare vardgivarId, LocalDate from, int numberOfMonths, Collection<HsaIdEnhet> enheter,
                          Collection<String> aldersgrupp, Collection<String> diagnoser, Collection<String> intygstyper) {
        this.vardgivarId = vardgivarId;
        this.from = from;
        this.numberOfMonths = numberOfMonths;
        this.enheter = enheter;
        this.aldersgrupp = aldersgrupp;
        this.diagnoser = diagnoser;
        this.intygstyper = intygstyper;
    }

    public HsaIdVardgivare getVardgivarId() {
        return vardgivarId;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return from.plusMonths(numberOfMonths);
    }

    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    public Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    public Collection<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public Collection<String> getDiagnoser() {
        return diagnoser;
    }

    public Collection<String> getIntygstyper() {
        return intygstyper;
    }

}
