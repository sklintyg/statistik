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

package se.inera.statistics.queue;

import org.junit.Test;

import se.inera.statistics.context.StartUp;

public class IntygSenderIntegrationTest {

    StartUp startUp = new StartUp();
    IntygSender intygSender;

    @Test
    public void verifySendIntyg() {
        try {
            startUp.startContext("fitnesse-context.xml");
            intygSender = new IntygSender();
            intygSender.sendIntyg("19790407-9295", "G01", "2013-02-05", "2013-09-06", "50", "","","", "","" ,"","","","","ENVE", "EnVG", "1");
            intygSender.sleep(5000);
            intygSender.getResult("ENVE", "TVAVE", "2012-01-01", "2013-11-01");
            IntygSender.getTestResult();

        } finally {
            startUp.stopContext();
        }
    }

    @Test
    public void verifyAwaitResult() {
        try {
            startUp.startContext("fitnesse-context.xml");
            intygSender = new IntygSender();
            intygSender.sendIntyg("19790407-9295", "G01", "2013-02-05", "2013-09-06", "50", "","","", "","" ,"","","","","ENVE", "EnVG", "1");
            intygSender.awaitResult();
        } finally {
            startUp.stopContext();
        }
    }
}
