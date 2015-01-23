/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import java.util.HashMap;
import java.util.Map;

public class FilterHashHandler {

    //TODO This is just a simple "mock". To be used until the real handler is implemented.

    private static Map<String, String> filterHashes = new HashMap<>(); //<hash, filterData>

    synchronized String getHash(String filterData) {
        final String hashValue = String.valueOf(filterHashes.size());
        filterHashes.put(hashValue, filterData);
        return hashValue;
    }

    String getFilterData(String hash) {
        return filterHashes.get(hash);
    }

}
