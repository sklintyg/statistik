/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.demo.LargeTestDataGenerator;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import java.io.FileNotFoundException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:warehouse-integration-test.xml", "classpath:icd10.xml"  })
@DirtiesContext
public class WarehouseIntegrationTest {

    @Autowired
    private Warehouse warehouse;

    @Autowired 
    private LargeTestDataGenerator dataGenerator;

    @Test
    public void addingManyIntyg() throws InterruptedException {
        dataGenerator.publishUtlatanden();
        final Aisle aisle = warehouse.get("vardgivare1");

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    measureSjukfall(aisle);
                }
            });
        }
        
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        showMem();
    }

    @Test
    public void exportManyIntyg() throws FileNotFoundException {
        dataGenerator.publishUtlatanden();

        String result = dataGenerator.exportUtlatanden();

        //System.setOut(new PrintStream("intyg.txt"));
        assertTrue(result.length() > 0);
    }

    private void measureSjukfall(Aisle aisle) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        int totalDays = 0;
        int realDays = 0;
        int totalIntyg = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (Sjukfall sjukfall: sjukfalls) {
            totalDays += (sjukfall.getEnd() - sjukfall.getStart());
            realDays += sjukfall.getRealDays();
            totalIntyg += sjukfall.getIntygCount();
        }
        stopWatch.stop();
        System.err.format("Sjukfall %1$s Real days %2$s Total days %3$s intyg %4$s %5$sms%n", sjukfalls.size(), realDays, totalDays, totalIntyg, stopWatch.getTotalTimeMillis());
    }

    private void showMem() {
        StringBuilder gc = new StringBuilder();
        for (GarbageCollectorMXBean bean: ManagementFactory.getGarbageCollectorMXBeans()) {
            gc.append(bean.getCollectionCount())
            .append(" ").append(bean.getCollectionTime()).append(" ");
        }
        
        Runtime r = Runtime.getRuntime();
        long used = r.totalMemory() - r.freeMemory();
        System.err.format("Memory total %1$s free %2$s used %3$s %4$s%n", r.totalMemory(), r.freeMemory(), used, gc.toString());
    }
}
