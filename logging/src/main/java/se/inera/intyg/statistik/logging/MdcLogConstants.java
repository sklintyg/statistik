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
package se.inera.intyg.statistik.logging;

public class MdcLogConstants {

    private MdcLogConstants() {

    }

    public static final String EVENT_ACTION = "event.action";
    public static final String EVENT_CATEGORY = "event.category";
    public static final String EVENT_CATEGORY_API = "[api]";
    public static final String EVENT_CATEGORY_PROCESS = "[process]";
    public static final String EVENT_TYPE = "event.type";
    public static final String EVENT_START = "event.start";
    public static final String EVENT_END = "event.end";
    public static final String EVENT_DURATION = "event.duration";
    public static final String EVENT_USER_CARE_PROVIDER_ID = "event.user.care_provider.id";
    public static final String EVENT_CLASS = "event.class";
    public static final String EVENT_LOGIN_METHOD = "event.login.method";
    public static final String EVENT_METHOD = "event.method";
    public static final String EVENT_OUTCOME = "event.outcome";
    public static final String EVENT_OUTCOME_FAILURE = "failure";
    public static final String EVENT_OUTCOME_SUCCESS = "success";
    public static final String USER_ID = "user.id";
    public static final String SESSION_ID_KEY = "session.id";
    public static final String SPAN_ID_KEY = "span.id";
    public static final String TRACE_ID_KEY = "trace.id";

    public static final String EVENT_TYPE_ACCESSED = "accessed";
    public static final String EVENT_TYPE_CHANGE = "change";
    public static final String EVENT_TYPE_CREATION = "creation";
    public static final String EVENT_TYPE_DELETION = "deletion";
    public static final String EVENT_TYPE_INFO = "info";
}