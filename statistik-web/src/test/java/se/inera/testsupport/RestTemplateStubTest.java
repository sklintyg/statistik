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
package se.inera.testsupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.web.util.SpyableClock;

import static org.junit.Assert.*;

public class RestTemplateStubTest {

    @Spy
    private SpyableClock clock = new SpyableClock();

    @InjectMocks
    private RestTemplateStub restTemplateStub;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        restTemplateStub.restTemplateStubPostConstruct();
    }

    @Test
    public void testCountyPopulationIsPopulatedCorrectlyAtCreation() throws Exception {
        assertEquals(11, restTemplateStub.getCountyPopulationPerYear().size());
    }

}
