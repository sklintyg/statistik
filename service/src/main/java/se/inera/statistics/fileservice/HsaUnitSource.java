/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.fileservice;

import com.google.common.io.ByteStreams;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HsaUnitSource {

    private static final Logger LOG = LoggerFactory.getLogger(HsaUnitSource.class);

    private static final int BUFFER_SIZE = 10000;

    private HsaUnitSource() {
    }

    public static InputStream getUnits(String certFileName, String certPass, String trustStoreName, String trustPass,
        String url) {

        SSLContext sslContext = buildSSLContext(certFileName, certPass, trustStoreName, trustPass);
        if (sslContext == null) {
            return null;
        }
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> registry = buildRegistry(sslConnectionSocketFactory);

        try (PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
            CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {
            final File fetchedFile = File.createTempFile("hsazip-", ".zip");
            LOG.info("Fetching data from: " + url + " and saving into file: " + fetchedFile);
            HttpGet httpget = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpget)) {
                final var entity = response.getEntity();
                if (entity != null) {
                    try (InputStream instream = entity.getContent();
                        OutputStream receivedZipFile = new FileOutputStream(fetchedFile)) {
                        byte[] buffer = new byte[BUFFER_SIZE];

                        int len;
                        while ((len = instream.read(buffer)) != -1) {
                            receivedZipFile.write(buffer, 0, len);
                        }
                    }
                    try (ZipFile zipFile = new ZipFile(fetchedFile)) {
                        final ZipEntry entry = zipFile.getEntry("hsaunits.xml");
                        int len = (int) entry.getSize();
                        final InputStream stream = new BufferedInputStream(zipFile.getInputStream(entry));
                        byte[] hsaUnitsBytes = ByteStreams.toByteArray(stream);
                        if (hsaUnitsBytes.length != len) {
                            throw new IOException("Reported file length does not match actual read bytes." + len
                                + " vs. " + hsaUnitsBytes.length);
                        } else {
                            return new ByteArrayInputStream(hsaUnitsBytes);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to get units", e);
        }

        return null;
    }

    private static SSLContext buildSSLContext(String certFileName, String certPass, String trustStoreName,
        String trustPass) {
        try (InputStream keystoreInput = new BufferedInputStream(new FileInputStream(certFileName));
            InputStream truststoreInput = new BufferedInputStream(new FileInputStream(trustStoreName))) {

            final KeyStore keystore = KeyStore.getInstance("pkcs12");
            keystore.load(keystoreInput, certPass.toCharArray());

            KeyStore truststore = KeyStore.getInstance("jks");
            truststore.load(truststoreInput, trustPass.toCharArray());

            return SSLContexts.custom()
                .loadTrustMaterial(truststore, null)
                .loadKeyMaterial(keystore, certPass.toCharArray())
                .build();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException
                 | KeyStoreException | KeyManagementException e) {
            LOG.error("Failed to build SSLContext", e);
        }

        return null;
    }

    private static Registry<ConnectionSocketFactory> buildRegistry(
        SSLConnectionSocketFactory sslConnectionSocketFactory) {
        return RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", new PlainConnectionSocketFactory())
            .register("https", sslConnectionSocketFactory)
            .build();
    }

}