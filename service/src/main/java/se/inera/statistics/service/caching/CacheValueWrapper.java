/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.io.Serializable;

/**
 * This wrapper object contains both the cached value and the keys to locate it. This class is used as a cure for the
 * symptom where the wrong (in some cases empty) reports are returned to the user. By saving the keys together with
 * the value it can be verified that the expected value object is returned from the cache.
 */
public class CacheValueWrapper<T> implements Serializable {

    private static final long serialVersionUID = 3504793853836631929L;
    private String key;
    private String hashKey;
    private T value;

    CacheValueWrapper(String key, String hashKey, T value) {
        this.key = key;
        this.hashKey = hashKey;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getHashKey() {
        return hashKey;
    }

    public T getValue() {
        return value;
    }

    public boolean isMatch(String key, String hashKey) {
        return key != null && key.equals(this.key) && hashKey != null && hashKey.equals(this.hashKey);
    }

}
