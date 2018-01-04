/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import se.inera.statistics.service.SpyableClock;
import se.inera.statistics.service.report.model.Range;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;

public class CountyPopulationManagerImplTest {

    @Spy
    private final Clock clock = new SpyableClock(Clock.systemDefaultZone());

    @Mock
    private EntityManager manager;

    @Mock
    private CountyPopulationFetcher countyPopulationFetcher;

    @InjectMocks
    private CountyPopulationManagerImpl countyPopulationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCountyPopulation() throws Exception {
        //Given
        final Query query = Mockito.mock(Query.class);
        final CountyPopulationRow populationRow = new CountyPopulationRow("{\"01\": {\"male\": \"202\", \"female\": \"101\"}}", LocalDate.parse("2016-04-24"));
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList(populationRow));
        Mockito.when(manager.createQuery(Mockito.anyString())).thenReturn(query);
        final Range range = Range.year(clock);

        //When
        final CountyPopulation countyPopulation = countyPopulationManager.getCountyPopulation(range);

        //Then
        assertEquals(2016, countyPopulation.getDate().getYear());
        assertEquals(4, countyPopulation.getDate().getMonthValue());
        assertEquals(24, countyPopulation.getDate().getDayOfMonth());

        assertEquals(1, countyPopulation.getPopulationPerCountyCode().size());
        assertEquals(101, countyPopulation.getPopulationPerCountyCode().get("01").getFemale());
        assertEquals(202, countyPopulation.getPopulationPerCountyCode().get("01").getMale());
    }

    @Test
    public void testGetCountyPopulationFetcherIsNotInvokedWhenPopulationIsFoundInDb() throws Exception {
        //Given
        final Query query = Mockito.mock(Query.class);
        final CountyPopulationRow populationRow = new CountyPopulationRow("{\"01\": {\"male\": \"202\", \"female\": \"101\"}}", LocalDate.parse("2016-04-24"));
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList(populationRow));
        Mockito.when(manager.createQuery(Mockito.anyString())).thenReturn(query);
        final Range range = Range.year(clock);

        //When
        countyPopulationManager.getCountyPopulation(range);

        //Then
        Mockito.verify(countyPopulationFetcher, times(0)).getPopulationFor(range.getTo().getYear() - 1);
        Mockito.verify(query, times(1)).getResultList();
    }

    @Test
    public void testGetCountyPopulationUseLastKnowPopulationIfNotPossibleToGetCorrectYear() throws Exception {
        //Given
        final Query query = Mockito.mock(Query.class);
        Mockito.when(query.getResultList()).thenReturn(Collections.emptyList());
        Mockito.when(manager.createQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(countyPopulationFetcher.getPopulationFor(anyInt())).thenReturn(Optional.empty());
        final Range range = Range.year(clock);

        //When
        final CountyPopulation countyPopulation = countyPopulationManager.getCountyPopulation(range);

        //Then
        Mockito.verify(countyPopulationFetcher, times(1)).getPopulationFor(range.getTo().getYear() - 1);
        Mockito.verify(query, times(2)).getResultList();
    }

}
