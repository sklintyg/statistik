/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.dto;

// CHECKSTYLE:OFF LineLength
public enum Messages {

    ST_F_FI_001(MessagesText.FILTER_WRONG_FROM_DATE, -1),
    ST_F_FI_002(MessagesText.FILTER_WRONG_END_DATE, -1),
    ST_F_FI_003(MessagesText.FILTER_WRONG_FROM_AND_END_DATE, -1),
    ST_F_FI_004(MessagesText.FILTER_TOO_EARLY_FROM_AND_END_DATE, -1),
    //ST_F_FI_005 //defined in frontend
    //ST_F_FI_006 //defined in frontend
    //ST_F_FI_007 //defined in frontend
    ST_F_FI_008(MessagesText.MESSAGE_OVERVIEW_WITH_FILTER, -1),
    ST_F_FI_009(MessagesText.FILTER_TOO_LATE_FROM_AND_END_DATE, -1),
    ST_F_FI_010(MessagesText.FILTER_NO_DATA, 5),
    ST_F_FI_011(MessagesText.MESSAGE_DIAGNOS_MISS_MATCH, 10),
    //ST_F_FI_012 //defined in frontend
    ST_F_FI_013(MessagesText.FILTER_COULD_NOT_APPLY, -1);

    public static final int ALWAYS_SHOW = -1;
    private final String text;
    private final int prio;

    Messages(String text, int prio) {
        this.text = text;
        this.prio = prio;
    }

    public String getText() {
        return text;
    }

    public int getPrio() {
        return prio;
    }
}

// CHECKSTYLE:ON LineLength
