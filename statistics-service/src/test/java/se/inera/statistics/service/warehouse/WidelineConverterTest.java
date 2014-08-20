/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.warehouse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:warehouse-test.xml", "classpath:icd10.xml" })
public class WidelineConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineConverterTest.class);

    WideLine wideLine = new WideLine(1, "{id}", "256002", "enhet", 1, EventType.CREATED, "19121212-1212", 4000, 4021, 0, 45, "A00", "A00-A09", "A00-B99", 100, 0, 32, "201010", "vardgivare");
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
    public void errorOnMissingIcd10() throws Exception {
        wideLine.setDiagnoskategori(null);
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnMissingVardgivare() throws Exception {
        wideLine.setVardgivareId("");
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
    public void allErrorsAreReported() throws Exception {
        List<String> errors = converter.validate(new WideLine());

        LOG.error("Error message: {}", errors);
        assertEquals(6, errors.size());
    }
}
