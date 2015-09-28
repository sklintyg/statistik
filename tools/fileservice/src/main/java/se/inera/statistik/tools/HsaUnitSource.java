/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 * <p/>
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 * <p/>
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistik.tools;

import com.google.common.io.ByteStreams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

public final class HsaUnitSource {

    public static final int SSL_PORT = 443;
    private static final int BUFFER_SIZE = 10000;

    private HsaUnitSource() {
    }

    public static ByteArrayInputStream getUnits(String certFileName, String certPass, String trustStoreName, String trustPass, String url) {
        try {
            final HttpParams httpParams = new BasicHttpParams();
            final KeyStore keystore = KeyStore.getInstance("pkcs12");
            InputStream keystoreInput;
            keystoreInput = new BufferedInputStream(new FileInputStream(certFileName));
            keystore.load(keystoreInput, certPass.toCharArray());
            KeyStore truststore = KeyStore.getInstance("jks");

            InputStream truststoreInput = new BufferedInputStream(new FileInputStream(trustStoreName));
            truststore.load(truststoreInput, trustPass.toCharArray());

            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSL_PORT, new SSLSocketFactory(keystore, certPass, truststore)));

            final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager(schemeRegistry), httpParams);

            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    OutputStream receivedZipFile = new FileOutputStream("hsazip.zip");
                    int len;
                    while ((len = instream.read(buffer)) != -1) {
                        receivedZipFile.write(buffer, 0, len);
                    }
                    ZipFile zipFile = new ZipFile("hsazip.zip");
                    final ZipEntry entry = zipFile.getEntry("hsaunits.xml");
                    len = (int) entry.getSize();
                    final InputStream stream = new BufferedInputStream(zipFile.getInputStream(entry));
                    byte[] hsaUnitsBytes = ByteStreams.toByteArray(stream);
                    if (hsaUnitsBytes.length != len) {
                        throw new IOException("Reported file length does not match actual read bytes." + len + " vs. " + hsaUnitsBytes.length);
                    } else {
                        return new ByteArrayInputStream(hsaUnitsBytes);
                    }
                }
            }
            throw new IOException("Respons entity is null.");
        } catch (CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }
}
