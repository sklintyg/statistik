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
package se.inera.statistics.web.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;

@RunWith(MockitoJUnitRunner.class)
public class ResultMessagHandlerTest {

    @InjectMocks
    private ResultMessageHandler resultMessageHandler;

    @Mock
    private Icd10 icd10;

    @Before
    public void setUp() throws Exception {
        Mockito.when(icd10.findIcd10FromNumericId(anyInt())).thenReturn(new Icd10.Id("180108190", "A00-B99") {
            @Override
            public int toInt() {
                return 180108190;
            }

            @Override
            public List<Icd10.Id> getSubItems() {
                return new ArrayList<>();
            }

            @Override
            public Optional<Icd10.Id> getParent() {
                return Optional.empty();
            }

            @Override
            public Icd10RangeType getRangeType() {
                return null;
            }
        });
    }

    @Test
    public void testEmptyFilter() {
        //Given
        List<String> selectedDxs = new ArrayList<>();
        Collection<String> filterDiagnoser = new ArrayList<>();

        //When
        final boolean isDxFilterDisableAllSelectedDxs = resultMessageHandler.isDxFilterDisableAllSelectedDxs(selectedDxs, filterDiagnoser);

        //Then
        assertFalse(isDxFilterDisableAllSelectedDxs);
    }

    @Test
    public void testNullFilter() {
        //Given
        List<String> selectedDxs = new ArrayList<>();
        Collection<String> filterDiagnoser = null;

        //When
        final boolean isDxFilterDisableAllSelectedDxs = resultMessageHandler.isDxFilterDisableAllSelectedDxs(selectedDxs, filterDiagnoser);

        //Then
        assertFalse(isDxFilterDisableAllSelectedDxs);
    }

    @Test
    public void testValidDxsSelected() {
        //Given
        List<String> selectedDxs = new ArrayList<>();
        selectedDxs.add("180108190");
        Collection<String> filterDiagnoser = new ArrayList<>();
        filterDiagnoser.add("180108190");

        //When
        final boolean isDxFilterDisableAllSelectedDxs = resultMessageHandler.isDxFilterDisableAllSelectedDxs(selectedDxs, filterDiagnoser);

        //Then
        assertFalse(isDxFilterDisableAllSelectedDxs);
    }

    @Test
    public void testNotPresentDxsSelected() {
        //Given
        List<String> selectedDxs = new ArrayList<>();
        selectedDxs.add("180108190");
        Collection<String> filterDiagnoser = new ArrayList<>();
        filterDiagnoser.add("180108192");

        //When
        final boolean isDxFilterDisableAllSelectedDxs = resultMessageHandler.isDxFilterDisableAllSelectedDxs(selectedDxs, filterDiagnoser);

        //Then
        assertTrue(isDxFilterDisableAllSelectedDxs);
    }
}
