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
package se.inera.statistics.web.error;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import se.inera.statistics.web.Messages;

/**
 * @author Magnus Ekstrand on 2016-11-09.
 */
public final class Message implements Serializable {

    private ErrorType type;
    private ErrorSeverity severity;
    private int priority;
    private String message;

    private Message(ErrorType type, ErrorSeverity severity, String message, int prio) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.priority = prio;
    }

    /**
     * Method creates a Message object. If argument text is null or
     * empty, a null obejct will be returned.
     *
     * @param type the type of error
     * @param severity the severity of the error
     * @param text the error message, cannot be null or empty string
     * @return Returns a Message object if all arguments are set, otherwise null
     */
    public static Message create(ErrorType type, ErrorSeverity severity, String text) {
        if (type == null || severity == null || text == null || text.isEmpty()) {
            return null;
        }
        return new Message(type, severity, text, Messages.ALWAYS_SHOW);
    }

    public static Message create(ErrorType type, ErrorSeverity severity, Messages message) {
        if (type == null || severity == null || message == null) {
            return null;
        }
        return new Message(type, severity, message.getText(), message.getPrio());
    }

    public ErrorType getType() {
        return type;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public static List<Message> filterByPrio(List<Message> msgs) {
        final int highestPrio = getHighestPrioInList(msgs);
        return msgs.stream()
            .filter(message -> message.priority == Messages.ALWAYS_SHOW || message.priority >= highestPrio)
            .collect(Collectors.toList());
    }

    private static int getHighestPrioInList(List<Message> msgs) {
        int highestPrio = -1;
        for (Message msg : msgs) {
            highestPrio = msg.priority > highestPrio ? msg.priority : highestPrio;
        }
        return highestPrio;
    }

    @Override
    public String toString() {
        return "Message{"
            + "type=" + type
            + ", severity=" + severity
            + ", message='" + message + '\''
            + "}";
    }

}
