/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model.overview;

import java.io.Serializable;

public class SjukfallPerManadOverview implements Serializable {

    private final int proportionMale;
    private final int proportionFemale;
    private final int alteration;
    private final String previousPeriodText;

    public SjukfallPerManadOverview(int proportionMale, int proportionFemale, int alteration, String previousPeriodText) {
        this.proportionMale = proportionMale;
        this.proportionFemale = proportionFemale;
        this.alteration = alteration;
        this.previousPeriodText = previousPeriodText;
    }

    public int getProportionMale() {
        return proportionMale;
    }

    public int getProportionFemale() {
        return proportionFemale;
    }

    public int getAlteration() {
        return alteration;
    }

    public String getPreviousPeriodText() {
        return previousPeriodText;
    }

}
