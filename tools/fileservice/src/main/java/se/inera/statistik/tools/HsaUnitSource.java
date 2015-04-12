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
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class HsaUnitSource {

    public static final int SSL_PORT = 443;

    private HsaUnitSource() {
    }

    public static ByteArrayInputStream getUnits(String certFileName, String certPass, String trustStoreName, String trustPass, String url) {
        try {
            final HttpParams httpParams = new BasicHttpParams();
            final KeyStore keystore = KeyStore.getInstance("pkcs12");
            InputStream keystoreInput;
            keystoreInput = new BufferedInputStream(new FileInputStream(certFileName));
            keystore.load(keystoreInput, "keystorepassword".toCharArray());
            KeyStore truststore = KeyStore.getInstance("jks");

            InputStream truststoreInput = new BufferedInputStream(new FileInputStream("hsa-truststore.jks"));
            truststore.load(truststoreInput, "truststorepassword".toCharArray());

            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", SSL_PORT, new SSLSocketFactory(keystore, "keystorePassword", truststore)));

            final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager(schemeRegistry), httpParams);

            HttpGet httpget = new HttpGet("https://wstest.hsa.sjunet.org/hsafileservice/informationlist/hsaunits.zip");
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    ZipInputStream zipStream = new ZipInputStream(instream);
                    final ZipEntry nextEntry = zipStream.getNextEntry();
                    int len = (int) nextEntry.getSize();
                    byte[] hsaUnitsBytes = new byte[len];
                    if (instream.read(hsaUnitsBytes) != len) {
                        throw new IOException("Reported file length does not match actual read bytes.");
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
