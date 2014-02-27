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

    static Assertion getSamlAssertion() throws ConfigurationException, SAXException, IOException, ParserConfigurationException, UnmarshallingException {
        return getSamlAssertion("/test-saml-biljett.xml");
    }

    static Assertion getSamlAssertion(String ticketFile) throws ConfigurationException, SAXException, IOException, ParserConfigurationException, UnmarshallingException {
        DefaultBootstrap.bootstrap();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        Document document = documentBuilderFactory.newDocumentBuilder().parse(SakerhetstjanstAssertionTest.class.getResourceAsStream(ticketFile));

        return (Assertion) Configuration.getUnmarshallerFactory().getUnmarshaller(document.getDocumentElement()).unmarshall(document.getDocumentElement());
    }

    @Test
    public void getEnhet() throws ConfigurationException, ParserConfigurationException, IOException, SAXException, UnmarshallingException {
        assertEquals("IFV1239877878-103F", sakerhetstjanstAssertion.getEnhetHsaId());
    }

    @Test
    public void getSystemRolls() throws ConfigurationException, ParserConfigurationException, IOException, SAXException, UnmarshallingException {
        assertEquals(Arrays.asList("BIF;Loggadministratör", "INTYG;Statistik"), sakerhetstjanstAssertion.getSystemRolls());
    }
}
