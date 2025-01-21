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
package se.inera.statistics.service.processlog;

import java.time.LocalDate;

public class Arbetsnedsattning {

    private static final int MAX_NEDSATTNING = 100;
    private final int nedsattning;
    private final LocalDate start;
    private final LocalDate slut;

    public Arbetsnedsattning(int nedsattning, LocalDate start, LocalDate slut) {
        this.nedsattning = nedsattning;
        this.start = start;
        this.slut = slut;
    }

    public int getNedsattning() {
        return nedsattning;
    }

    public int getSysselsattningsgrad() {
        return MAX_NEDSATTNING - nedsattning;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getSlut() {
        return slut;
    }
}
