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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class CountyPopulationManagerTest {

    @Mock
    private EntityManager manager;

    @InjectMocks
    private CountyPopulationManager countyPopulationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCountyPopulation() throws Exception {
        //Given
        final Query query = Mockito.mock(Query.class);
        final CountyPopulationRow populationRow = new CountyPopulationRow("{\"01\": {\"male\": \"202\", \"female\": \"101\"}}", LocalDate.parse("2016-04-24"));
        Mockito.when(query.getSingleResult()).thenReturn(populationRow);
        Mockito.when(manager.createQuery(Mockito.anyString())).thenReturn(query);

        //When
        final CountyPopulation countyPopulation = countyPopulationManager.getCountyPopulation();

        //Then
        assertEquals(2016, countyPopulation.getDate().getYear());
        assertEquals(4, countyPopulation.getDate().getMonthValue());
        assertEquals(24, countyPopulation.getDate().getDayOfMonth());

        assertEquals(1, countyPopulation.getPopulationPerCountyCode().size());
        assertEquals(101, countyPopulation.getPopulationPerCountyCode().get("01").getFemale());
        assertEquals(202, countyPopulation.getPopulationPerCountyCode().get("01").getMale());
    }

}
