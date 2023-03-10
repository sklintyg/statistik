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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

@Component("taskCoordinatorServiceImpl")
public class TaskCoordinatorServiceImpl implements TaskCoordinatorService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskCoordinatorServiceImpl.class);
    private static final int SIMULTANEOUS_CALLS_ALLOWED = 2;
    private static final boolean UNLIMITED_REQUESTS = false;
    private final Cache taskCoordinatorCache;
    private final ObjectMapper objectMapper;

    public TaskCoordinatorServiceImpl(@Qualifier("taskCoordinatorRedisCache") Cache taskCoordinatorCache, ObjectMapper objectMapper) {
        this.taskCoordinatorCache = taskCoordinatorCache;
        this.objectMapper = objectMapper;
    }

    @Override
    public TaskCoordinatorResponse request(String userHsaId) {
        final var cachedRequests = getCachedRequests(userHsaId);
        if (limitForSimultaneousCallsExceeded(cachedRequests)) {
            LOG.debug(
                "Request was declined for {}. Reason for this is that the number of simultaneous call "
                    + "was exceeded. Allowed numbers of simultaneous call is currently set to {}.",
                userHsaId, SIMULTANEOUS_CALLS_ALLOWED);
            return TaskCoordinatorResponse.DECLINE;
        }
        updateCachedRequests(userHsaId, cachedRequests + 1);
        return TaskCoordinatorResponse.ACCEPT;
    }

    @Override
    public void clearRequest(String userHsaId) {
        clearRequestFromCache(userHsaId);
    }

    private static boolean limitForSimultaneousCallsExceeded(int numberOfRequestFromSession) {
        if (UNLIMITED_REQUESTS) {
            return false;
        }
        return numberOfRequestFromSession >= SIMULTANEOUS_CALLS_ALLOWED;
    }

    private void clearRequestFromCache(String userHsaId) {
        final var numberOfRequests = getCachedRequests(userHsaId);
        updateCachedRequests(userHsaId, numberOfRequests > 0 ? numberOfRequests - 1 : 0);
    }

    private void updateCachedRequests(String key, Integer numberOfRequests) {
        try {
            taskCoordinatorCache.put(key, objectMapper.writeValueAsString(numberOfRequests));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer getCachedRequests(String key) {
        return deserialize(
            taskCoordinatorCache.get(key, () -> objectMapper.writeValueAsString(0)));
    }

    private Integer deserialize(String jsonData) {
        try {
            return Integer.parseInt(objectMapper.readValue(jsonData, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
