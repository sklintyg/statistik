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
package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.WidelineManager;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    private JsonNode utlatande;

    @Mock
    private WidelineManager widelineManager;
    @Mock
    private LakareManager lakareManager;
    @Mock
    private IntygCommonManager intygCommonManager;
    @Mock
    private VardgivareManager vardgivareManager;

    @InjectMocks
    private Processor processor;

    @Before
    public void init() {
        utlatande = JSONParser.parse(this.getClass().getResourceAsStream("/json/maximalt-fk7263-internal.json"));
    }

    @Test
    public void processorCallsListener() {
        ArgumentCaptor<IntygDTO> intygDTOCaptor = ArgumentCaptor.forClass(IntygDTO.class);
        ArgumentCaptor<HsaInfo> hsaCaptor = ArgumentCaptor.forClass(HsaInfo.class);
        Mockito.doNothing().when(widelineManager)
            .accept(intygDTOCaptor.capture(), hsaCaptor.capture(), anyLong(), anyString(), any(EventType.class));

        IntygDTO dto = JsonDocumentHelper.convertToDTO(utlatande);

        processor.accept(dto, null, 1L, "1", EventType.CREATED);

        assertEquals(98, intygDTOCaptor.getValue().getPatientData().getAlder());
        assertEquals(Kon.MALE, intygDTOCaptor.getValue().getPatientData().getKon());
    }

}
