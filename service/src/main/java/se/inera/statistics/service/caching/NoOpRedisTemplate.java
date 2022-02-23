/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.caching;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Used when caching is not enabled.
 */
public class NoOpRedisTemplate extends RedisTemplate<Object, Object> {

    public NoOpRedisTemplate() {
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public Set keys(Object pattern) {
        return Collections.emptySet();
    }

    @Override
    public Boolean delete(Object key) {
        return null;
    }

    @Override
    public Long delete(Collection<Object> keys) {
        return null;
    }

    @Override
    public Boolean expire(Object key, final long timeout, final TimeUnit unit) {
        return false;
    }

    @Override
    public ValueOperations opsForValue() {

        return new ValueOperations<Object, Object>() {

            @Override
            public void set(Object key, Object value) {
            }

            @Override
            public void set(Object key, Object value, long timeout, TimeUnit unit) {
            }

            @Override
            public Boolean setIfAbsent(Object key, Object value) {
                return false;
            }

            @Override
            public Boolean setIfAbsent(Object key, Object value, long timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public Boolean setIfPresent(Object key, Object value) {
                return null;
            }

            @Override
            public Boolean setIfPresent(Object key, Object value, long timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public void multiSet(Map<?, ?> map) {
            }

            @Override
            public Boolean multiSetIfAbsent(Map<?, ?> map) {
                return false;
            }

            @Override
            public Object get(Object key) {
                return null;
            }

            @Override
            public Object getAndDelete(Object key) {
                return null;
            }

            @Override
            public Object getAndExpire(Object key, long timeout, TimeUnit unit) {
                return null;
            }

            @Override
            public Object getAndExpire(Object key, Duration timeout) {
                return null;
            }

            @Override
            public Object getAndPersist(Object key) {
                return null;
            }

            @Override
            public Object getAndSet(Object key, Object value) {
                return null;
            }

            @Override
            public List<Object> multiGet(Collection<Object> keys) {
                return Collections.emptyList();
            }

            @Override
            public Long increment(Object key) {
                return null;
            }

            @Override
            public Long increment(Object key, long delta) {
                return null;
            }

            @Override
            public Double increment(Object key, double delta) {
                return null;
            }

            @Override
            public Long decrement(Object key) {
                return null;
            }

            @Override
            public Long decrement(Object key, long delta) {
                return null;
            }

            @Override
            public Integer append(Object key, String value) {
                return null;
            }

            @Override
            public String get(Object key, long start, long end) {
                return null;
            }

            @Override
            public void set(Object key, Object value, long offset) {
            }

            @Override
            public Long size(Object key) {
                return null;
            }

            @Override
            public Boolean setBit(Object key, long offset, boolean value) {
                return null;
            }

            @Override
            public Boolean getBit(Object key, long offset) {
                return null;
            }

            @Override
            public List<Long> bitField(Object key, BitFieldSubCommands subCommands) {
                return null;
            }

            @Override
            public RedisOperations<Object, Object> getOperations() {
                return NoOpRedisTemplate.this;
            }
        };
    }

}
