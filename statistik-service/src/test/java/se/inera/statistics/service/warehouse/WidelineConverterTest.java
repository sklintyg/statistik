/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:warehouse-integration-test.xml", "classpath:icd10.xml" })
@DirtiesContext
public class WidelineConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineConverterTest.class);

    WideLine wideLine = new WideLine(1, "{id}", "256002", new HsaIdEnhet("enhet"), 1, EventType.CREATED, "19121212-1212", 4000, 4021, 0, 45, "A00", "A00-A09", "A00-B99", "A0000", 100, 0, 32, "201010", new HsaIdVardgivare("vardgivare"), new HsaIdLakare("lakare"), false);
    @Autowired
    private WidelineConverter converter;

    @Test
    public void noErrorsOnValidLine() throws Exception {
        List<String> errors = converter.validate(wideLine);
        assertEquals(0, errors.size());
    }

    @Test
    public void errorOnIncorrectSjukskrivningsgrad() throws Exception {
        wideLine.setSjukskrivningsgrad(0);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnMissingVardgivare() throws Exception {
        wideLine.setVardgivareId(new HsaIdVardgivare(""));
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnMissingEnhet() throws Exception {
        wideLine.setEnhet(null);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnMissingLkf() throws Exception {
        wideLine.setLkf("");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnPatientId() throws Exception {
        wideLine.setPatientid("");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnEarlyDate() throws Exception {
        wideLine.setStartdatum(0);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnLateDate() throws Exception {
        wideLine.setSlutdatum(1000000);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals("N:o of errors: " +  errors.size(), 1, errors.size());
    }

    @Test
    public void errorOnEndDateBeforeStartDateIntyg2943() throws Exception {
        wideLine.setStartdatum(7001);
        wideLine.setSlutdatum(7000);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals("N:o of errors: " +  errors.size(), 1, errors.size());
    }

    @Test
    public void errorOnLongCorrelationId() throws Exception {
        wideLine.setCorrelationId("012345678901234567890123456789012345678901234567890");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals("N:o of errors: " +  errors.size(), 1, errors.size());
    }

    @Test
    public void allErrorsAreReported() throws Exception {
        List<String> errors = converter.validate(new WideLine());

        LOG.error("Error message: {}", errors);
        assertEquals(7, errors.size());
    }

    @Test
    public void testToDay() throws Exception {
        //Given
        final int day = 5538;

        //When
        final LocalDate localDate = WidelineConverter.toDate(day);

        //Then
        assertEquals(2015, localDate.getYear());
        assertEquals(3, localDate.getMonthValue());
        assertEquals(1, localDate.getDayOfMonth());

        //When
        final int convertedDay = WidelineConverter.toDay(localDate);

        //Then
        assertEquals(day, convertedDay);
    }

}
