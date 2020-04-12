/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HsaUnitSource {

    private static final Logger LOG = LoggerFactory.getLogger(HsaUnitSource.class);

    private static final int SSL_PORT = 443;
    private static final int HTTP_PORT = 8000;
    private static final int BUFFER_SIZE = 10000;

    private HsaUnitSource() {
    }

    public static InputStream getUnits(String certFileName, String certPass, String trustStoreName, String trustPass, String url) {
        try (InputStream keystoreInput = new BufferedInputStream(new FileInputStream(certFileName));
            InputStream truststoreInput = new BufferedInputStream(new FileInputStream(trustStoreName))) {
            final HttpParams httpParams = new BasicHttpParams();

            final KeyStore keystore = KeyStore.getInstance("pkcs12");
            keystore.load(keystoreInput, certPass.toCharArray());

            KeyStore truststore = KeyStore.getInstance("jks");
            truststore.load(truststoreInput, trustPass.toCharArray());

            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", HTTP_PORT, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", SSL_PORT, new SSLSocketFactory(keystore, certPass, truststore)));

            final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager(schemeRegistry), httpParams);

            final File fetchedFile = File.createTempFile("hsazip-", ".zip");
            LOG.info("Fetching data from: " + url + " and saving into file: " + fetchedFile);
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
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
                        throw new IOException(
                            "Reported file length does not match actual read bytes." + len + " vs. " + hsaUnitsBytes.length);
                    } else {
                        return new ByteArrayInputStream(hsaUnitsBytes);
                    }
                }
            }
            throw new IOException("Response entity is null.");
        } catch (CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyStoreException
            | KeyManagementException e) {
            LOG.error("Failed to get units", e);
            e.printStackTrace();
        }
        LOG.error("Error while fetching unit names from HSA");
        return null;
    }
}
