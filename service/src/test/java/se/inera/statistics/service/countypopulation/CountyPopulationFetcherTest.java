/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.countypopulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

public class CountyPopulationFetcherTest {

    @Mock
    private RestTemplate rest;

    @Spy
    private Resource scbCountyPopulationRequestResource = new ClassPathResource("scb-county-population-request.json");

    @InjectMocks
    private CountyPopulationFetcher countyPopulationFetcher = new CountyPopulationFetcher();

    private static final String RESPONSE = "\"region\",\"kön\",\"Folkmängd 2013\"\n" +
        "\"01 Stockholms län\",\"män\",1073900\n" +
        "\"01 Stockholms län\",\"kvinnor\",1089142\n" +
        "\"03 Uppsala län\",\"män\",172125\n" +
        "\"03 Uppsala län\",\"kvinnor\",173356\n" +
        "\"04 Södermanlands län\",\"män\",138358\n" +
        "\"04 Södermanlands län\",\"kvinnor\",139211\n" +
        "\"05 Östergötlands län\",\"män\",220024\n" +
        "\"05 Östergötlands län\",\"kvinnor\",217824\n" +
        "\"06 Jönköpings län\",\"män\",171115\n" +
        "\"06 Jönköpings län\",\"kvinnor\",170120\n" +
        "\"07 Kronobergs län\",\"män\",94597\n" +
        "\"07 Kronobergs län\",\"kvinnor\",92559\n" +
        "\"08 Kalmar län\",\"män\",117112\n" +
        "\"08 Kalmar län\",\"kvinnor\",116762\n" +
        "\"09 Gotlands län\",\"män\",28388\n" +
        "\"09 Gotlands län\",\"kvinnor\",28773\n" +
        "\"10 Blekinge län\",\"män\",77387\n" +
        "\"10 Blekinge län\",\"kvinnor\",75370\n" +
        "\"12 Skåne län\",\"män\",631800\n" +
        "\"12 Skåne län\",\"kvinnor\",642269\n" +
        "\"13 Hallands län\",\"män\",152800\n" +
        "\"13 Hallands län\",\"kvinnor\",154040\n" +
        "\"14 Västra Götalands län\",\"män\",806676\n" +
        "\"14 Västra Götalands län\",\"kvinnor\",808408\n" +
        "\"17 Värmlands län\",\"män\",136903\n" +
        "\"17 Värmlands län\",\"kvinnor\",136912\n" +
        "\"18 Örebro län\",\"män\",142240\n" +
        "\"18 Örebro län\",\"kvinnor\",143155\n" +
        "\"19 Västmanlands län\",\"män\",129449\n" +
        "\"19 Västmanlands län\",\"kvinnor\",129605\n" +
        "\"20 Dalarnas län\",\"män\",139115\n" +
        "\"20 Dalarnas län\",\"kvinnor\",138234\n" +
        "\"21 Gävleborgs län\",\"män\",139111\n" +
        "\"21 Gävleborgs län\",\"kvinnor\",138859\n" +
        "\"22 Västernorrlands län\",\"män\",121305\n" +
        "\"22 Västernorrlands län\",\"kvinnor\",120851\n" +
        "\"23 Jämtlands län\",\"män\",63473\n" +
        "\"23 Jämtlands län\",\"kvinnor\",62988\n" +
        "\"24 Västerbottens län\",\"män\",131556\n" +
        "\"24 Västerbottens län\",\"kvinnor\",129556\n" +
        "\"25 Norrbottens län\",\"män\",126923\n" +
        "\"25 Norrbottens län\",\"kvinnor\",122513\n";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPopulationForHappyPath() throws Exception {
        //Given
        Mockito.when(rest.postForObject(nullable(String.class), any(), any())).thenReturn(RESPONSE);

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertTrue(countyPopulation.isPresent());
        final CountyPopulation population = countyPopulation.get();
        assertEquals(LocalDate.parse("2013-12-31"), population.getDate());
        assertEquals(21, population.getPopulationPerCountyCode().size());
        assertEquals(129556, population.getPopulationPerCountyCode().get("24").getFemale());
    }

    @Test
    public void testGetPopulationForWrongRegionHeader() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any())).thenReturn(RESPONSE.replaceAll("region", "annat"));

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

    @Test
    public void testGetPopulationForWrongGenderHeader() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any())).thenReturn(RESPONSE.replaceAll("kön", "annat"));

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

    @Test
    public void testGetPopulationForWrongPopulationHeader() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any())).thenReturn(RESPONSE);

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2012);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

    @Test
    public void testGetPopulationForMissingOneGender() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any()))
            .thenReturn(RESPONSE.replace("\"17 Värmlands län\",\"män\",136903\n", ""));

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

    @Test
    public void testGetPopulationForCountyWithIllegalId() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any())).thenReturn(RESPONSE.replace("17 Värmlands län", "1b Värmlands län"));

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

    @Test
    public void testGetPopulationForCountyUsesCorrectYearInRequest() throws Exception {
        //Given
        final int year = 1234321;
        Mockito.when(rest.postForObject(nullable(String.class), any(), any()))
            .thenReturn(RESPONSE.replace("Folkmängd 2013", "Folkmängd " + year));

        //When
        countyPopulationFetcher.getPopulationFor(year);

        //Then
        final ArgumentCaptor<String> requestBody = ArgumentCaptor.forClass(String.class);
        Mockito.verify(rest).postForObject(nullable(String.class), requestBody.capture(), any());
        assertTrue(requestBody.getValue().contains("\"" + String.valueOf(year) + "\""));
    }

    @Test
    public void testGetPopulationForIllegalPopulationValue() throws Exception {
        //Given
        Mockito.when(rest.postForObject(anyString(), any(), any())).thenReturn(RESPONSE.replace("1073900", "OgiltigtNummer"));

        //When
        final Optional<CountyPopulation> countyPopulation = countyPopulationFetcher.getPopulationFor(2013);

        //Then
        assertFalse(countyPopulation.isPresent());
    }

}
