package se.inera.statistics.service.warehouse;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.demo.LargeTestDataGenerator;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HSAServiceMock;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:warehouse-test.xml" })
public class WarehouseTest {

    private HSAService hsaService = new HSAServiceMock();

    private JsonNode rawDocument = JSONParser.parse(JSONSource.readTemplateAsString());
    private JsonNode hsaInfo = hsaService.getHSAInfo(null);
    
    @Autowired
    private Warehouse warehouse;

    @Autowired 
    private LargeTestDataGenerator dataGenerator;

    @Test
    public void addingIntygAddsToCorrectAisle() {
        JsonNode document = DocumentHelper.prepare(rawDocument, hsaInfo);
        warehouse.accept(document);
        Aisle aisle = warehouse.get("VardgivarId");
        assertEquals(1, aisle.getSize());
    }

    @Test
    public void addingManyIntyg() throws InterruptedException {
        System.err.println(warehouse);
        showMem(); 
        dataGenerator.publishUtlatanden();
        showMem();
        System.gc();
        showMem();
        System.err.println(warehouse);
        final Aisle aisle = warehouse.get("vardgivare1");
        //assertEquals(790, aisle.getSize());
        ExecutorService pool = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    measureSjukfall(aisle);
                    long end = System.currentTimeMillis();
                    System.err.println((end - start));
                }
            });
        }
        
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        System.err.println("Total time " + (end - start));
        showMem();
    }

    @Test
    public void exportManyIntyg() throws FileNotFoundException {
        dataGenerator.publishUtlatanden();

        String result = dataGenerator.exportUtlatanden();

        //System.setOut(new PrintStream("intyg.txt"));
        System.out.println(result);
    }

    private void measureSjukfall(Aisle aisle) {
        long start = System.currentTimeMillis();
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        long end = System.currentTimeMillis();
        int totalDays = 0;
        int realDays = 0;
        int totalIntyg = 0;
        for (Sjukfall sjukfall: sjukfalls) {
            totalDays += (sjukfall.end - sjukfall.start);
            realDays += sjukfall.realDays;
            totalIntyg += sjukfall.intygCount;
        }
        System.err.format("Sjukfall %1$s Real days %2$s Total days %3$s intyg %4$s%n", sjukfalls.size(), realDays, totalDays, totalIntyg);
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
