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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author andreaskaltenbach
 */
public class HttpClientFactory {

    private static final int HTTPS_PORT = 443;

    @Value("${saml.truststore.file}")
    private org.springframework.core.io.Resource trustStoreFile;

    @Value("${saml.truststore.password}")
    private String trustStorePassword;

    public HttpClient createInstance() {
        HttpClient httpClient = new HttpClient();

        ProtocolSocketFactory factory = new KeystoreBasedSocketFactory(trustStoreFile, trustStorePassword);
        Protocol protocol = new Protocol("https", factory, HTTPS_PORT);
        Protocol.registerProtocol("https", protocol);

        return httpClient;
    }
}
