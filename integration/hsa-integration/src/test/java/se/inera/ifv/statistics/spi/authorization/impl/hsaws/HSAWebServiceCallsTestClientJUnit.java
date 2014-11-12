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
package se.inera.ifv.statistics.spi.authorization.impl.hsaws;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;


/**
 * @author Pehr Assarsson
 *
 */
public class HSAWebServiceCallsTestClientJUnit {

    ApplicationContext ctx;
    HSAWebServiceCalls client;


    @Before
    public void init(){
        ctx = new ClassPathXmlApplicationContext(new String[] {"HSAWebServiceCallsTest-applicationContext.xml", "hsa-services-config.xml"});
        client = (HSAWebServiceCalls)ctx.getBean("wsCalls");
    }

    @Ignore
    public void testHSAPing() throws Exception{
        client.callPing();

        GetStatisticsHsaUnitResponseType response = client.getStatisticsHsaUnit("IFV1239877878-103F");
        System.out.println(response);
    }

}
