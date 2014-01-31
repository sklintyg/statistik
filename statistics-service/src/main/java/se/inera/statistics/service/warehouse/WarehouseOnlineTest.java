package se.inera.statistics.service.warehouse;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

//import se.inera.statistics.service.demo.LargeTestDataGenerator;

public class WarehouseOnlineTest {

    private static class Watch {
        private final long start;
        private final String name;
        private final int line;
        private long stop;
        public Watch() {
            StackTraceElement element = Thread.currentThread().getStackTrace()[2];
            name = element.getClassName() + ":" + element.getMethodName();
            line = element.getLineNumber();
            start = System.currentTimeMillis();
        }
        public void stop() {
            stop = System.currentTimeMillis();
        }
        @Override
        public String toString() {
            return "Time " + name + "(" + line + "):" + (stop - start) + "ms";
        }
    }

//    @Autowired
//    private Warehouse warehouse;
//
//    private ExecutorService pool = Executors.newFixedThreadPool(10);
//
//    @Autowired 
//    private LargeTestDataGenerator dataGenerator;
//
//    @PostConstruct
//    public void addIntyg() throws InterruptedException {
//        System.err.println(warehouse);
//        showMem(); 
//        Watch watch = new Watch();
//        dataGenerator.publishUtlatanden();
//        watch.stop();
//        showMem();
//        System.err.println(warehouse);
//        System.err.println(watch);
//        Timer timer = new Timer(true);
//        timer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                execute();
//            }
//        }, 30000, 30000);
//    }
//
//    public void execute() {
//        final Aisle aisle = warehouse.get("vardgivare1");
//        for (int i = 0; i < 100; i++) {
//            pool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    measureSjukfall(aisle);
//                }
//            });
//        }
//        
//    }
//
//    private void measureSjukfall(Aisle aisle) {
//        Watch watch = new Watch();
//        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
//        watch.stop();
//        int totalDays = 0;
//        int realDays = 0;
//        int totalIntyg = 0;
//        for (Sjukfall sjukfall: sjukfalls) {
//            totalDays += (sjukfall.end - sjukfall.start);
//            realDays += sjukfall.realDays;
//            totalIntyg += sjukfall.intygCount;
//        }
//        System.err.format(watch + " %1$s Real days %2$s Total days %3$s intyg %4$s%n", sjukfalls.size(), realDays, totalDays, totalIntyg);
//    }
//
//    private void showMem() {
//        StringBuilder gc = new StringBuilder();
//        for (GarbageCollectorMXBean bean: ManagementFactory.getGarbageCollectorMXBeans()) {
//            gc.append(bean.getCollectionCount())
//            .append(" ").append(bean.getCollectionTime()).append(" ");
//        }
//        
//        Runtime r = Runtime.getRuntime();
//        long used = r.totalMemory() - r.freeMemory();
//        System.err.format("Memory total %1$s free %2$s used %3$s %4$s%n", r.totalMemory(), r.freeMemory(), used, gc.toString());
//    }

}
