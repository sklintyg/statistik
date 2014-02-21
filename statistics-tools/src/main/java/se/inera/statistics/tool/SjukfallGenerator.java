package se.inera.statistics.service.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kapitel;
import se.inera.statistics.service.report.util.Icd10.Kategori;

import com.fasterxml.jackson.databind.JsonNode;

public class SjukfallGenerator {

    private static final int NUMBER_OF_LAKARE = 30000;
    private static final int NUMBER_OF_UNITS = 6000;
    private static final int NUMBER_OF_VARDGIVARE = 500;

    private static final int INTYG_PER_DAY = 3000;
    private static final int INTYG_PER_DAY_VARIATION = 500;

    private static final int SEED = 1234;

    private static final int PERIOD_MIN_DAYS = 7;
    private static final int PERIOD_MAX_DAYS = 45;
    private static final int PERIOD_LONG_MAX_DAYS = 365;
    private static final float LONG_PERIOD_FRACTION = 0.05f;
    private static final float PERIOD_CONTINUED_FRACTION = 0.2f;
    private static final float PERIOD_RECONTINUED_FRACTION = 0.8f;

    private static final int MIN_AGE_DAYS = 365 * 10;
    private static final int MAX_AGE_DAYS = 365 * 80;

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 0, 0, 25, 50, 75);

    private static Random random = new Random(SEED);

    @Autowired
    private Icd10 icd10;

    private Map<LocalDate,List<Fall>> continued = new HashMap<>();

    @Autowired
    private Consumer consumer;
    
    @PostConstruct
    public void init() {
        for (Kapitel kapitel: icd10.getKapitel()) {
            for (Avsnitt avsnitt: kapitel.getAvsnitt()) {
               for (Kategori kategori: avsnitt.getKategori()) {
                   DIAGNOSER.add(kategori.getId());
               }
            }
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:sjukfall-generator.xml");
        SjukfallGenerator generator = applicationContext.getBean(SjukfallGenerator.class);
        generator.publishUtlatanden();
        applicationContext.close();
    }

    private void publishUtlatanden() {
        consumer.start();
        LocalDate now = new LocalDate();
        LocalDate start = now.minusMonths(19);
        for (LocalDate today = start; today.isBefore(now); today = today.plusDays(1)) {
            handleDay(today);
        }
        consumer.end();
    }

    @Transactional
    private void handleDay(LocalDate today) {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        List<Fall> toBeContinued = getContinued(today);
        int intygCount = random(INTYG_PER_DAY, INTYG_PER_DAY_VARIATION);
        int reextended = 0;
        for (Fall fall: toBeContinued) {
            String diagnos = random(DIAGNOSER);
            Integer arbetsformaga = random(ARBETSFORMAGOR);
            LocalDate intygFirstDay = today;
            LocalDate intygLastDay = today.plusDays(random.nextFloat() < LONG_PERIOD_FRACTION ? randomBetween(PERIOD_MAX_DAYS, PERIOD_LONG_MAX_DAYS) : randomBetween(PERIOD_MIN_DAYS, PERIOD_MAX_DAYS));

            JsonNode intyg = builder.build(fall.getPersonnummer(), intygFirstDay, intygLastDay, fall.getLakare().getLakareId(), fall.getLakare().getEnhetId(), fall.getLakare().getVardgivareId(), diagnos, arbetsformaga);
            consumer.accept(intyg);
            
            // Extend?
            if (random.nextDouble() < PERIOD_RECONTINUED_FRACTION) {
                LocalDate next = intygLastDay.plusDays(randomBetween(-2,4));
                List<Fall> list = getContinued(next);
                list.add(fall);
                reextended++;
            }
        }
        System.err.println("Building " + today + " new: " + intygCount + " continued: " + toBeContinued.size() + " reextended:" + reextended);

        continued.remove(today);

        for (int i = 0; i < intygCount; i ++) {
            int age = randomBetween(MIN_AGE_DAYS, MAX_AGE_DAYS);
            LocalDate birthday = today.minusDays(age);
            String personnummer = makePersonnummer(birthday);
            String diagnos = random(DIAGNOSER);
            Lakare lakare = getLakare();
            Integer arbetsformaga = random(ARBETSFORMAGOR);
            LocalDate intygFirstDay = today;
            LocalDate intygLastDay = today.plusDays(random.nextFloat() < LONG_PERIOD_FRACTION ? randomBetween(PERIOD_MAX_DAYS, PERIOD_LONG_MAX_DAYS) : randomBetween(PERIOD_MIN_DAYS, PERIOD_MAX_DAYS));

            JsonNode intyg = builder.build(personnummer, intygFirstDay, intygLastDay, lakare.getLakareId(), lakare.getEnhetId(), lakare.getVardgivareId(), diagnos, arbetsformaga);
            consumer.accept(intyg);

            // Extend?
            if (random.nextFloat() < PERIOD_CONTINUED_FRACTION) {
                LocalDate dayToContinue = intygLastDay.plusDays(randomBetween(-2,4));
                List<Fall> list = getContinued(dayToContinue);
                list.add(new Fall(personnummer, lakare));
            }
        }
    }

    private List<Fall> getContinued(LocalDate day) {
        List<Fall> list = continued.get(day);
        if (list == null) {
            list = new ArrayList<>();
            continued.put(day, list);
        }
        return list;
    }

    private Lakare getLakare() {
        int lakarId;
        if (random.nextInt(10) == 0) {
            // Make about 10% of intyg belong to a lakare for first vardgivare
            lakarId = random.nextInt(NUMBER_OF_LAKARE / NUMBER_OF_VARDGIVARE); 
        } else {
            lakarId = random.nextInt(NUMBER_OF_LAKARE);
        }
        return new Lakare(lakarId);
    }

    private String makePersonnummer(LocalDate birthday) {
        return birthday.toString("yyyyMMdd-") + String.format("%1$s04d", random.nextInt(10000));
    }

    private int randomBetween(int min, int max) {
        return min + random.nextInt(max - min);
    }

    private int random(int middle, int variation) {
        return middle - variation + random.nextInt(2 * variation);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private class Lakare {
        private final int id;

        public Lakare(int id) {
            this.id = id;
        }
        
        String getLakareId() {
            return String.format("LAKARE-%1$06d", id);
        }

        String getEnhetId() {
            return String.format("ENHET-%1$06d", id * NUMBER_OF_UNITS / NUMBER_OF_LAKARE );
        }

        String getVardgivareId() {
            return String.format("VARDGIVARE-%1$06d", id * NUMBER_OF_VARDGIVARE / NUMBER_OF_LAKARE);
        }
    }
    
    public class Fall {
        private String personnummer;
        private Lakare lakare;

        public Fall(String personnummer, Lakare lakare) {
            this.personnummer = personnummer;
            this.lakare = lakare;
        }
        public String getPersonnummer() {
            return personnummer;
        }
        public Lakare getLakare() {
            return lakare;
        }
    }
    public static interface Consumer {
        void start();
        void accept(JsonNode intyg);
        void end();
    }
    
    public static class PrintConsumer implements Consumer {
        private GZIPOutputStream out;

        @Override
        public void accept(JsonNode intyg) {
            try {
                out.write(intyg.toString().getBytes());
                out.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void start() {
            try {
                out = new GZIPOutputStream(new FileOutputStream("intyg.json.gz"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void end() {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
