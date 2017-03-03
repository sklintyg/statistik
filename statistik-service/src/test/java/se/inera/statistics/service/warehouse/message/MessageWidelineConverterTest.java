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
package se.inera.statistics.service.warehouse.message;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.PartKod;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageWidelineConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(MessageWidelineConverterTest.class);

    private MessageWideLine wideLine = new MessageWideLine(1, 1, "1231", "123-123-123", MessageEventType.SENT, "19121212-1212", LocalDateTime.now(), PartKod.FKASSA, "AMNE", 1, 103, "e1", "v1");
    private MessageWidelineConverter converter = new MessageWidelineConverter();

    @Test
    public void noErrorsOnValidLine() throws Exception {
        List<String> errors = converter.validate(wideLine);
        assertEquals(0, errors.size());
    }

    @Test
    public void errorOnMissingVardgivare() throws Exception {
        wideLine.setVardgivareid("");
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
    public void errorOnPatientId() throws Exception {
        wideLine.setPatientid("");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnIntygsId() throws Exception {
        wideLine.setIntygId("");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void errorOnMeddelandeId() throws Exception {
        wideLine.setMeddelandeId("");
        List<String> errors = converter.validate(wideLine);

        LOG.error("Error message: {}", errors);
        assertEquals(1, errors.size());
    }

    @Test
    public void allErrorsAreReported() throws Exception {
        List<String> errors = converter.validate(new MessageWideLine());

        LOG.error("Error message: {}", errors);
        assertEquals(5, errors.size());
    }

}
