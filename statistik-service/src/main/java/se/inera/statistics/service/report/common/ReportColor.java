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
package se.inera.statistics.service.report.common;

/**
 * From https://inera-certificate.atlassian.net/wiki/spaces/IT/pages/64323885/Intygsstatistik+F+rger page.
 */
public enum ReportColor {

    ST_COLOR_01("#E11964"),
    ST_COLOR_02("#032C53"),
    ST_COLOR_03("#FFBA3E"),
    ST_COLOR_04("#799745"),
    ST_COLOR_05("#3CA3FF"),
    ST_COLOR_06("#C37EB2"),
    ST_COLOR_07("#2A5152"),
    ST_COLOR_08("#FB7F4D"),
    ST_COLOR_09("#5CC2BC"),
    ST_COLOR_10("#704F38"),
    ST_COLOR_11("#600030"),
    ST_COLOR_12("#006697"),
    ST_COLOR_13("#5D5D5D"),
    ST_COLOR_14("#EA8025"),
    ST_COLOR_15("#008391"),
    ST_COLOR_16("#57843B"),
    ST_COLOR_17("#FFFFFF"),
    ST_COLOR_18("#F9F9F9"),
    ST_COLOR_19("#F5F5F5"),
    ST_COLOR_20("#E6E9ED"),
    ST_COLOR_21("#E4E4E4"),
    ST_COLOR_22("#D4D4D4"),
    ST_COLOR_23("#CCCCCC"),
    ST_COLOR_24("#BCBCBC"),
//    ST_COLOR_25("Graditen FFFFFF (top) till E0E0E0 (bottom)"),
    ST_COLOR_26("#D2E7F4"),
    ST_COLOR_27("#28B4C4"),
//    ST_COLOR_28("Gradient #008291 (top) till #00697E (bottom)"),
    ST_COLOR_29("#0F6B76"),
//    ST_COLOR_30("Gradient #5CB85C (top) till #398439 (bottom)"),
    ST_COLOR_31("#5CB85C"),
    ST_COLOR_32("#398439"),
    ST_COLOR_33("#3E8F3E"),
    ST_COLOR_34("#333333"),
    ST_COLOR_35("#212633");

    private final String color;

    ReportColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

}