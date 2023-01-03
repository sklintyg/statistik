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
package se.inera.testsupport;

import se.inera.statistics.service.processlog.message.MessageEventType;

public class Meddelande {

    private MessageEventType type;
    private String data;
    private String messageId;
    private long timestamp;

    public Meddelande() {
    }

    public Meddelande(MessageEventType type, String data, String messageId, long timestamp) {
        this.type = type;
        this.data = data;
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public MessageEventType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getMessageId() {
        return messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
