/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanCursor;
import org.springframework.data.redis.core.ScanIteration;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used when caching is not enabled.
 */
public class FakeRedisTemplate extends RedisTemplate<Object, Object> {

    public FakeRedisTemplate() {
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public Set keys(Object pattern) {
        return Collections.emptySet();
    }

    @Override
    public void delete(Object key) {
    }

    @Override
    public void delete(Collection keys) {
    }

    @Override
    public HashOperations opsForHash() {
        return new FakeHashOperations();
    }

    private static class FakeHashOperations implements HashOperations {

        @Override
        public Long delete(Object key, Object... hashKeys) {
            return Long.valueOf(hashKeys.length);
        }

        @Override
        public Boolean hasKey(Object key, Object hashKey) {
            return false;
        }

        @Override
        public Object get(Object key, Object hashKey) {
            return null;
        }

        @Override
        public List multiGet(Object key, Collection hashKeys) {
            return Collections.emptyList();
        }

        @Override
        public Long increment(Object key, Object hashKey, long delta) {
            return 0L;
        }

        @Override
        public Double increment(Object key, Object hashKey, double delta) {
            return 0D;
        }

        @Override
        public Set keys(Object key) {
            return Collections.emptySet();
        }

        @Override
        public Long size(Object key) {
            return 0L;
        }

        @Override
        public void putAll(Object key, Map m) {

        }

        @Override
        public void put(Object key, Object hashKey, Object value) {

        }

        @Override
        public Boolean putIfAbsent(Object key, Object hashKey, Object value) {
            return false;
        }

        @Override
        public List values(Object key) {
            return Collections.emptyList();
        }

        @Override
        public Map entries(Object key) {
            return Collections.emptyMap();
        }

        @Override
        public Cursor<Map.Entry> scan(Object key, ScanOptions options) {
            return new ScanCursor() {
                @Override
                protected ScanIteration doScan(long cursorId, ScanOptions options) {
                    return new ScanIteration(0, Collections.emptyList());
                }
            };
        }

        @Override
        public RedisOperations getOperations() {
            return new FakeRedisTemplate();
        }
    }
}
