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

package se.inera.statistics.web.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCache;

@ExtendWith(MockitoExtension.class)
class TaskCoordinatorServiceImplTest {

    private static final String REDIS_CACHE_KEY = "CURRENT_TASKS";
    private ConcurrentMapCache taskCoordinatorCache;

    private ObjectMapper objectMapper;

    private TaskCoordinatorServiceImpl taskCoordinatorService;

    private static final String SESSION_ID = "sessionId";
    private List request;

    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        taskCoordinatorCache = new ConcurrentMapCache("test-cache");
        objectMapper = new ObjectMapper();
        taskCoordinatorService = new TaskCoordinatorServiceImpl(
            taskCoordinatorCache,
            objectMapper
        );
        request = new ArrayList<String>();
        request.add(httpServletRequest);
    }

    @Nested
    class UserHasNoCurrentRequestStoredInRedis {

        @BeforeEach
        void setUp() {
            request = new ArrayList<String>();
        }

        @Test
        void shouldReturnTaskCoordinatorAccepted() {
            request.add(httpServletRequest);
            when(httpServletRequest.getSession()).thenReturn(session);
            when(session.getId()).thenReturn(SESSION_ID);
            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.ACCEPTED, response);
        }

        //Possibly redundant test
        @Test
        void shouldReturnTaskCoordinatorAcceptedIfNoSessionWasFound() {
            String notHttpServletRequest = "notHttpServletRequest";
            request.add(notHttpServletRequest);
            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.ACCEPTED, response);
        }
    }

    @Nested
    class UserHasCurrentlyOngoingRequestsStoredInRedis {

        @BeforeEach
        void setUp() {
            when(httpServletRequest.getSession()).thenReturn(session);
            when(session.getId()).thenReturn(SESSION_ID);
        }

        @Test
        void shouldReturnTaskCoordinatorAcceptedIfOnlyOneRequestIsFound() throws JsonProcessingException {
            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(SESSION_ID))));
            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.ACCEPTED, response);
        }

        @Test
        void shouldReturnTaskCoordinatorDeniedIfTwoRequestIsFound() throws JsonProcessingException {
            final var sessionIdsCurrentlyStoredInRedis = new ArrayList<>(Arrays.asList(SESSION_ID, SESSION_ID));
            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(sessionIdsCurrentlyStoredInRedis));
            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.DECLINED, response);
        }
    }

    @Nested
    class MultipleUsersHasyOngoingRequestsStoredInRedis {

        @BeforeEach
        void setUp() {
            when(httpServletRequest.getSession()).thenReturn(session);
            when(session.getId()).thenReturn(SESSION_ID);
        }

        private static final String ANOTHER_SESSION_ID = "anotherSessionId";

        @Test
        void shouldReturnTaskCoordinatorAcceptedIfOneRequestIsFoundForUser() throws JsonProcessingException {
            final var sessionIdsCurrentlyStoredInRedis = new ArrayList<>(Arrays.asList(SESSION_ID, ANOTHER_SESSION_ID));
            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(sessionIdsCurrentlyStoredInRedis));

            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.ACCEPTED, response);
        }

        @Test
        void shouldReturnTaskCoordinatorAcceptedIfSessionIdsDoesNotMatchWithUser() throws JsonProcessingException {
            final var sessionIdsCurrentlyStoredInRedis = new ArrayList<>(Arrays.asList(SESSION_ID, ANOTHER_SESSION_ID, ANOTHER_SESSION_ID));

            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(sessionIdsCurrentlyStoredInRedis));

            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.ACCEPTED, response);
        }

        @Test
        void shouldReturnTaskCoordinatorDeniedIfSessionIdsMatchWithUser() throws JsonProcessingException {
            final var sessionIdsCurrentlyStoredInRedis = new ArrayList<>(Arrays.asList(SESSION_ID, SESSION_ID, ANOTHER_SESSION_ID));

            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(sessionIdsCurrentlyStoredInRedis));

            final var response = taskCoordinatorService.request(request);
            assertEquals(TaskCoordinatorResponse.DECLINED, response);
        }
    }
}
