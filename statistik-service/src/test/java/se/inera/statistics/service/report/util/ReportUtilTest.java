/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Range;

public class ReportUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(ReportUtilTest.class);

    @Test
    public void testRangeWithinYear() {
        final Range testRange = new Range(new LocalDate(2013, 5, 1), new LocalDate(2013, 6, 1));

        Range result = ReportUtil.getPreviousPeriod(testRange);

        // CHECKSTYLE:OFF MagicNumber
        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2013, result.getFrom().getYear());
        Assert.assertEquals(3, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2013, result.getTo().getYear());
        Assert.assertEquals(4, result.getTo().getMonthOfYear());
        // CHECKSTYLE:ON MagicNumber
    }

    @Test
    public void testNextRangeWithinYear() {
        final Range testRange = new Range(new LocalDate(2013, 5, 1), new LocalDate(2013, 5, 1));

        Range result = ReportUtil.getNextPeriod(testRange);
        System.err.println(result);
        // CHECKSTYLE:OFF MagicNumber
        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2013, result.getFrom().getYear());
        Assert.assertEquals(6, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2013, result.getTo().getYear());
        Assert.assertEquals(6, result.getTo().getMonthOfYear());
        // CHECKSTYLE:ON MagicNumber
    }

    @Test
    public void testNextRangeBiggerThanOneYear() {
        final Range testRange = new Range(new LocalDate(2012, 1, 1), new LocalDate(2013, 6, 1));

        Range result = ReportUtil.getNextPeriod(testRange);

        // CHECKSTYLE:OFF MagicNumber
        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2013, result.getFrom().getYear());
        Assert.assertEquals(7, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2014, result.getTo().getYear());
        Assert.assertEquals(12, result.getTo().getMonthOfYear());
        // CHECKSTYLE:ON MagicNumber
    }

    @Test
    public void testRangeBiggerThanOneYear() {
        final Range testRange = new Range(new LocalDate(2012, 1, 1), new LocalDate(2013, 6, 1));

        Range result = ReportUtil.getPreviousPeriod(testRange);

        // CHECKSTYLE:OFF MagicNumber
        LOG.info(result.getFrom() + " -- " + result.getTo());
        Assert.assertEquals(2010, result.getFrom().getYear());
        Assert.assertEquals(7, result.getFrom().getMonthOfYear());
        Assert.assertEquals(2011, result.getTo().getYear());
        Assert.assertEquals(12, result.getTo().getMonthOfYear());
        // CHECKSTYLE:ON MagicNumber
    }
}
