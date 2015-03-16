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
package se.inera.statistics.service.helper;

import org.junit.Assert;
import org.junit.Test;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import com.fasterxml.jackson.databind.JsonNode;

public class HSAServiceHelperTest {
    @Test
    public void getLanTest() {
        HSAService hsaService = new HSAServiceMock();
        JsonNode info = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetId", "lakareId"));

        String lan = HSAServiceHelper.getLan(info);
        Assert.assertEquals("20", lan);
    }

    @Test
    public void getLanTestForFixedData() {
        JsonNode info = JSONParser.parse(JSONSource.readHSASample());

        String lan = HSAServiceHelper.getLan(info);

        Assert.assertEquals("03", lan);
    }

    @Test
    public void getLanOnHuvudenhet() {
        JsonNode info = JSONParser.parse(JSONSource.readHSASample("hsa_example_huvudenhet"));

        String lan = HSAServiceHelper.getLan(info);

        Assert.assertEquals("05", lan);
    }

    @Test
    public void getKommunTest() {
        HSAService hsaService = new HSAServiceMock();
        JsonNode info = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetId", "lakareId"));

        String kommun = HSAServiceHelper.getKommun(info);
        Assert.assertEquals("2062", kommun);
    }

    @Test
    public void getVerksamhetTyperTest() {
        HSAService hsaService = new HSAServiceMock();
        JsonNode info = hsaService.getHSAInfo(new HSAKey("vardgivarid", "enhetId", "lakareId"));

        String verksamhetsTyper = HSAServiceHelper.getVerksamhetsTyper(info);
        Assert.assertEquals("1217,1218,1219,02", verksamhetsTyper);
    }

}
