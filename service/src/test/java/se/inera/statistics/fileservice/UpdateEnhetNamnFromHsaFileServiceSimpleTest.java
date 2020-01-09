/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.fileservice;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import org.junit.Test;
import se.inera.ifv.hsawsresponder.v3.ListGetHsaUnitsResponseType;

public class UpdateEnhetNamnFromHsaFileServiceSimpleTest {

    @Test
    public void testUnmarshalXml() {
        final String in = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<FileGetHsaUnitsResponse xmlns=\"urn:riv:hsa:HsaWsResponder:3\">\n" +
            "    <startDate>2017-12-12T01:04:02.873+01:00</startDate>\n" +
            "    <endDate>2017-12-12T01:28:36.546+01:00</endDate>\n" +
            "    <hsaUnits>\n" +
            "        <hsaUnit>\n" +
            "            <hsaIdentity>SE2120001231-01F8KO</hsaIdentity>\n" +
            "            <DN>ou=Hjortsberg,ou=Aleris Omsorg AB,ou=Entreprenader,o=Falkenbergs kommun,l=Hallands län,c=SE</DN>\n" +
            "            <orgNo>556334-1659</orgNo>\n" +
            "            <name>Hjortsberg</name>\n" +
            "            <publicName>Hjortsberg</publicName>\n" +
            "        </hsaUnit>\n" +
            "        <hsaUnit>\n" +
            "            <hsaIdentity>SE2120001231-01BEKO</hsaIdentity>\n" +
            "            <DN>ou=Hjortsberg - Havsviken,ou=Hjortsberg,ou=Aleris Omsorg AB,ou=Entreprenader,o=Falkenbergs kommun,l=Hallands län,c=SE</DN>\n"
            +
            "            <orgNo>556334-1659</orgNo>\n" +
            "            <name>Hjortsberg - Havsviken</name>\n" +
            "            <publicName>Hjortsberg - Havsviken</publicName>\n" +
            "        </hsaUnit>\n" +
            "        <hsaUnit>\n" +
            "            <hsaIdentity>SE2120001231-00FBKO</hsaIdentity>\n" +
            "            <DN>ou=Hjortsberg - Lerbäcken,ou=Hjortsberg,ou=Aleris Omsorg AB,ou=Entreprenader,o=Falkenbergs kommun,l=Hallands län,c=SE</DN>\n"
            +
            "            <orgNo>556334-1659</orgNo>\n" +
            "            <name>Hjortsberg - Lerbäcken</name>\n" +
            "            <publicName>Hjortsberg - Lerbäcken</publicName>\n" +
            "        </hsaUnit>\n" +
            "        <hsaUnit>\n" +
            "            <hsaIdentity>SE2120001231-0090KO</hsaIdentity>\n" +
            "            <DN>ou=Hjortsberg - Ljungheden,ou=Hjortsberg,ou=Aleris Omsorg AB,ou=Entreprenader,o=Falkenbergs kommun,l=Hallands län,c=SE</DN>\n"
            +
            "            <name>Hjortsberg - Ljungheden</name>\n" +
            "            <publicName>Hjortsberg - Ljungheden</publicName>\n" +
            "        </hsaUnit>\n" +
            "        <hsaUnit>\n" +
            "            <hsaIdentity>SE2120001231-00F1KO</hsaIdentity>\n" +
            "            <DN>ou=Furugården,ou=Aleris Omsorg AB,ou=Entreprenader,o=Falkenbergs kommun,l=Hallands län,c=SE</DN>\n" +
            "            <orgNo>556334-1659</orgNo>\n" +
            "            <name>Furugården</name>\n" +
            "            <publicName>Furugården</publicName>\n" +
            "        </hsaUnit>\n" +
            "    </hsaUnits>\n" +
            "</FileGetHsaUnitsResponse>\n";
        final ListGetHsaUnitsResponseType resp = UpdateEnhetNamnFromHsaFileService.unmarshalXml(new ByteArrayInputStream(in.getBytes()));
        assertEquals(5, resp.getHsaUnits().getHsaUnit().size());
        assertEquals("Furugården", resp.getHsaUnits().getHsaUnit().get(4).getName());
        assertEquals("SE2120001231-00F1KO", resp.getHsaUnits().getHsaUnit().get(4).getHsaIdentity());
    }

}