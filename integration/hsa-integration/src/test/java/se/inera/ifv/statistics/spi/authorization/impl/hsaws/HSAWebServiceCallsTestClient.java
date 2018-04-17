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
package se.inera.ifv.statistics.spi.authorization.impl.hsaws;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;


/**
 * @author rlindsjo
 *
 */
public class HSAWebServiceCallsTestClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"HSAWebServiceCallsTest-applicationContext.xml", "hsa-services-config.xml"});
        HSAWebServiceCalls client = (HSAWebServiceCalls)ctx.getBean("wsCalls");
        try {
            client.callPing();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}
