/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.interceptor;

import com.google.common.base.Strings;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;

public class LogInInterceptor extends LoggingInInterceptor {

    @Override
    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        return removePayload(loggingMessage.toString());
    }

    private String removePayload(String str) {
        StringBuilder builder = new StringBuilder(str);
        final int payloadIndex = str.indexOf("Payload:");
        final int contentTypeIndex = str.indexOf("Content-Type:", payloadIndex);
        if (payloadIndex >= 0 && contentTypeIndex >= 0) {
            builder.setLength(contentTypeIndex);
            builder.append(" <rest of content skipped>\n");
            final int repeatTimes = 25;
            builder.append(Strings.repeat("-", repeatTimes));
        }
        return builder.toString();
    }

}
