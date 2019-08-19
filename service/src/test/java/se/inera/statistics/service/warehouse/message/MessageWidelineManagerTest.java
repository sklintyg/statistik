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
package se.inera.statistics.service.warehouse.message;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MessageWidelineManagerTest {

    @Autowired
    private MessageWidelineManager messageWidelineManager;

    @Test
    public void countLinesWhenEmpty() {
        int result = messageWidelineManager.count();

        assertEquals(0, result);
    }

    @Test
    public void count3Lines() {
        insertLine(MessageEventType.SENT, ":1");
        insertLine(MessageEventType.SENT, ":2");
        insertLine(MessageEventType.SENT, ":3");

        int result = messageWidelineManager.count();

        assertEquals(3, result);
    }

    private void insertLine(MessageEventType event, String correlationId) {
        MessageWideLine line1 = new MessageWideLine();
        String patientId = "19121212-1212";
        line1.setAlder(23);
        line1.setEnhet("e1");
        line1.setKon(1);
        line1.setMeddelandeTyp(event);
        line1.setPatientid(patientId);
        line1.setVardgivareid("v1");
        line1.setSkickatDate(LocalDate.now());
        line1.setSkickatTidpunkt(LocalTime.now());
        line1.setIntygId("i-123");
        line1.setMeddelandeId(correlationId);
        line1.setIntygstyp(IntygType.LISJP);
        line1.setIntygSigneringsdatum(LocalDate.now());
        line1.setIntygLakareId("123");
        line1.setIntygDx("");

        messageWidelineManager.saveWideline(line1);
    }
}
