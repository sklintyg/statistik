/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;

public final class Converters {

    private Converters() {
    }

    public static String combineMessages(String... messages) {
        final String message = Joiner.on(" : ").skipNulls().join(Iterables.filter(Arrays.asList(messages), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s != null && !s.isEmpty();
            }
        }));
        if (message.trim().isEmpty()) {
            return null;
        }
        return message;
    }

}
