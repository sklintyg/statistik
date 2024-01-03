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

package se.inera.statistics.web.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import se.inera.statistics.web.service.task.TaskCoordinatorResponse;
import se.inera.statistics.web.service.task.TaskCoordinatorServiceImpl;

@ExtendWith(MockitoExtension.class)
class TaskCoordinatorServiceImplTest {

    private ConcurrentMapCache taskCoordinatorCache;

    private ObjectMapper objectMapper;

    private TaskCoordinatorServiceImpl taskCoordinatorService;
    private final int simultaneousCallsAllowed = 2;
    private static final String USER_HSA_ID = "userHsaId";

    @BeforeEach
    void setUp() {
        taskCoordinatorCache = new ConcurrentMapCache("test-cache");
        objectMapper = new ObjectMapper();
        taskCoordinatorService = new TaskCoordinatorServiceImpl(
            simultaneousCallsAllowed,
            taskCoordinatorCache,
            objectMapper
        );
    }

    @Nested
    class Request {

        @Nested
        class UserHasNoCurrentRequestStoredInRedis {

            @Test
            void shouldReturnTaskCoordinatorAccepted() {
                final var response = taskCoordinatorService.request(USER_HSA_ID);
                assertEquals(TaskCoordinatorResponse.ACCEPT, response);
            }
        }

        @Nested
        class UserHasCurrentlyOngoingRequestsStoredInRedis {

            @Test
            void shouldReturnTaskCoordinatorAcceptedIfOnlyOneRequestIsFound() throws JsonProcessingException {
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(1));
                final var response = taskCoordinatorService.request(USER_HSA_ID);
                assertEquals(TaskCoordinatorResponse.ACCEPT, response);
            }

            @Test
            void shouldReturnTaskCoordinatorDeniedIfTwoRequestIsFound() throws JsonProcessingException {
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(2));
                final var response = taskCoordinatorService.request(USER_HSA_ID);
                assertEquals(TaskCoordinatorResponse.DECLINE, response);
            }
        }

        @Nested
        class MultipleUsersHasyOngoingRequestsStoredInRedis {

            private static final String ANOTHER_USER_HSA_ID = "anotherUserHsaId";

            @Test
            void shouldReturnTaskCoordinatorAcceptedIfOneRequestIsFoundForUser() throws JsonProcessingException {
                taskCoordinatorCache.put(ANOTHER_USER_HSA_ID, objectMapper.writeValueAsString(2));
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(1));

                final var response = taskCoordinatorService.request(USER_HSA_ID);
                assertEquals(TaskCoordinatorResponse.ACCEPT, response);
            }

            @Test
            void shouldReturnTaskCoordinatorDeniedIfTwoRequestIsFoundForUser() throws JsonProcessingException {
                taskCoordinatorCache.put(ANOTHER_USER_HSA_ID, objectMapper.writeValueAsString(1));
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(2));

                final var response = taskCoordinatorService.request(USER_HSA_ID);
                assertEquals(TaskCoordinatorResponse.DECLINE, response);
            }
        }

        @Nested
        class RedisCacheGetsUpdatedCorrectly {

            @Test
            void shouldNotAddNewRequestIfDeclined() throws JsonProcessingException {
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(2));
                taskCoordinatorService.request(USER_HSA_ID);

                final var requestsStoredInRedis = Integer.parseInt(taskCoordinatorCache.get(USER_HSA_ID, String.class));

                assertEquals(2, requestsStoredInRedis);
            }

            @Test
            void shouldAddNewRequestIfAccepted() throws JsonProcessingException {
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(1));
                taskCoordinatorService.request(USER_HSA_ID);

                final var requestsStoredInRedis = Integer.parseInt(taskCoordinatorCache.get(USER_HSA_ID, String.class));

                assertEquals(2, requestsStoredInRedis);
            }
        }

        @Nested
        class UnlimitedRequests {

            @BeforeEach
            void setUp() {
                taskCoordinatorCache = new ConcurrentMapCache("test-cache");
                objectMapper = new ObjectMapper();
                taskCoordinatorService = new TaskCoordinatorServiceImpl(
                    -1,
                    taskCoordinatorCache,
                    objectMapper
                );
            }

            @Test
            void shouldAcceptIfSimultaneousCallsAllowedSetToMinusOne() throws JsonProcessingException {
                taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(5));
                final var request = taskCoordinatorService.request(USER_HSA_ID);

                assertEquals(TaskCoordinatorResponse.ACCEPT, request);
            }
        }
    }

    @Nested
    class ClearRequests {

        private static final String ANOTHER_USER_HSA_ID = "anotherUserHsaId";

        @Test
        void shouldClearRequestFromCache() throws JsonProcessingException {
            taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(1));
            taskCoordinatorService.clearRequest(USER_HSA_ID);

            final var actualResult = Integer.parseInt(taskCoordinatorCache.get(USER_HSA_ID, String.class));

            assertEquals(0, actualResult);
        }

        @Test
        void shouldClearRequestFromCacheIfMultipleRequestsAreStored() throws JsonProcessingException {
            taskCoordinatorCache.put(USER_HSA_ID, objectMapper.writeValueAsString(1));
            taskCoordinatorCache.put(ANOTHER_USER_HSA_ID, objectMapper.writeValueAsString(1));
            taskCoordinatorService.clearRequest(USER_HSA_ID);

            final var actualResultUser = Integer.parseInt(taskCoordinatorCache.get(USER_HSA_ID, String.class));
            final var actualResultAnotherUser = Integer.parseInt(taskCoordinatorCache.get(ANOTHER_USER_HSA_ID, String.class));

            assertAll(
                () -> assertEquals(0, actualResultUser),
                () -> assertEquals(1, actualResultAnotherUser)
            );
        }

        @Test
        void shouldNotDecreaseNumnberOfRequestsIfNoRequestsAreFound() {
            taskCoordinatorService.clearRequest(USER_HSA_ID);

            final var actualResult = Integer.parseInt(taskCoordinatorCache.get(USER_HSA_ID, String.class));

            assertEquals(0, actualResult);
        }
    }
}
