package se.inera.statistics.service.helper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface Matcher {

    boolean match(JsonNode node);
    Matcher add(Matcher matcher);

    class Builder {
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
