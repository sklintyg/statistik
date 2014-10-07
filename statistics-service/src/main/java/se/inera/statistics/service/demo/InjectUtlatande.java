/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.common.CommonPersistence;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.scheduler.LogJob;
import se.inera.statistics.service.warehouse.WarehouseManager;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InjectUtlatande {
    private static final int SEED = 1234;

    private static final Logger LOG = LoggerFactory.getLogger(InjectUtlatande.class);

    private static final int LONG_PERIOD_DAYS = 365;
    private static final int SHORT_PERIOD_DAYS = 30;
    private static final float LONG_PERIOD_FRACTION = 0.1f;

    private static final int MONTHS = 19;

    private static final LocalDate BASE = new LocalDate().minusMonths(MONTHS);

    private static final List<String> VG = Arrays.asList("vg1", "vg2", "vg3", "vg4", "vg5");

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final Multimap<String, String> VARDGIVARE = ArrayListMultimap.create();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 25, 50, 75);
    public static final float FEL_DIAGNOS_THRESHOLD = 0.1f;

    private static Random random = new Random(SEED);

    public static final int VARDGIVARE_ANTAL = 20;

    static {
        for (String vardgivare : VG) {
            for (int i = 1; i <= random.nextInt(VARDGIVARE_ANTAL); i++) {
                VARDGIVARE.put(vardgivare, vardgivare + "-enhet-" + i);
            }
        }
    }

    @Autowired
    private CommonPersistence persistence;

    @Autowired
    private Receiver receiver;

    @Autowired
    private LogJob logJob;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private WarehouseManager warehouseManager;

    private List<String> getDiagnoser() {
        if (DIAGNOSER.isEmpty()) {
            for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
                for (Icd10.Avsnitt avsnitt : kapitel.getAvsnitt()) {
                    for (Icd10.Kategori kategori : avsnitt.getKategori()) {
                        DIAGNOSER.add(kategori.getId());
                    }
                }
            }
        }
        return DIAGNOSER;
    }

    @PostConstruct
    public void init() {
        cleanupDB();
        publishUtlatanden();
        logJob.checkLog();
        warehouseManager.loadWideLines();
    }

    private void cleanupDB() {
        persistence.cleanDb();
    }

    private void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        System.out.println(System.getProperty("statistics.test.max.intyg"));
        int maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.intyg", "0"));
        List<String> personNummers = readList("/personnr/testpersoner.log", maxIntyg);

        LOG.info("Inserting " + personNummers.size() + " certificates");
        for (String id : personNummers) {
            JsonNode newPermutation = permutate(builder, id);
            accept(newPermutation.toString(), newPermutation.path("id").path("root").textValue());
        }
        LOG.info("Inserting " + personNummers.size() + " certificates completed");
    }

    public JsonNode permutate(UtlatandeBuilder builder, String patientId) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate start = BASE.plusMonths(random.nextInt(MONTHS)).plusDays(random.nextInt(SHORT_PERIOD_DAYS));
        LocalDate end = random.nextFloat() < LONG_PERIOD_FRACTION ? start.plusDays(random.nextInt(LONG_PERIOD_DAYS) + 7) : start.plusDays(random.nextInt(SHORT_PERIOD_DAYS) + 7);
        // CHECKSTYLE:ON MagicNumber

        String vardgivare = random(VG);

        String vardenhet = random(Lists.newArrayList((VARDGIVARE.get(vardgivare))));

        String diagnos = random(getDiagnoser());
        if (random.nextFloat() < FEL_DIAGNOS_THRESHOLD) {
            diagnos = "unknown";
        }

        int arbetsformaga = random(ARBETSFORMAGOR);
        return builder.build(patientId, start, end, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private void accept(final String intyg, final String correlationId) {
        receiver.accept(EventType.CREATED, intyg, correlationId, System.currentTimeMillis());
    }

    private List<String> readList(final String path, final int maxIntyg) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"));) {
            List<String> list = new ArrayList<>();
            int count = 0;
            for (String line = in.readLine(); line != null && (maxIntyg == 0 || count++ < maxIntyg); line = in.readLine()) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
