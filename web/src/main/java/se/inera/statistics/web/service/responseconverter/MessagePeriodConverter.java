/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.responseconverter;

public final class MessagePeriodConverter extends SimpleDualSexConverter {

    private MessagePeriodConverter(String tableGroupTitle, String seriesNameTemplate, String totalColumnName,
            String femaleColumnName, String maleColumnName) {
        super(tableGroupTitle, seriesNameTemplate, totalColumnName, femaleColumnName, maleColumnName);
    }

    public static MessagePeriodConverter newTidsserie() {
        return new MessagePeriodConverter("Period",
                "%1$s",
                "Antal meddelanden totalt",
                "Antal meddelanden för kvinnor",
                "Antal meddelanden för män");
    }

    public static MessagePeriodConverter newTvarsnitt() {
        return new MessagePeriodConverter("",
                "%1$s",
                "Antal meddelanden totalt",
                "Antal meddelanden för kvinnor",
                "Antal meddelanden för män");
    }

}
