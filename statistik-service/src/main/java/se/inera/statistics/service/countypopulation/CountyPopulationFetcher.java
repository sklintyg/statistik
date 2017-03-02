/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CountyPopulationFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(CountyPopulationFetcher.class);
    private static final String FEMALE = "kvinnor";
    private static final String HEADER_REGION = "region";
    private static final String HEADER_GENDER = "kön";
    private static final String HEADER_POPULATION = "Folkmängd";
    private static final String POPULATION_REQUEST_FILE = "/scb-county-population-request.json";

    @Autowired
    private RestTemplate rest;

    @Value("${scb.population.url}")
    private String scbPopulationUrl;

    /**
     * Fetch population per county from SCb for a specific year.
     *
     * @param year
     *            Which years population will be fetched
     * @return An empty optional if no population for the requested yer was found, otherwise the populations per county
     */
    public Optional<CountyPopulation> getPopulationFor(int year) {
        try {
            final Map<String, KonField> populationFromScb = getPopulationFromScb(year);
            final java.time.LocalDate date = java.time.LocalDate.of(year, 12, 31);
            final CountyPopulation countyPopulation = new CountyPopulation(populationFromScb, date);
            return Optional.of(countyPopulation);
        } catch (Exception e) {
            final String msg = "Failed to get county population from SCB for " + year;
            LOG.error(msg);
            LOG.debug(msg, e);
            return Optional.empty();
        }
    }

    private Map<String, KonField> getPopulationFromScb(int year) {
        final String scbPopulationResponse = requestPopulationFromScb(year);
        return parseScbResult(scbPopulationResponse, year);
    }

    private Map<String, KonField> parseScbResult(String result, int year) {
        final String[] rows = result.split("(\\r\\n|\\r|\\n)");
        validateScbResponseHeaders(rows[0], year);
        final Map<String, List<ScbRow>> rowsPerCounty = Arrays.stream(rows).skip(1).map(this::parseScbRow)
                .collect(Collectors.groupingBy(scbRow -> scbRow.countyId));
        return rowsPerCounty.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> toKonField(e.getValue())));
    }

    private void validateScbResponseHeaders(String row, int year) {
        final String[] headers = splitStringAndRemoveQuotationMarks(row);
        if (!HEADER_REGION.equalsIgnoreCase(headers[0])) {
            throw new ScbResponseHeadersNotRecognisedException("Region header [0] mismatch");
        }
        if (!HEADER_GENDER.equalsIgnoreCase(headers[1])) {
            throw new ScbResponseHeadersNotRecognisedException("Gender header [1] mismatch");
        }
        if (!(HEADER_POPULATION + " " + year).equalsIgnoreCase(headers[2])) {
            throw new ScbResponseHeadersNotRecognisedException("Population header [2] mismatch");
        }
    }

    private KonField toKonField(List<ScbRow> konValuesForSingleCounty) {
        final HashMap<Kon, Integer> valuePerKon = new HashMap<>();
        for (ScbRow scbRow : konValuesForSingleCounty) {
            valuePerKon.put(scbRow.gender, scbRow.amount);
        }
        if (!valuePerKon.containsKey(Kon.FEMALE) || !valuePerKon.containsKey(Kon.MALE)) {
            final String countyId = konValuesForSingleCounty.isEmpty() ? "?" : konValuesForSingleCounty.get(0).countyId;
            throw new ScbPopulationException("Can't find population for both genders on county: " + countyId);
        }
        return new KonField(valuePerKon.get(Kon.FEMALE), valuePerKon.get(Kon.MALE));
    }

    @java.lang.SuppressWarnings("squid:UnusedPrivateMethod") // SONAR reports this method as not used due to
                                                             // https://jira.sonarsource.com/browse/SONARJAVA-583
    private ScbRow parseScbRow(String row) {
        final String[] rowFields = splitStringAndRemoveQuotationMarks(row);
        final String countyId = rowFields[0].substring(0, 2);
        if (!countyId.matches("^[0-9][0-9]$")) {
            throw new ScbPopulationException("County id should be an integer with two digits: " + countyId);
        }
        final Kon gender = rowFields[1].contains(FEMALE) ? Kon.FEMALE : Kon.MALE;
        final Integer amount = Integer.valueOf(rowFields[2]);
        return new ScbRow(countyId, gender, amount);
    }

    private String[] splitStringAndRemoveQuotationMarks(String row) {
        return Arrays.stream(row.split(",")).map(str -> str.replaceAll("\"", "")).collect(Collectors.toList()).toArray(new String[0]);
    }

    private static final class ScbRow {
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
        String bodySource = readTemplate(POPULATION_REQUEST_FILE);
        return bodySource.replaceAll("%year%", String.valueOf(year));
    }

    private static String readTemplate(String path) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(CountyPopulationFetcher.class.getResourceAsStream(path), "utf8"))) {
            StringBuilder sb = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            throw new ScbPopulationException("Could not read scb county population request resource file", e);
        }
    }

}
