/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.inera.statistics.service.report.model.Lan;

import java.util.Map;

public class SosCalculatedRow {

    private String diagnos;
    private Number totalt;
    private Number kvinnor;
    private Number man;
    private Number blekingeLan;
    private Number dalarnasLan;
    private Number hallandsLan;
    private Number kalmarLan;
    private Number kronobergsLan;
    private Number gotlandsLan;
    private Number gavleborgsLan;
    private Number jamtlandsLan;
    private Number jonkopingsLan;
    private Number norrbottensLan;
    private Number skaneLan;
    private Number stockholmsLan;
    private Number sodermanlandsLan;
    private Number uppsalaLan;
    private Number varmlandsLan;
    private Number vasterbottensLan;
    private Number vasternorrlandsLan;
    private Number vastmanlandsLan;
    private Number vastraGotalandsLan;
    private Number orebroLan;
    private Number ostergotlandsLan;
    private Number okantLan;

    /**
     * @param lanNumbers Map of lan id as key and the number as value
     */
    public SosCalculatedRow(String diagnos, Number totalt, Number kvinnor, Number man, Map<String, Number> lanNumbers) {
        this.diagnos = diagnos;
        this.totalt = totalt;
        this.kvinnor = kvinnor;
        this.man = man;
        this.blekingeLan = lanNumbers.get("10");
        this.dalarnasLan = lanNumbers.get("20");
        this.hallandsLan = lanNumbers.get("13");
        this.kalmarLan = lanNumbers.get("08");
        this.kronobergsLan = lanNumbers.get("07");
        this.gotlandsLan = lanNumbers.get("09");
        this.gavleborgsLan = lanNumbers.get("21");
        this.jamtlandsLan = lanNumbers.get("23");
        this.jonkopingsLan = lanNumbers.get("06");
        this.norrbottensLan = lanNumbers.get("25");
        this.skaneLan = lanNumbers.get("12");
        this.stockholmsLan = lanNumbers.get("01");
        this.sodermanlandsLan = lanNumbers.get("04");
        this.uppsalaLan = lanNumbers.get("03");
        this.varmlandsLan = lanNumbers.get("17");
        this.vasterbottensLan = lanNumbers.get("24");
        this.vasternorrlandsLan = lanNumbers.get("22");
        this.vastmanlandsLan = lanNumbers.get("19");
        this.vastraGotalandsLan = lanNumbers.get("14");
        this.orebroLan = lanNumbers.get("18");
        this.ostergotlandsLan = lanNumbers.get("05");
        this.okantLan = lanNumbers.get(Lan.OVRIGT_ID);
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

    public Number getBlekingeLan() {
        return blekingeLan;
    }

    public Number getDalarnasLan() {
        return dalarnasLan;
    }

    public Number getHallandsLan() {
        return hallandsLan;
    }

    public Number getKalmarLan() {
        return kalmarLan;
    }

    public Number getKronobergsLan() {
        return kronobergsLan;
    }

    public Number getGotlandsLan() {
        return gotlandsLan;
    }

    public Number getGavleborgsLan() {
        return gavleborgsLan;
    }

    public Number getJamtlandsLan() {
        return jamtlandsLan;
    }

    public Number getJonkopingsLan() {
        return jonkopingsLan;
    }

    public Number getNorrbottensLan() {
        return norrbottensLan;
    }

    public Number getSkaneLan() {
        return skaneLan;
    }

    public Number getStockholmsLan() {
        return stockholmsLan;
    }

    public Number getSodermanlandsLan() {
        return sodermanlandsLan;
    }

    public Number getUppsalaLan() {
        return uppsalaLan;
    }

    public Number getVarmlandsLan() {
        return varmlandsLan;
    }

    public Number getVasterbottensLan() {
        return vasterbottensLan;
    }

    public Number getVasternorrlandsLan() {
        return vasternorrlandsLan;
    }

    public Number getVastmanlandsLan() {
        return vastmanlandsLan;
    }

    public Number getVastraGotalandsLan() {
        return vastraGotalandsLan;
    }

    public Number getOrebroLan() {
        return orebroLan;
    }

    public Number getOstergotlandsLan() {
        return ostergotlandsLan;
    }

    public Number getOkantLan() {
        return okantLan;
    }

}
