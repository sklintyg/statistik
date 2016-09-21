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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.userselection.UserSelection;
import se.inera.statistics.service.userselection.UserSelectionManager;

import java.io.IOException;
import java.util.ArrayList;

public class FilterHashHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FilterHashHandler.class);

    @Autowired
    private UserSelectionManager userSelectionManager;

    String getHash(String filterData) throws FilterException {
        try {
            final JsonParser parser = new ObjectMapper().getFactory().createParser(filterData);
            JsonToken token;
            do {
                token = parser.nextToken();
            } while (token != null);

            final String hash = DigestUtils.md5Hex(filterData);
            userSelectionManager.register(hash, filterData);
            return hash;
        } catch (JsonParseException parseException) {
            throw new FilterException("Attempt to store illegal json detected.", parseException);
        } catch (Exception e) {
            throw new FilterException("Illegal user selection", e);
        }
    }

    Optional<String> getFilterData(String hash) {
        final UserSelection userSelection = userSelectionManager.find(hash);
        if (userSelection == null) {
            return Optional.absent();
        }
        return Optional.of(userSelection.getValue());
    }


    FilterData getFilterFromHash(String filterHash) {
        final Optional<String> filterData = getFilterData(filterHash);
        if (!filterData.isPresent()) {
            throw new FilterHashMissingException("Could not find filter with given hash: " + filterHash);
        }
        final String filterDataString = filterData.get();
        return parseFilterData(filterDataString);
    }

    private FilterData parseFilterData(String filterDataString) {
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = null;
            rootNode = m.readTree(filterDataString);
            final ArrayList<String> diagnoser = getJsonArray(rootNode.path("diagnoser"));
            final ArrayList<String> enheter = getJsonArray(rootNode.path("enheter"));
            final ArrayList<String> verksamhetstyper = getJsonArray(rootNode.path("verksamhetstyper"));
            final ArrayList<String> sjukskrivningslangd = getJsonArray(rootNode.path("sjukskrivningslangd"));
            final ArrayList<String> aldersgrupp = getJsonArray(rootNode.path("aldersgrupp"));
            final String fromDate = rootNode.path("fromDate").asText().isEmpty() ? null : rootNode.path("fromDate").asText();
            final String toDate = rootNode.path("toDate").asText().isEmpty() ? null : rootNode.path("toDate").asText();
            final boolean useDefaultPeriod = rootNode.path("useDefaultPeriod").asBoolean(true);
            return new FilterData(diagnoser, enheter, verksamhetstyper, sjukskrivningslangd, aldersgrupp, fromDate, toDate, useDefaultPeriod);
        } catch (IOException e) {
            LOG.error("Failed to parse filter data: " + filterDataString, e);
            throw new FilterHashParseException("Filter data failed");
        }
    }

    private ArrayList<String> getJsonArray(JsonNode jsonArrayNode) {
        final ArrayList<String> values = new ArrayList<>();
        if (jsonArrayNode.isArray()) {
            for (JsonNode value : jsonArrayNode) {
                values.add(value.asText());
            }
        }
        return values;
    }

}
