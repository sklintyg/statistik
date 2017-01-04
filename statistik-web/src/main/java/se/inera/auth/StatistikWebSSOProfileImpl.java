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
package se.inera.auth;

import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

/**
 * Created by eriklupander on 2016-06-29.
 */
public class StatistikWebSSOProfileImpl extends org.springframework.security.saml.websso.WebSSOProfileImpl {

    private static final String URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_TLSCLIENT = "urn:oasis:names:tc:SAML:2.0:ac:classes:TLSClient";
    /**
     * Returns AuthnRequest SAML message to be used to demand authentication from an IDP described using
     * idpEntityDescriptor, with an expected response to the assertionConsumer address.
     *
     * This overridden version explicitly sets the attributeConsumingServiceIndex for better control over IdP behaviour.
     *
     * @param context           message context
     * @param options           preferences of message creation
     * @param assertionConsumer assertion consumer where the IDP should respond
     * @param bindingService    service used to deliver the request
     * @return authnRequest ready to be sent to IDP
     * @throws org.opensaml.common.SAMLException             error creating the message
     * @throws org.opensaml.saml2.metadata.provider.MetadataProviderException error retreiving metadata
     */
    @Override
    protected AuthnRequest getAuthnRequest(SAMLMessageContext context, WebSSOProfileOptions options,
                                           AssertionConsumerService assertionConsumer,
                                           SingleSignOnService bindingService) throws SAMLException, MetadataProviderException {

        AuthnRequest authnRequest = super.getAuthnRequest(context, options, assertionConsumer, bindingService);

        // Only specify attributeConsumingServiceIndex for SITHS-based authentications.
        if (options.getAuthnContexts().contains(URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_TLSCLIENT)) {
            authnRequest.setAttributeConsumingServiceIndex(1);
        }
        return authnRequest;
    }
}
