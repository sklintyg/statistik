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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UpdateEnhetNamnFromHsaFileServiceTest {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private UpdateEnhetNamnFromHsaFileService updateEnhetNamnFromHsaFileService;

    private int enhetId = 0;

    @Test
    @Ignore
    public void testMain() throws Exception {
        //Given.

        //Does exist in the hsaunits file and should be updated
        final String hsaId1 = "SE2120001231-01F8KO";
        final String namn1 = "namn1";

        //Does not exist in the hsaunits file
        final String hsaId2 = "SE2120001231-01F8K1";
        final String namn2 = "namn2";

        //Does exist in the hsaunits file and should be updated
        final String hsaId3 = "SE2120001231-0090KO";
        final String namn3 = "namn3";

        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertEnhet(hsaId1, namn1);
                insertEnhet(hsaId2, namn2);
                insertEnhet(hsaId3, namn3);
            }

            private void insertEnhet(final String hsaId, final String namn) {
                manager.createNativeQuery("INSERT INTO enhet (id, enhetId, namn, vardgivareId, lansId, kommunId, verksamhetsTyper) VALUES "
                    + "(" + enhetId++ + ", '" + hsaId + "', '" + namn + "', 'vg1', '1', '1', '1,2,3')").executeUpdate();
            }
        });

        //When
        final URL hsaUnitFile = getClass().getResource("/hsaunitsTest.xml");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    updateEnhetNamnFromHsaFileService.doUpdateEnhetnamesFromHsaFileServiceInputstream(hsaUnitFile.openStream());
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });

        //Then
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                final Object[] result1 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId1 + "'")
                    .getSingleResult();
                assertEquals("Hjortsberg", result1[2]);
                final Object[] result2 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId2 + "'")
                    .getSingleResult();
                assertEquals(namn2, result2[2]);
                final Object[] result3 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId3 + "'")
                    .getSingleResult();
                assertEquals("Hjortsberg - Ljungheden", result3[2]);
            }
        });
    }

    /**
     * This test will do an end to end test of the fileservice. A webserver will be started locally which makes it possible to download a
     * local fileservice zip file. That file will be downloaded and used to update the database.
     */
    @Test
    @Ignore
    public void e2etest() throws Exception {
        //Given

        //Does exist in the hsaunits file and should be updated
        final String hsaId1 = "SE2120001231-01F8KO";
        final String namn1 = "namn1";

        //Does not exist in the hsaunits file
        final String hsaId2 = "HsaidNotExistingInFileserviceFile";
        final String namn2 = "namn2";

        //Does exist in the hsaunits file and should be updated
        final String hsaId3 = "SE2120001231-0090KO";
        final String namn3 = "namn3";

        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                insertEnhet(hsaId1, namn1);
                insertEnhet(hsaId2, namn2);
                insertEnhet(hsaId3, namn3);
            }

            private void insertEnhet(final String hsaId, final String namn) {
                manager.createNativeQuery("INSERT INTO enhet (id, enhetId, namn, vardgivareId, lansId, kommunId, verksamhetsTyper) VALUES "
                    + "(" + enhetId++ + ", '" + hsaId + "', '" + namn + "', 'vg1', '1', '1', '1,2,3')").executeUpdate();
            }
        });

        final Server server = new Server();
        final ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(0);
        server.addConnector(serverConnector);
        ClassLoader cl = getClass().getClassLoader();
        URL file = cl.getResource("hsaunitsTest.xml");
        URI webRootUri = file.toURI().resolve("./").normalize();
        ResourceHandler handler = new ResourceHandler();
        final var resourceFactory = ResourceFactory.of(handler);
        handler.setBaseResource(resourceFactory.newResource(webRootUri));
        handler.setDirAllowed(true);
        server.setHandler(handler);
        server.start();
        final int port = serverConnector.getLocalPort();

        ReflectionTestUtils
            .setField(updateEnhetNamnFromHsaFileService, "hsaunitsUrl", "http://localhost:" + port + "/hsazip-fileservice-intyg-p.zip");
        ReflectionTestUtils.setField(updateEnhetNamnFromHsaFileService, "hsaCertificateFile", cl.getResource("certificate.p12").getFile());
        ReflectionTestUtils.setField(updateEnhetNamnFromHsaFileService, "hsaCertificatePassword", "statistik");
        ReflectionTestUtils
            .setField(updateEnhetNamnFromHsaFileService, "hsaTruststoreFile", cl.getResource("test.statistik.jks").getFile());
        ReflectionTestUtils.setField(updateEnhetNamnFromHsaFileService, "hsaTruststorePassword", "statistik");

        //When
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

                updateEnhetNamnFromHsaFileService.doUpdateEnhetnamesFromHsaFileService();
            }
        });

        //Then
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                final Object[] result1 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId1 + "'")
                    .getSingleResult();
                assertEquals("Hjortsberg", result1[2]);
                final Object[] result2 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId2 + "'")
                    .getSingleResult();
                assertEquals(namn2, result2[2]);
                final Object[] result3 = (Object[]) manager
                    .createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId3 + "'")
                    .getSingleResult();
                assertEquals("Hjortsberg - Ljungheden", result3[2]);
            }
        });

        server.stop();
    }

}