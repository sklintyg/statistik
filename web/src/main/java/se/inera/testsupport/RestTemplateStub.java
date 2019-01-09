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
package se.inera.testsupport;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import se.inera.statistics.service.report.model.KonField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;

@Component
public class RestTemplateStub extends RestTemplate implements CountyPopulationInjector {

    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateStub.class);

    @Value("${scb.population.url}")
    private String scbPopulationUrl;

    @Autowired
    private Clock clock;

    private Map<String, Map<String, KonField>> countyPopulationPerYear = new HashMap<>();

    private static Map<String, String> countynamesPerCountyCode = new HashMap<>();

    static {
        countynamesPerCountyCode.put("01", "01 Stockholms län");
        countynamesPerCountyCode.put("03", "03 Uppsala län");
        countynamesPerCountyCode.put("04", "04 Södermanlands län");
        countynamesPerCountyCode.put("05", "05 Östergötlands län");
        countynamesPerCountyCode.put("06", "06 Jönköpings län");
        countynamesPerCountyCode.put("07", "07 Kronobergs län");
        countynamesPerCountyCode.put("08", "08 Kalmar län");
        countynamesPerCountyCode.put("09", "09 Gotlands län");
        countynamesPerCountyCode.put("10", "10 Blekinge län");
        countynamesPerCountyCode.put("12", "12 Skåne län");
        countynamesPerCountyCode.put("13", "13 Hallands län");
        countynamesPerCountyCode.put("14", "14 Västra Götalands län");
        countynamesPerCountyCode.put("17", "17 Värmlands län");
        countynamesPerCountyCode.put("18", "18 Örebro län");
        countynamesPerCountyCode.put("19", "19 Västmanlands län");
        countynamesPerCountyCode.put("20", "20 Dalarnas län");
        countynamesPerCountyCode.put("21", "21 Gävleborgs län");
        countynamesPerCountyCode.put("22", "22 Västernorrlands län");
        countynamesPerCountyCode.put("23", "23 Jämtlands län");
        countynamesPerCountyCode.put("24", "24 Västerbottens län");
        countynamesPerCountyCode.put("25", "25 Norrbottens län");
    }

    @PostConstruct
    public void restTemplateStubPostConstruct() {
        final int year = LocalDate.now(clock).getYear();
        final int yearsBackToGenerate = 10;
        for (int i = year - yearsBackToGenerate; i <= year; i++) {
            countyPopulationPerYear.put(String.valueOf(i), createRandomizedPopulation());
        }
    }

    private Map<String, KonField> createRandomizedPopulation() {
        final Map<String, KonField> population = new HashMap<>();
        final int minPopulation = 100;
        final int maxPopulation = 500000;
        for (String countyCode : countynamesPerCountyCode.keySet()) {
            final int randomMalePopulation = ThreadLocalRandom.current().nextInt(minPopulation, maxPopulation);
            final int randomFemalePopulation = ThreadLocalRandom.current().nextInt(minPopulation, maxPopulation);
            population.put(countyCode, new KonField(randomFemalePopulation, randomMalePopulation));
        }
        return population;
    }

    @Override
    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) {
        if (scbPopulationUrl.equalsIgnoreCase(url.toString())) {
            try {
                final String requestedYear = getRequestedYear(request);
                if (countyPopulationPerYear.containsKey(requestedYear)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\"region\",\"kön\",\"Folkmängd " + requestedYear + "\"\n");
                    final Map<String, KonField> populationPerCountyCode = countyPopulationPerYear.get(requestedYear);
                    for (Map.Entry<String, KonField> populationEntry : populationPerCountyCode.entrySet()) {
                        final String county = countynamesPerCountyCode.get(populationEntry.getKey());
                        sb.append("\"" + county + "\",\"män\"," + populationEntry.getValue().getMale() + "\n");
                        sb.append("\"" + county + "\",\"kvinnor\"," + populationEntry.getValue().getFemale() + "\n");
                    }
                    return (T) sb.toString();
                }
            } catch (IOException e) {
                LOG.error("Failed to create mocked population response");
                LOG.debug("Failed to create mocked population response", e);
            }
        }
        return null;
    }

    private String getRequestedYear(Object request) throws IOException {
        final JsonNode jsonNode = new ObjectMapper().readTree(String.valueOf(request));
        final int yearIndex = 3;
        return jsonNode.get("query").get(yearIndex).get("selection").get("values").get(0).asText();
    }

    @Override
    public synchronized void clearCountyPopulations() {
        countyPopulationPerYear.clear();
    }

    @Override
    public synchronized void addCountyPopulation(Map<String, KonField> countyPopulation, int year) {
        countyPopulationPerYear.put(String.valueOf(year), countyPopulation);
    }

    public Map<String, Map<String, KonField>> getCountyPopulationPerYear() {
        return Collections.unmodifiableMap(countyPopulationPerYear);
    }
}
