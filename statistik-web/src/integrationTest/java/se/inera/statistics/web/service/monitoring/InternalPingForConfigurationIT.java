/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.monitoring;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import se.inera.statistics.web.service.BaseIntegrationTest;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class InternalPingForConfigurationIT extends BaseIntegrationTest {

    private static final String BASE = "Envelope.Body.InternalPingForConfigurationResponse.";
    private static final String INTERNAL_PING_FOR_CONFIGURATION_V1_0 = "services/internal-ping-for-configuration/v1.0";

    private ST requestTemplate;
    private STGroup templateGroup;


    @Before
    public void setup() throws IOException {
        // Setup String template resource
        templateGroup = new STGroupFile("integrationtestTemplates/internalPingForConfiguration.v1.stg");
        requestTemplate = templateGroup.getInstanceOf("request");
    }

    @Test
    public void testInternalPingForConfiguration() throws Exception {
        System.out.println(RestAssured.baseURI + INTERNAL_PING_FOR_CONFIGURATION_V1_0);

        given().body(requestTemplate.render())
                .when()
                .post(RestAssured.baseURI + INTERNAL_PING_FOR_CONFIGURATION_V1_0)
                .then().statusCode(200)
                .rootPath(BASE)
                .body("configuration[0].name", is("dbStatus"))
                .body("configuration[0].value", is("ok"));

    }

}
