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

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class FilterHashHandler {

    //TODO This is just a simple "mock". To be used until the real handler is implemented.

    private static Map<String, String> filterHashes = new HashMap<>(); //<hash, filterData>

    synchronized String getHash(String filterData) {
        try {
            final String s = DigestUtils.md5Hex(filterData);
            if (!filterHashes.containsKey(s)) {
                filterHashes.put(s, filterData);
            }
            return s;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String getFilterData(String hash) {
        return filterHashes.get(hash);
    }

}
