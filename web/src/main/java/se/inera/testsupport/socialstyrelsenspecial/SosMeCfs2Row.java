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
package se.inera.testsupport.socialstyrelsenspecial;

import se.inera.statistics.service.report.model.Kon;

public class SosMeCfs2Row {

    private Kon kon;
    private String ageGroup;
    private int amount;
    private double median;
    private double q1;
    private double q3;

    public SosMeCfs2Row(Kon kon, String ageGroup, int amount, double median, double q1, double q3) {
        this.kon = kon;
        this.ageGroup = ageGroup;
        this.amount = amount;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
    }

    public Kon getKon() {
        return kon;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public int getAmount() {
        return amount;
    }

    public double getMedian() {
        return median;
    }

    public double getQ1() {
        return q1;
    }

    public double getQ3() {
        return q3;
    }

}
