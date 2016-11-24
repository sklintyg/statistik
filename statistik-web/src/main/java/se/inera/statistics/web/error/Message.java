/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.error;

/**
 * @author Magnus Ekstrand on 2016-11-09.
 */
public final class Message {

    private ErrorType type = null;
    private ErrorSeverity severity = null;

    private String message = null;

    private Message(ErrorType type, ErrorSeverity severity, String message) {
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    public static Message create(ErrorType type, ErrorSeverity severity, String message) {
        if (message == null) {
            throw new IllegalArgumentException("Argument message cannot be null");
        }
        return new Message(type, severity, message);
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

    @Override
    public String toString() {
        return "Message{"
                + "type=" + type
                + ", severity=" + severity
                + ", message='" + message + '\''
                + "}";
    }

}
