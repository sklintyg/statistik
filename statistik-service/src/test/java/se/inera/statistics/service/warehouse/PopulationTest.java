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
package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:warehouse-integration-test.xml", "classpath:icd10.xml"  })
@DirtiesContext
public class PopulationTest {

    private JsonNode rawDocument = JSONParser.parse(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));
    public static final HsaInfo JSON_NODE = HSADecorator.jsonToHsaInfo("{\"enhet\":{\"id\":\"enhetId\",\"namn\":\"Enhet enhetId\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"startdatum\":\"\",\"slutdatum\":\"\",\"arkiverad\":null,\"organisationsnamn\":\"Organisationsnamn\",\"vardform\":null,\"geografi\":{\"koordinat\":{\"typ\":\"testtype\",\"x\":\"1\",\"y\":\"2\"},\"plats\":\"Plats\",\"kommundelskod\":\"0\",\"kommundelsnamn\":\"Centrum\",\"lan\":\"20\",\"kommun\":\"62\"},\"verksamhet\":[\"1217\",\"1218\",\"1219\"],\"vgid\":\"vardgivarid\"},\"huvudenhet\":{\"id\":\"enhetId\",\"namn\":\"Enhet enhetId\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"startdatum\":\"\",\"slutdatum\":\"\",\"arkiverad\":null,\"organisationsnamn\":\"Organisationsnamn\",\"vardform\":null,\"geografi\":{\"koordinat\":{\"typ\":\"testtype\",\"x\":\"1\",\"y\":\"2\"},\"plats\":\"Plats\",\"kommundelskod\":\"0\",\"kommundelsnamn\":\"Centrum\",\"lan\":\"20\",\"kommun\":\"62\"},\"verksamhet\":[\"1217\",\"1218\",\"1219\"],\"vgid\":\"vardgivarid\"},\"vardgivare\":{\"id\":\"vardgivarid\",\"orgnr\":null,\"namn\":\"vardgivarnamn\",\"startdatum\":null,\"slutdatum\":null,\"arkiverad\":null},\"personal\":{\"id\":\"lakareId\",\"initial\":null,\"kon\":null,\"alder\":null,\"befattning\":null,\"specialitet\":null,\"yrkesgrupp\":null,\"skyddad\":null,\"tilltalsnamn\":\"Sirkka\",\"efternamn\":\"Isaac\"}}");

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private FactPopulator factPopulator;

    @Test
    public void addingIntygAddsToCorrectAisle() {
        final Patientdata patientData = DocumentHelper.getPatientData(rawDocument);
        for (WideLine wideLine : widelineConverter.toWideline(rawDocument, patientData, JSON_NODE, 0, "0", EventType.CREATED)) {
            factPopulator.accept(wideLine);
        }
        warehouse.complete(LocalDateTime.now());
        Aisle aisle = warehouse.get(new HsaIdVardgivare("VARDGIVARID"));
        assertEquals(1, aisle.getSize());
    }

}
