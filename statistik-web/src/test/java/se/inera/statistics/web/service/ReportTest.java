package se.inera.statistics.web.service;

import org.junit.Test;

import static org.junit.Assert.*;

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
