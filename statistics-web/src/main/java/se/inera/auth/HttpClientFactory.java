package se.inera.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;
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

        Protocol protocol = new Protocol("https", new KeystoreBasedSocketFactory(trustStoreFile, trustStorePassword), HTTPS_PORT);
        Protocol.registerProtocol("https", protocol);

        return httpClient;
    }
}
