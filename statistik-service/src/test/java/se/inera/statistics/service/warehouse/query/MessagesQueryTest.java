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
package se.inera.statistics.service.warehouse.query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.warehouse.message.MessageWidelineLoader;
import se.inera.statistics.service.warehouse.message.MsgAmne;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MessagesQueryTest {

    @InjectMocks
    private MessagesQuery messagesQuery;

    @Mock
    private MessageWidelineLoader messageWidelineLoader;

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
        final KonDataResponse messagesTvarsnittPerAmnePerEnhet = messagesQuery.getMessagesTvarsnittPerAmnePerEnhet(null);
        final List<String> groups = messagesTvarsnittPerAmnePerEnhet.getGroups().stream().map(a -> MsgAmne.parse(a).getText()).collect(Collectors.toList());
        final List<String> sortedGroups = new ArrayList<>(groups);
        sortedGroups.sort(coll);

        //Then
        assertEquals(MsgAmne.values().length, groups.size());
        assertArrayEquals(sortedGroups.toArray(new String[0]), groups.toArray(new String[0]));

    }
}