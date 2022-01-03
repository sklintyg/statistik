/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.auth;

import static se.inera.auth.SakerhetstjanstAssertion.HSA_ID_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.inera.auth.model.User;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.Vardgivare;
import se.inera.statistics.integration.hsa.services.HsaOrganizationsService;

/**
 * @author andreaskaltenbach
 */
public class FakeAuthenticationProvider implements AuthenticationProvider {

    private static DocumentBuilder documentBuilder;

    private SAMLUserDetailsService userDetails;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    static {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DocumentBuilderInstantiationException("Failed to instantiate DocumentBuilder", e);
        }
    }

    @Override
    public Authentication authenticate(Authentication token) {

        SAMLCredential credential = createSamlCredential(token);
        Object details = userDetails.loadUserBySAML(credential);

        // As per INTYG-2638 we need to augment the principal with fake properties _after_ the loadUserBySAML.
        User user = (User) details;
        FakeCredentials fakeCredentials = (FakeCredentials) token.getCredentials();

        // Being immutable, we build up a new User principal combining the User from loadUserBySAML and fakes.

        String name = user.getName() != null && user.getName().trim().length() > 0 && !user.getName().startsWith("null") ? user.getName()
            : fakeCredentials.getFornamn() + " " + fakeCredentials.getEfternamn();

        List<Vardgivare> vardgivareSomProcessLedare = fakeCredentials.getVardgivarIdSomProcessLedare().stream()
            .filter(hsaId -> fakeCredentials.isVardgivarniva())
            .map(hsaId -> new Vardgivare(hsaId, hsaOrganizationsService.getVardgivare(new HsaIdVardgivare(hsaId)).getNamn()))
            .collect(Collectors.toList());

        User decoratedUser = new User(user.getHsaId(), name, vardgivareSomProcessLedare, user.getVardenhetList());

        ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(null, decoratedUser, credential,
            new ArrayList<>());
        result.setDetails(decoratedUser);

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FakeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private SAMLCredential createSamlCredential(Authentication token) {
        FakeCredentials fakeCredentials = (FakeCredentials) token.getCredentials();

        Assertion assertion = new AssertionBuilder().buildObject();
        AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
        assertion.getAttributeStatements().add(attributeStatement);

        attributeStatement.getAttributes().add(createAttribute(HSA_ID_ATTRIBUTE, fakeCredentials.getHsaId()));
        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setValue(token.getCredentials().toString());
        return new SAMLCredential(nameId, assertion, "fake-idp", "statistics");
    }

    private Attribute createAttribute(String name, String value) {

        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setName(name);

        Document doc = documentBuilder.newDocument();
        Element element = doc.createElement("element");
        element.setTextContent(value);

        XMLObject xmlObject = new XSStringBuilder().buildObject(new QName("ns", "local"));
        xmlObject.setDOM(element);
        attribute.getAttributeValues().add(xmlObject);

        return attribute;
    }

    public void setUserDetails(SAMLUserDetailsService userDetails) {
        this.userDetails = userDetails;
    }
}
