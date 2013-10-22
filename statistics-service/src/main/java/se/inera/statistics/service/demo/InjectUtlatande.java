package se.inera.statistics.service.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.common.CommonPersistence;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.queue.Receiver;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class InjectUtlatande {
    private static final int SEED = 1234;

    private static final Logger LOG = LoggerFactory.getLogger(InjectUtlatande.class);

    private static final int DAYS = 30;

    private static final int MONTHS = 19;

    private static final LocalDate BASE = new LocalDate("2012-03-01");

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<String> VARDENHETER = Arrays.asList("verksamhet1", "verksamhet2");
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 25, 50, 75);

    private static Random random = new Random(SEED);

    @Autowired
    private CommonPersistence persistence;

    @Autowired
    private Receiver receiver;

    static {
        for (DiagnosisGroup mainGroup: DiagnosisGroupsUtil.getAllDiagnosisGroups()) {
            for (DiagnosisGroup group: DiagnosisGroupsUtil.getSubGroups(mainGroup.getId())) {
                DIAGNOSER.add(group.getId().split("-")[0]);
            }
        }
    }

    @PostConstruct
    public void init() {
        cleanupDB();
        new Thread(new Runnable() {
            public void run() {
                publishUtlatanden();
            }
        }).start();
    }

    private void cleanupDB() {
        persistence.cleanDb();
    }

    private void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();

        List<String> personNummers = readList("/personnr/testpersoner.log");

        LOG.info("Inserting " + personNummers.size() + " certificates");
        for (String id : personNummers) {
            JsonNode newPermutation = permutate(builder, id);
            accept(newPermutation.toString(), newPermutation.path("id").path("extension").textValue());
        }
        LOG.info("Inserting " + personNummers.size() + " certificates completed");
    }

    public static JsonNode permutate(UtlatandeBuilder builder, String patientId) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate start = BASE.plusMonths(random.nextInt(MONTHS)).plusDays(random.nextInt(DAYS));
        LocalDate end = start.plusDays(random.nextInt(DAYS) + 7);
        // CHECKSTYLE:ON MagicNumber

        String vardenhet = random(VARDENHETER);

        String diagnos = random(DIAGNOSER);

        int arbetsformaga = random(ARBETSFORMAGOR);
        return builder.build(patientId, start, end, vardenhet, diagnos, arbetsformaga);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private void accept(final String intyg, final String correlationId) {
        receiver.accept(EventType.CREATED, intyg, correlationId, System.currentTimeMillis());
    }

    private List<String> readList(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));) {
            List<String> list = new ArrayList<>();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
