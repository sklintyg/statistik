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

public class AndelExtras {

    private int femaleIntyg;
    private int femaleKompl;
    private int maleIntyg;
    private int maleKompl;

    public AndelExtras(int femaleIntyg, int femaleKompl, int maleIntyg, int maleKompl) {
        this.femaleIntyg = femaleIntyg;
        this.femaleKompl = femaleKompl;
        this.maleIntyg = maleIntyg;
        this.maleKompl = maleKompl;
    }

    public int getPart() {
        return femaleKompl + maleKompl;
    }

    public int getWhole() {
        return femaleIntyg + maleIntyg;
    }

    public int getFemaleIntyg() {
        return femaleIntyg;
    }

    public int getFemaleKompl() {
        return femaleKompl;
    }

    public int getMaleIntyg() {
        return maleIntyg;
    }

    public int getMaleKompl() {
        return maleKompl;
    }

    public static AndelExtras combined(AndelExtras a, AndelExtras b) {
        return new AndelExtras(a.getFemaleIntyg() + b.getFemaleIntyg(), a.getFemaleKompl() + b.getFemaleKompl(),
            a.getMaleIntyg() + b.getMaleIntyg(), a.getMaleKompl() + b.getMaleKompl());
    }

}
