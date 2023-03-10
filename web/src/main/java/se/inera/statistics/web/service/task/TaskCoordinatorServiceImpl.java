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

package se.inera.statistics.web.service.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.util.ThreadLocalTimerUtil;

@Component("taskCoordinatorServiceImpl")
public class TaskCoordinatorServiceImpl implements TaskCoordinatorService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskCoordinatorServiceImpl.class);
    private static final String REDIS_CACHE_KEY = "CURRENT_TASKS";
    private static final int SIMULTANEOUS_CALLS_ALLOWED = 2;
    private final Cache taskCoordinatorCache;
    private final ObjectMapper objectMapper;

    public TaskCoordinatorServiceImpl(@Qualifier("taskCoordinatorRedisCache") Cache taskCoordinatorCache, ObjectMapper objectMapper) {
        this.taskCoordinatorCache = taskCoordinatorCache;
        this.objectMapper = objectMapper;
    }

    @Override
    public TaskCoordinatorResponse request(Object request) {
        if (!hasSession(request)) {
            LOG.warn("No session was found for request.");
            return TaskCoordinatorResponse.ACCEPTED;
        }
        final var sessionId = getSessionId(request);
        final var cachedSessionIds = getCachedSession();
        final var numberOfRequestFromSession = getNumberOfRequestFromSession(sessionId, cachedSessionIds);

        if (numberOfRequestFromSession >= SIMULTANEOUS_CALLS_ALLOWED) {
            LOG.debug(
                "Request was declined for {}. Reason for this is that the number of simultaneous call "
                    + "was exceeded. Allowed numbers of simultaneous call is currently set to {}.",
                sessionId, SIMULTANEOUS_CALLS_ALLOWED);
            return TaskCoordinatorResponse.DECLINED;
        }
        cachedSessionIds.add(sessionId);
        updateCachedSessions(cachedSessionIds);
        ThreadLocalTimerUtil.startTimer();
        LOG.debug("Request started for {} and was added to cache. Cache currently holding {} slots. Timestamp when request started: {} ms",
            sessionId, cachedSessionIds.size(), LocalDateTime.now());
        return TaskCoordinatorResponse.ACCEPTED;
    }


    @Override
    public void clearRequest(Object request) {
        final var sessionId = getSessionId(request);
        clearSessionIdFromCache(sessionId);
        LOG.debug("Request completed for {}. Total time taken for request to finish: {} ms.", sessionId, timeElapsed());
        ThreadLocalTimerUtil.removeTimer();
    }

    private long timeElapsed() {
        long endTime = System.currentTimeMillis();
        long startTime = ThreadLocalTimerUtil.getTimer();
        return endTime - startTime;
    }

    private void clearSessionIdFromCache(String sessionId) {
        final var cachedSessionIds = getCachedSession();
        cachedSessionIds.remove(sessionId);
        updateCachedSessions(cachedSessionIds);
    }

    private void updateCachedSessions(List<String> cachedSessionIds) {
        try {
            taskCoordinatorCache.put(REDIS_CACHE_KEY, objectMapper.writeValueAsString(cachedSessionIds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getCachedSession() {
        return deserialize(
            taskCoordinatorCache.get(REDIS_CACHE_KEY, () -> objectMapper.writeValueAsString(new ArrayList<String>())));
    }

    private int getNumberOfRequestFromSession(String sessionId, List<String> cachedSessionIds) {
        return (int) cachedSessionIds.stream()
            .filter(id -> Objects.equals(id, sessionId))
            .count();
    }

    private String getSessionId(Object request) {
        final var requestParams1 = (List) request;
        final var httpServletRequest = (HttpServletRequest) requestParams1.get(0);
        final var session = httpServletRequest.getSession();
        System.out.println(session.getCreationTime());
        return session.getId();
    }

    private static Boolean hasSession(Object request) {
        if (request instanceof List) {
            final var requestParams1 = (List) request;
            return requestParams1.get(0) instanceof HttpServletRequest;
        }
        return false;
    }

    private List<String> deserialize(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
