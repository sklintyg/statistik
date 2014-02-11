/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.testsupport;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface Matcher {

    boolean match(JsonNode node);
    Matcher add(Matcher matcher);

    final class Builder {
        private Builder() {
        }
        public static Matcher matcher(String name, String value) {
            return new ValueMatcher(name, value);
        }
        public static Matcher matcher(String name) {
            return new NodeMatcher(name);
        }
    }

    class NodeMatcher implements Matcher {

        private final List<Matcher> matchers = new ArrayList<Matcher>();
        private final String name;

        public NodeMatcher(String name) {
            this.name = name;
        }

        @Override
        public boolean match(JsonNode node) {
            JsonNode found = node.get(name);
            return found != null && subFind(found);
        }

        private boolean subFind(JsonNode node) {
            for (Matcher matcher: matchers) {
                if (!matcher.match(node)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Matcher add(Matcher matcher) {
            matchers.add(matcher);
            return this;
        }
    }

    class ValueMatcher implements Matcher {

        private final String name;
        private final String value;

        public ValueMatcher(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean match(JsonNode node) {
            return node.findValue(name).textValue().equals(value);
        }

        @Override
        public Matcher add(Matcher matcher) {
            throw new IllegalArgumentException("Can not add matcher to ValueMatcher");
        }
    }
}
