/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReportTest {

    /**
     * Short name is used as part of file name when exporting and is assumed to contain no spaces or
     * "special" characters (e.g. swedish characters like "åäö").
     */
    @Test
    public void testGetShortNameShouldContainNoSpaces() throws Exception {
        final Report[] allReports = Report.values();
        for (Report report : allReports) {
            final String shortName = report.getShortName();
            assertTrue("Unexpected short name: " + shortName, shortName.matches("[0-9a-zA-Z]*"));
        }
    }

}
