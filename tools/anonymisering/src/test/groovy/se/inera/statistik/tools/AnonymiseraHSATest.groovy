/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistik.tools

import groovy.json.JsonSlurper
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId


class AnonymiseraHSATest extends GroovyTestCase {
    void testAnonymizeHsaJson() {
        //Given
        String actualIn = "{\"enhet\":{\"id\":\"VG1-ENHET-11\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"verksamhet\":[\"2009\"],\"vardform\":[],\"geografi\":{\"koordinat\":{\"typ\":\"RT_90\",\"x\":\"1\",\"y\":\"1\"},\"kommun\":\"00\",\"lan\":\"23\"},\"vgid\":\"VG1\"},\"huvudenhet\":{\"id\":\"VG1-ENHET-11\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"verksamhet\":[\"2009\"],\"vardform\":[],\"geografi\":{\"koordinat\":{\"typ\":\"RT_90\",\"x\":\"1\",\"y\":\"1\"},\"kommun\":\"00\",\"lan\":\"23\"},\"vgid\":\"VG1\"},\"vardgivare\":{\"id\":\"VG1\"},\"personal\":{\"id\":\"HSA-28\",\"befattning\":[],\"specialitet\":[],\"yrkesgrupp\":[],\"skyddad\":false,\"tilltalsnamn\":\"Fredrika\",\"efternamn\":\"En\"}}";
        String expected = "{\"enhet\":{\"id\":\"VG1-ENHET-11\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"verksamhet\":[\"2009\"],\"vardform\":[],\"geografi\":{\"koordinat\":{\"typ\":\"RT_90\",\"x\":\"1\",\"y\":\"1\"},\"kommun\":\"00\",\"lan\":\"23\"},\"vgid\":\"VG1\"},\"huvudenhet\":{\"id\":\"VG1-ENHET-11\",\"enhetsTyp\":[\"02\"],\"agarform\":[\"Landsting/Region\"],\"verksamhet\":[\"2009\"],\"vardform\":[],\"geografi\":{\"koordinat\":{\"typ\":\"RT_90\",\"x\":\"1\",\"y\":\"1\"},\"kommun\":\"00\",\"lan\":\"23\"},\"vgid\":\"VG1\"},\"vardgivare\":{\"id\":\"VG1\"},\"personal\":{\"id\":\"TestAnonHsaId\",\"befattning\":[],\"specialitet\":[],\"yrkesgrupp\":[],\"skyddad\":false,\"tilltalsnamn\":\"xxxxxxxx\",\"efternamn\":\"xx\"}}";

        //When
        def anonymized = AnonymiseraHSA.anonymizeHsaJson(actualIn, [anonymisera: { String hsaId -> 'TestAnonHsaId' }] as AnonymiseraHsaId)

        //Then
        def slurper = new JsonSlurper()
        assertEquals(slurper.parseText(expected), slurper.parseText(anonymized))
    }
}
