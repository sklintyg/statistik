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
package se.inera.statistics.service
/**
 * Created with IntelliJ IDEA.
 * User: inera
 * Date: 9/24/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
class JSONSource {

    static String readTemplateAsString() {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/maximalt-fk7263-internal.json"))
        doc.text
    }

    static String readHSASample() {
        readHSASample("hsa_example")
    }

    static String readHSASample(name) {
        def doc = new InputStreamReader(this.getClass().getResourceAsStream("/json/${name}.json"))
        doc.text
    }
}