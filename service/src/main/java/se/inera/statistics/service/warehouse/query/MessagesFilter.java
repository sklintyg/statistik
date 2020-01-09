/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

public class MessagesFilter {

    private HsaIdVardgivare vardgivarId;
    private final LocalDate from;
    private final LocalDate to;
    private final Collection<HsaIdEnhet> enheter;
    private final Collection<String> aldersgrupp;
    private final Collection<String> diagnoser;
    private Collection<String> intygstyper;
    private Collection<String> amne;

    public MessagesFilter(HsaIdVardgivare vardgivarId, LocalDate from, LocalDate to, Collection<HsaIdEnhet> enheter,
        Collection<String> aldersgrupp, Collection<String> diagnoser, Collection<String> intygstyper) {
        this.vardgivarId = vardgivarId;
        this.from = from;
        this.to = to;
        this.enheter = enheter;
        this.aldersgrupp = aldersgrupp;
        this.diagnoser = diagnoser;
        this.intygstyper = intygstyper;
    }

    public MessagesFilter(MessagesFilter messagesFilter, Collection<String> amne) {
        this.vardgivarId = messagesFilter.vardgivarId;
        this.from = messagesFilter.from;
        this.to = messagesFilter.to;
        this.enheter = messagesFilter.enheter;
        this.aldersgrupp = messagesFilter.aldersgrupp;
        this.diagnoser = messagesFilter.diagnoser;
        this.intygstyper = messagesFilter.intygstyper;
        this.amne = amne;
    }

    public HsaIdVardgivare getVardgivarId() {
        return vardgivarId;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public int getNumberOfMonths() {
        Period diff = Period.between(from, to.plusDays(1));
        final int monthsPerYear = 12;
        return diff.getYears() * monthsPerYear + diff.getMonths();
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

    public Collection<String> getAmne() {
        return amne;
    }

}
