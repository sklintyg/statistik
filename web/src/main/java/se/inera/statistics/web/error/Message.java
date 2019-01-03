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
package se.inera.statistics.web.error;

import java.io.Serializable;

/**
 * @author Magnus Ekstrand on 2016-11-09.
 */
public final class Message implements Serializable {

    private ErrorType type = null;
    private ErrorSeverity severity = null;

    private String message = null;

    private Message(ErrorType type, ErrorSeverity severity, String message) {
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    /**
     * Method creates a Message object. If argument text is null or
     * empty, a null obejct will be returned.
     *
     * @param type the type of error
     * @param severity the severity of the error
     * @param text the error message, cannot be null or empty string
     *
     * @return Returns a Message object if all arguments are set, otherwise null
     */
    public static Message create(ErrorType type, ErrorSeverity severity, String text) {
        if (type == null || severity == null || text == null || text.isEmpty()) {
            return null;
        }
        return new Message(type, severity, text);
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
