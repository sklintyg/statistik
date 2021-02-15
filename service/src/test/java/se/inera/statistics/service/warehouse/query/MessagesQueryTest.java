/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.warehouse.message.CountDTOAmne;
import se.inera.statistics.service.warehouse.message.MessageWidelineLoader;
import se.inera.statistics.service.warehouse.message.MsgAmne;

public class MessagesQueryTest {

    @InjectMocks
    private MessagesQuery messagesQuery;

    @Mock
    private MessageWidelineLoader messageWidelineLoader;

    @Mock
    private LakareManager lakareManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMessagesTvarsnittPerAmnePerEnhetGroupsAreSortedAlphanumercallyINTYG5446() {
        //Given
        Locale la = new Locale("sv", "SE");
        Collator coll = Collator.getInstance(la);
        coll.setStrength(Collator.PRIMARY);

        //When
        final KonDataResponse response = messagesQuery.getMessagesTvarsnittPerAmnePerEnhet(null, new HashMap<>(), false);
        final List<String> groups = response.getGroups().stream().map(a -> MsgAmne.parse(a).getText()).collect(Collectors.toList());
        final List<String> sortedGroups = new ArrayList<>(groups);
        sortedGroups.sort(coll);

        //Then
        assertEquals(MsgAmne.values().length, groups.size());
        assertArrayEquals(sortedGroups.toArray(new String[0]), groups.toArray(new String[0]));
    }

    @Test
    public void testNamesAreTranslatedUsingTheIdToNameMap() {
        //Given
        final MessagesFilter filter = null;
        final String enhetsidtest = "enhetsidtest";
        final CountDTOAmne countDTOAmne = createCountDTOAmne(enhetsidtest);
        Mockito.when(messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true)).thenReturn(Arrays.asList(countDTOAmne));
        final HashMap<HsaIdEnhet, String> idToNameMap = new HashMap<>();
        final String testenhetsnamn = "testenhetsnamn";
        idToNameMap.put(new HsaIdEnhet(enhetsidtest), testenhetsnamn);

        //When
        final KonDataResponse resp = messagesQuery.getMessagesTvarsnittPerAmnePerEnhet(filter, idToNameMap, true);

        //Then
        assertEquals(1, resp.getRows().size());
        resp.getRows().forEach(r -> assertEquals(testenhetsnamn, r.getName()));
    }

    private CountDTOAmne createCountDTOAmne(String enhetId) {
        final CountDTOAmne countDTOAmne = new CountDTOAmne();
        countDTOAmne.setEnhet(enhetId);
        countDTOAmne.setAmne(MsgAmne.KOMPLT);
        countDTOAmne.setKon(Kon.FEMALE);
        return countDTOAmne;
    }

    @Test
    public void testGetMessagesPerAmnePerLakareTomtNamn() {
        //Given
        final MessagesFilter filter = new MessagesFilter(null, LocalDate.now(), LocalDate.now(), null, null, null, null);
        final CountDTOAmne countDTOAmne = new CountDTOAmne();
        final HsaIdLakare lakare = new HsaIdLakare("lakareid");
        countDTOAmne.setLakareId(lakare);
        countDTOAmne.setDate(LocalDate.now());
        Mockito.when(messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true)).thenReturn(Collections.singletonList(countDTOAmne));
        Mockito.doReturn(Collections.singletonList(new Lakare(HsaIdVardgivare.empty(), lakare, null, null)))
            .when(lakareManager).getAllSpecifiedLakares(Mockito.anyCollection());

        //When
        final KonDataResponse resp = messagesQuery.getMessagesPerAmnePerLakare(filter);

        //Then
        assertTrue(resp.getGroups().get(0).startsWith(lakare.getId()));
    }

    @Test
    public void testGetMessagesPerAmnePerLakareSaknadLakare() {
        //Given
        final MessagesFilter filter = new MessagesFilter(null, LocalDate.now(), LocalDate.now(), null, null, null, null);
        final CountDTOAmne countDTOAmne = new CountDTOAmne();
        final HsaIdLakare lakare = new HsaIdLakare("lakareid");
        countDTOAmne.setLakareId(lakare);
        countDTOAmne.setDate(LocalDate.now());
        Mockito.when(messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true)).thenReturn(Collections.singletonList(countDTOAmne));
        Mockito.doReturn(Collections.emptyList()).when(lakareManager).getAllSpecifiedLakares(Mockito.anyCollection());

        //When
        final KonDataResponse resp = messagesQuery.getMessagesPerAmnePerLakare(filter);

        //Then
        assertTrue(resp.getGroups().get(0).startsWith(lakare.getId()));
    }

    @Test
    public void testGetMessagesTvarsnittPerAmnePerLakareTomtNamn() {
        //Given
        final MessagesFilter filter = new MessagesFilter(null, LocalDate.now(), LocalDate.now(), null, null, null, null);
        final CountDTOAmne countDTOAmne = new CountDTOAmne();
        final HsaIdLakare lakare = new HsaIdLakare("lakareid");
        countDTOAmne.setLakareId(lakare);
        countDTOAmne.setKon(Kon.FEMALE);
        countDTOAmne.setAmne(MsgAmne.KOMPLT);
        countDTOAmne.setDate(LocalDate.now());
        Mockito.when(messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true)).thenReturn(Collections.singletonList(countDTOAmne));
        Mockito.doReturn(Collections.singletonList(new Lakare(HsaIdVardgivare.empty(), lakare, null, null)))
            .when(lakareManager).getAllSpecifiedLakares(Mockito.anyCollection());

        //When
        final KonDataResponse resp = messagesQuery.getMessagesTvarsnittPerAmnePerLakare(filter);

        //Then
        assertEquals(1, resp.getRows().size());
        assertEquals(lakare.getId(), resp.getRows().get(0).getName());
    }

    @Test
    public void testGetMessagesTvarsnittPerAmnePerLakareSaknadLakare() {
        //Given
        final MessagesFilter filter = new MessagesFilter(null, LocalDate.now(), LocalDate.now(), null, null, null, null);
        final CountDTOAmne countDTOAmne = new CountDTOAmne();
        final HsaIdLakare lakare = new HsaIdLakare("lakareid");
        countDTOAmne.setLakareId(lakare);
        countDTOAmne.setKon(Kon.FEMALE);
        countDTOAmne.setAmne(MsgAmne.KOMPLT);
        countDTOAmne.setDate(LocalDate.now());
        Mockito.when(messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true)).thenReturn(Collections.singletonList(countDTOAmne));
        Mockito.doReturn(Collections.emptyList()).when(lakareManager).getAllSpecifiedLakares(Mockito.anyCollection());

        //When
        final KonDataResponse resp = messagesQuery.getMessagesTvarsnittPerAmnePerLakare(filter);

        //Then
        assertEquals(1, resp.getRows().size());
        assertEquals(lakare.getId(), resp.getRows().get(0).getName());
    }

}