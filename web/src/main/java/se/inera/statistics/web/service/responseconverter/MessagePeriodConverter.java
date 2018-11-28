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
package se.inera.statistics.web.service.responseconverter;

import se.inera.statistics.web.MessagesText;

public final class MessagePeriodConverter extends SimpleDualSexConverter {

    private MessagePeriodConverter(String tableGroupTitle, String seriesNameTemplate, String totalColumnName,
            String femaleColumnName, String maleColumnName) {
        super(tableGroupTitle, seriesNameTemplate, totalColumnName, femaleColumnName, maleColumnName);
    }

    public static MessagePeriodConverter newTidsserie() {
        return new MessagePeriodConverter(MessagesText.REPORT_PERIOD,
                "%1$s",
                MessagesText.REPORT_ANTAL_MEDDELANDEN_TOTALT,
                MessagesText.REPORT_COLUMN_ANTAL_MESSAGES_FEMALE,
                MessagesText.REPORT_COLUMN_ANTAL_MESSAGES_MALE);
    }

    public static MessagePeriodConverter newTvarsnitt() {
        return new MessagePeriodConverter("",
                "%1$s",
                MessagesText.REPORT_ANTAL_MEDDELANDEN_TOTALT,
                MessagesText.REPORT_COLUMN_ANTAL_MESSAGES_FEMALE,
                MessagesText.REPORT_COLUMN_ANTAL_MESSAGES_MALE);
    }

}
