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
package se.inera.statistics.service.warehouse.message;

import se.inera.statistics.service.report.common.ReportColor;

public enum MsgAmne {

    KOMPLT("Komplettering", ReportColor.ST_COLOR_02),
    AVSTMN("Avstämningsmöte", ReportColor.ST_COLOR_01),
    KONTKT("Kontakt", ReportColor.ST_COLOR_03),
    OVRIGT("Övrigt", ReportColor.ST_COLOR_05),
    PAMINN("Påminnelse", ReportColor.ST_COLOR_04),
    OKANT("Okänt", ReportColor.ST_COLOR_13);

    private final String text;
    private final ReportColor color;

    MsgAmne(String text, ReportColor color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public ReportColor getColor() {
        return color;
    }

    public static MsgAmne parse(String amneCode) {
        for (MsgAmne value : values()) {
            if (value.name().equalsIgnoreCase(amneCode)) {
                return value;
            }
        }
        return OKANT;
    }

}
