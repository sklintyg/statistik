/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.model.overview;

public class SjukfallPerManadOverview {

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
