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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.statistik.logging.MdcHelper.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.statistik.logging.MdcHelper.LOG_TRACE_ID_HEADER;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MdcHelperTest {

    private se.inera.intyg.statistik.logging.MdcHelper mdcHelper;

    @BeforeEach
    void setUp() {
        mdcHelper = new se.inera.intyg.statistik.logging.MdcHelper();
    }

    @Nested
    class SessionId {

        @Test
        void shouldReturnTraceIdFromHeader() {
            final var expectedValue = "sessionId";
            final var httpServletRequest = mock(HttpServletRequest.class);
            when(httpServletRequest.getHeader(LOG_SESSION_ID_HEADER)).thenReturn(expectedValue);
            final var result = mdcHelper.sessionId(httpServletRequest);
            assertEquals(expectedValue, result);
        }

        @Test
        void shouldReturnEmptySessionIdIfNotPresentInHeader() {
            final var expectedValue = "-";
            final var httpServletRequest = mock(HttpServletRequest.class);
            final var result = mdcHelper.sessionId(httpServletRequest);
            assertEquals(expectedValue, result);
        }
    }

    @Nested
    class TraceId {

        @Test
        void shouldReturnTraceIdFromHeader() {
            final var expectedValue = "traceId";
            final var httpServletRequest = mock(HttpServletRequest.class);
            when(httpServletRequest.getHeader(LOG_TRACE_ID_HEADER)).thenReturn(expectedValue);
            final var result = mdcHelper.traceId(httpServletRequest);
            assertEquals(expectedValue, result);
        }

        @Test
        void shouldGenerateTraceIdIfNotPresentInHeader() {
            final var httpServletRequest = mock(HttpServletRequest.class);
            final var result = mdcHelper.traceId(httpServletRequest);
            assertNotNull(result);
        }

        @Test
        void shouldGeneratetraceId() {
            final var result = mdcHelper.traceId();
            assertNotNull(result);
        }
    }

    @Nested
    class SpanId {

        @Test
        void shouldGenerateSpanId() {
            final var result = mdcHelper.spanId();
            assertNotNull(result);
        }
    }
}