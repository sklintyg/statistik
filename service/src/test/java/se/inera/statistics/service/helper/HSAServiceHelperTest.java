/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper;

import org.junit.Assert;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.hsa.HsaInfo;

public class HSAServiceHelperTest {

    private static final String JSON_VALUE = "{\"enhet\":{\"id\":\"enhetId\",\"namn\":\"Enhet enhetId\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"startdatum\":null,\"slutdatum\":null,\"arkiverad\":null,\"organisationsnamn\":\"Organisationsnamn\",\"vardform\":null,\"geografi\":{\"koordinat\":{\"typ\":\"testtype\",\"x\":\"1\",\"y\":\"2\"},\"plats\":\"Plats\",\"kommundelskod\":\"0\",\"kommundelsnamn\":\"Centrum\",\"lan\":\"20\",\"kommun\":\"62\"},\"verksamhet\":[\"1217\",\"1218\",\"1219\"],\"vgid\":\"vardgivarid\"},\"huvudenhet\":{\"id\":\"enhetId\",\"namn\":\"Enhet enhetId\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"startdatum\":null,\"slutdatum\":null,\"arkiverad\":null,\"organisationsnamn\":\"Organisationsnamn\",\"vardform\":null,\"geografi\":{\"koordinat\":{\"typ\":\"testtype\",\"x\":\"1\",\"y\":\"2\"},\"plats\":\"Plats\",\"kommundelskod\":\"0\",\"kommundelsnamn\":\"Centrum\",\"lan\":\"20\",\"kommun\":\"62\"},\"verksamhet\":[\"1217\",\"1218\",\"1219\"],\"vgid\":\"vardgivarid\"},\"vardgivare\":{\"id\":\"vardgivarid\",\"orgnr\":null,\"namn\":\"vardgivarnamn\",\"startdatum\":null,\"slutdatum\":null,\"arkiverad\":null},\"personal\":{\"id\":\"lakareId\",\"initial\":null,\"kon\":null,\"alder\":null,\"befattning\":null,\"specialitet\":null,\"yrkesgrupp\":null,\"skyddad\":null,\"tilltalsnamn\":\"Sirkka\",\"efternamn\":\"Isaac\"}}";
    private static final HsaInfo HSA_INFO = HSADecorator.jsonToHsaInfo(JSON_VALUE);

    @Test
    public void getLanTest() {
        String lan = HSAServiceHelper.getLan(HSA_INFO);
        Assert.assertEquals("20", lan);
    }

    @Test
    public void getLanTestForFixedData() {
        final HsaInfo hsaInfo = HSADecorator.jsonToHsaInfo(JSONSource.readHSASample());
        String lan = HSAServiceHelper.getLan(hsaInfo);
        Assert.assertEquals("03", lan);
    }

    @Test
    public void getLanOnHuvudenhet() {
        final HsaInfo hsaInfo = HSADecorator.jsonToHsaInfo(JSONSource.readHSASample("hsa_example_huvudenhet"));
        String lan = HSAServiceHelper.getLan(hsaInfo);
        Assert.assertEquals("05", lan);
    }

    @Test
    public void getKommunTest() {
        String kommun = HSAServiceHelper.getKommun(HSA_INFO);
        Assert.assertEquals("62", kommun);
    }

    @Test
    public void getVerksamhetTyperTest() {
        String verksamhetsTyper = HSAServiceHelper.getVerksamhetsTyper(HSA_INFO, false);
        Assert.assertEquals("1217,1218,1219,02", verksamhetsTyper);
    }

}
