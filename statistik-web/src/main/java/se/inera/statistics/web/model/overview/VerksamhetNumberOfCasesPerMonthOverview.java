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

public class VerksamhetNumberOfCasesPerMonthOverview {

    private final int amountMaleNew;
    private final int amountFemaleNew;
    private final String newPeriod;
    private final int amountMaleOld;
    private final int amountFemaleOld;
    private final String oldPeriod;
    private final int totalCases;

    public VerksamhetNumberOfCasesPerMonthOverview(int amountMaleNew, int amountFemaleNew, String newPeriod, int amountMaleOld, int amountFemaleOld,
            String oldPeriod, int totalCases) {
        this.amountMaleNew = amountMaleNew;
        this.amountFemaleNew = amountFemaleNew;
        this.newPeriod = newPeriod;
        this.amountMaleOld = amountMaleOld;
        this.amountFemaleOld = amountFemaleOld;
        this.oldPeriod = oldPeriod;
        this.totalCases = totalCases;
    }

    public String getNewPeriod() {
        return newPeriod;
    }

    public String getOldPeriod() {
        return oldPeriod;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getAmountMaleNew() {
        return amountMaleNew;
    }

    public int getAmountFemaleNew() {
        return amountFemaleNew;
    }

    public int getAmountMaleOld() {
        return amountMaleOld;
    }

    public int getAmountFemaleOld() {
        return amountFemaleOld;
    }

}
