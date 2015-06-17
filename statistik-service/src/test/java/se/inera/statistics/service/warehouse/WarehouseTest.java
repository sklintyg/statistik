/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:warehouse-integration-test.xml", "classpath:icd10.xml"  })
@DirtiesContext
public class WarehouseTest {

    private HSAService hsaService = new HSAServiceMock();

    private JsonNode rawDocument = JSONParser.parse(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));
    private JsonNode hsaInfo = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetid", "lakareid"));
    
    @Autowired
    private Warehouse warehouse;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private FactPopulator factPopulator;

    @Test
    public void addingIntygAddsToCorrectAisle() {
        JsonNode document = DocumentHelper.prepare(rawDocument);
        for (WideLine wideLine : widelineConverter.toWideline(document, hsaInfo, 0, "0", EventType.CREATED)) {
            factPopulator.accept(wideLine);
        }
        warehouse.complete(LocalDateTime.now());
        Aisle aisle = warehouse.get(new HsaId("VARDGIVARID"));
        assertEquals(1, aisle.getSize());
    }
}
