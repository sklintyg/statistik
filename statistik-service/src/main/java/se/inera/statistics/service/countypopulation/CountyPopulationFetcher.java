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
package se.inera.statistics.service.countypopulation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonField;

@Component
public class CountyPopulationFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(CountyPopulationFetcher.class);

    @Autowired
    private RestTemplate rest;

    @Value("classpath:scb-county-population-request.json")
    private Resource scbCountyPopulationRequestResource;

    @Value("${scb.population.url}")
    private String scbPopulationUrl;

    /**
     * Fetch population per county from SCb for a specific year.
     * @param year Which years population will be fetched
     * @return An empty optional if no population for the requested yer was found, otherwise the populations per county
     */
    public Optional<CountyPopulation> getPopulationFor(int year) {
        try {
            final Map<String, KonField> populationFromScb = getPopulationFromScb(year);
            final java.time.LocalDate date = java.time.LocalDate.of(year, 12, 31);
            final CountyPopulation countyPopulation = new CountyPopulation(populationFromScb, date);
            return Optional.of(countyPopulation);
        } catch (Exception e) {
            LOG.error("Failed to get county population from SCB for " + year, e);
            return Optional.empty();
        }
    }

    private Map<String, KonField> getPopulationFromScb(int year) throws ScbPopulationException {
        final String scbPopulationResponse = requestPopulationFromScb(year);
        return parseScbResult(scbPopulationResponse, year);
    }

    private Map<String, KonField> parseScbResult(String result, int year) throws ScbResponseHeadersNotRecognisedException {
        final String[] rows = result.split("(\\r\\n|\\r|\\n)");
        validateScbResponseHeaders(rows[0], year);
        final Map<String, List<ScbRow>> rowsPerCounty = Arrays.stream(rows).skip(1).map(this::parseScbRow).collect(Collectors.groupingBy(scbRow -> scbRow.countyId));
        return rowsPerCounty.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> toKonField(e.getValue())));
    }

    private void validateScbResponseHeaders(String row, int year) throws ScbResponseHeadersNotRecognisedException {
        final String[] headers = splitStringAndRemoveQuotationMarks(row);
        if (!"region".equalsIgnoreCase(headers[0])) {
            throw new ScbResponseHeadersNotRecognisedException("Region header [0] mismatch");
        }
        if (!"kön".equalsIgnoreCase(headers[1])) {
            throw new ScbResponseHeadersNotRecognisedException("Gender header [1] mismatch");
        }
        if (!("Folkmängd " + year).equalsIgnoreCase(headers[2])) {
            throw new ScbResponseHeadersNotRecognisedException("Population header [2] mismatch");
        }
    }

    private KonField toKonField(List<ScbRow> konValuesForSingleCounty) throws ScbPopulationException {
        final HashMap<Kon, Integer> valuePerKon = new HashMap<>();
        for (ScbRow scbRow : konValuesForSingleCounty) {
            valuePerKon.put(scbRow.gender, scbRow.amount);
        }
        if (!valuePerKon.containsKey(Kon.Female) || !valuePerKon.containsKey(Kon.Male)) {
            final String countyId = konValuesForSingleCounty.isEmpty() ? "?" : konValuesForSingleCounty.get(0).countyId;
            throw new ScbPopulationException("Can't find population for both genders on county: " + countyId);
        }
        return new KonField(valuePerKon.get(Kon.Female), valuePerKon.get(Kon.Male));
    }

    private ScbRow parseScbRow(String row) {
        final String[] rowFields = splitStringAndRemoveQuotationMarks(row);
        final String countyId = rowFields[0].substring(0, 2);
        if (!countyId.matches("^[0-9][0-9]$")) {
            throw new ScbPopulationException("County id should be an integer with two digits: " + countyId);
        }
        final Kon gender = rowFields[1].contains("kvinnor") ? Kon.Female : Kon.Male;
        final Integer amount = Integer.valueOf(rowFields[2]);
        return new ScbRow(countyId, gender, amount);
    }

    private String[] splitStringAndRemoveQuotationMarks(String row) {
        return Arrays.stream(row.split(",")).map(str -> str.replaceAll("\"", "")).collect(Collectors.toList()).toArray(new String[0]);
    }

    private final class ScbRow {
        private String countyId;
        private Kon gender;
        private int amount;

        private ScbRow(String countyId, Kon gender, int amount) {
            this.countyId = countyId;
            this.gender = gender;
            this.amount = amount;
        }
    }

    private String requestPopulationFromScb(int year) {
        try {
            String body = getRequestBody(year);
            final String response = rest.postForObject(scbPopulationUrl, body, String.class);
            if (response == null) {
                throw new ScbPopulationException("Null returned from SCB for county population for " + year);
            }
            return response;
        } catch (RestClientException e) {
            throw new ScbPopulationException("Failed to fetch county population for " + year, e);
        }
    }

    private String getRequestBody(int year) {
        try {
            String bodySource = new Scanner(scbCountyPopulationRequestResource.getFile(), "UTF-8").useDelimiter("\\Z").next();
            return bodySource.replaceAll("%year%", String.valueOf(year));
        } catch (IOException e) {
            throw new ScbPopulationException("Could not read scb county population request resource file", e);
        }
    }

}
