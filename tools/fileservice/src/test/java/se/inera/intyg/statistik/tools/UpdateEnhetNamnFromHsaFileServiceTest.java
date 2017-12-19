/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.statistik.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.net.URL;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UpdateEnhetNamnFromHsaFileServiceTest {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private int enhetId = 0;

    @Test
    public void testMain() throws Exception {
        //Given

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
        final String hsaUnitFilePath = hsaUnitFile.getPath();
        final URL datasourceFile = getClass().getResource("/dataSourceTest.properties");
        final String datasourceFilePath = datasourceFile.getPath();
        UpdateEnhetNamnFromHsaFileService.main(new String[]{"-d", datasourceFilePath, "-u", hsaUnitFilePath});

        //Then
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                final Object[] result1 = (Object[]) manager.createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId1 + "'").getSingleResult();
                assertEquals("Hjortsberg", result1[2]);
                final Object[] result2 = (Object[]) manager.createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId2 + "'").getSingleResult();
                assertEquals(namn2, result2[2]);
                final Object[] result3 = (Object[]) manager.createNativeQuery("SELECT id, enhetId, namn FROM enhet WHERE enhetId='" + hsaId3 + "'").getSingleResult();
                assertEquals("Hjortsberg - Ljungheden", result3[2]);
            }
        });
    }

}
