/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.auth;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SakerhetstjanstAssertionTest {

    private static SakerhetstjanstAssertion sakerhetstjanstAssertion;

    @BeforeClass
    public static void setupClass() throws Exception {
        sakerhetstjanstAssertion = new SakerhetstjanstAssertion(getSamlAssertion());
    }

    static Assertion getSamlAssertion() {
        return getSamlAssertion("/test-saml-biljett.xml");
    }

    static Assertion getSamlAssertion(String ticketFile) {
        try {
            DefaultBootstrap.bootstrap();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            Document document = documentBuilderFactory.newDocumentBuilder().parse(SakerhetstjanstAssertionTest.class.getResourceAsStream(ticketFile));

            return (Assertion) Configuration.getUnmarshallerFactory().getUnmarshaller(document.getDocumentElement()).unmarshall(document.getDocumentElement());
        } catch (ConfigurationException | SAXException | UnmarshallingException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEnhet() throws ConfigurationException, ParserConfigurationException, IOException, SAXException, UnmarshallingException {
        assertEquals("IFV1239877878-103F", sakerhetstjanstAssertion.getEnhetHsaId());
    }

    @Test
    public void getSystemRolls() throws ConfigurationException, ParserConfigurationException, IOException, SAXException, UnmarshallingException {
        assertEquals(Arrays.asList("BIF;Loggadministratör", "INTYG;Statistik-IFV1239877878-0001"), sakerhetstjanstAssertion.getSystemRoles());
    }
}
