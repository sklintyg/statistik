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
package se.inera.statistics.service.warehouse.message;

import se.inera.statistics.service.report.common.ReportColor;

public enum MsgAmne {

    AVSTMN("Avstämningsmöte", ReportColor.ST_COLOR_01, true),
    KOMPLT("Komplettering", ReportColor.ST_COLOR_02, true),
    KONTKT("Kontakt", ReportColor.ST_COLOR_03, true),
    OKANT("Okänt", ReportColor.ST_COLOR_13, false),
    PAMINN("Påminnelse", ReportColor.ST_COLOR_04, true),
    OVRIGT("Övrigt", ReportColor.ST_COLOR_05, true);

    private final String text;
    private final ReportColor color;
    private final boolean showEmpty; //Should the data series be shown in the result if the result is empty

    MsgAmne(String text, ReportColor color, boolean showEmpty) {
        this.text = text;
        this.color = color;
        this.showEmpty = showEmpty;
    }

    public String getText() {
        return text;
    }

    public ReportColor getColor() {
        return color;
    }

    public boolean isShowEmpty() {
        return showEmpty;
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
