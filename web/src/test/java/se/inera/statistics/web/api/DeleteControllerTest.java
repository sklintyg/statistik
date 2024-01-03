/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.api;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.service.DeleteCustomerData;

@RunWith(MockitoJUnitRunner.class)
public class DeleteControllerTest extends TestCase {

    @Mock
    private DeleteCustomerData deleteCustomerData;

    @InjectMocks
    private DeleteController deleteController;

    @Test
    public void testDeleteIntygsIdList() {
        List<String> strings = new ArrayList<>();
        List<String> dataByIntygsIdResult = new ArrayList<>();
        when(deleteCustomerData.deleteCustomerDataByIntygsId(strings)).thenReturn(dataByIntygsIdResult);

        assertNotNull(deleteController.deleteIntygsIdList(strings));

        verify(deleteCustomerData, times(1)).deleteCustomerDataByIntygsId(strings);
    }

    @Test
    public void testDeleteVardgivareIdList() {
        List<String> strings = new ArrayList<>();
        List<String> deletedVardgivares = new ArrayList<>();
        when(deleteCustomerData.deleteCustomerDataByVardgivarId(strings)).thenReturn(deletedVardgivares);

        assertNotNull(deleteController.deleteVardgivareIdList(strings));

        verify(deleteCustomerData, times(1)).deleteCustomerDataByVardgivarId(strings);
    }
}