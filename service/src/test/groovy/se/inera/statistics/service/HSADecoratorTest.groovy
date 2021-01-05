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
package se.inera.statistics.service

import org.junit.Before
import org.junit.Test
import se.inera.statistics.service.helper.JSONParser
import se.inera.statistics.service.hsa.*

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class HSADecoratorTest {
    HSADecorator hsaDecorator = new HSADecorator()
    HSAService hsaService;

    @Before
    void setup() {
        hsaService = [
                getHSAInfo: { HSAKey that ->
                    new HsaInfo(null, null, new HsaInfoVg("län för " + that.vardgivareId + "," + that.enhetId + "," + that.lakareId, "", null, null, false), null);
                }
        ] as HSAService
    }

    @Test
    void hsa_created_test() {
        assertNotNull(hsaDecorator)
    }

    @Test
    void hsa_get_hsa_data_from_hsa_mock() {
        HSAKey key = new HSAKey("vardgivareId", "enhetId", "lakareId")

        HsaInfo result = hsaService.getHSAInfo(key)

        assertEquals "län för VARDGIVAREID,ENHETID,LAKAREID", result.getVardgivare().getId()
    }

    @Test
    void hsa_decorator_extract_key_from_document() {
        def doc = JSONParser.parse(JSONSource.readTemplateAsString())

        def key = hsaDecorator.extractHSAKey(doc)

        assertEquals "VardenhetY".toUpperCase(), key.enhetId.id
        assertEquals "Personal HSA-ID".toUpperCase().replaceAll(" ", ""), key.lakareId.id
        assertEquals "VardgivarId".toUpperCase(), key.vardgivareId.id
    }

}
