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
package se.inera.statistics.web.service.interceptor;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;

public class LogInInterceptor extends LoggingInInterceptor {

    private static final String LOOKUP_CONTENTTYPE = "Content-Type:";
    private static final String LOOKUP_PDF = "pdf=";
    private static final List<String> LOOKUP_LIST = Arrays.asList(LOOKUP_CONTENTTYPE, LOOKUP_PDF);

    @Override
    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        return removePayload(loggingMessage.toString());
    }

    private String removePayload(String str) {
        StringBuilder builder = new StringBuilder(str);

        int lookupIndex = lookupContentIndex(str);
        if (lookupIndex > -1) {
            builder.setLength(lookupIndex);
            builder.append("<rest of content skipped>\n");
            final int repeatTimes = 25;
            builder.append(Strings.repeat("-", repeatTimes));
        }
        return builder.toString();
    }

    private int lookupContentIndex(String payload) {
        final int payloadIndex = payload.indexOf("Payload:");

        int index = -1;
        if (payloadIndex > -1) {
            for (String s : LOOKUP_LIST) {
                index = payload.indexOf(s, payloadIndex);
                if (index > -1) {
                    return index;
                }
            }
        }

        return index;
    }

}
