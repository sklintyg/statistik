/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.util.Map;

public class SosCountRow {

    private String diagnos;
    private Number totalt;
    private Number kvinnor;
    private Number man;
    private Map<SjukfallsLangdGroupSos, Integer> malePerLength;
    private Map<SjukfallsLangdGroupSos, Integer> femalePerLength;

    public SosCountRow(String diagnos, Number totalt, Number kvinnor, Number man,
        Map<SjukfallsLangdGroupSos, Integer> malePerLength, Map<SjukfallsLangdGroupSos, Integer> femalePerLength) {
        this.diagnos = diagnos;
        this.totalt = totalt;
        this.kvinnor = kvinnor;
        this.man = man;
        this.malePerLength = malePerLength;
        this.femalePerLength = femalePerLength;
    }

    public String getDiagnos() {
        return diagnos;
    }

    public Number getTotalt() {
        return totalt;
    }

    public Number getKvinnor() {
        return kvinnor;
    }

    public Number getMan() {
        return man;
    }

    public Integer getMaleByLength(SjukfallsLangdGroupSos length) {
        final Integer count = malePerLength.get(length);
        return count != null ? count : 0;
    }

    public int getFemaleByLength(SjukfallsLangdGroupSos length) {
        final Integer count = femalePerLength.get(length);
        return count != null ? count : 0;
    }

}
