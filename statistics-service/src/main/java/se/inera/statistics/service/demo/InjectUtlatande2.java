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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.common.CommonPersistence;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.queue.Receiver;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.repository.NationellUpdater;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class InjectUtlatande2 {
    private static final int NUMBER_OF_UNITS = 3000;

    private static final int INTYG_PER_MONTH = 40000;

    private static final int SEED = 1234;

    private static final Logger LOG = LoggerFactory.getLogger(InjectUtlatande2.class);

    private static final int LONG_PERIOD_DAYS = 365;
    private static final int SHORT_PERIOD_DAYS = 30;
    private static final float LONG_PERIOD_FRACTION = 0.1f;

    private static final int MONTHS = 19;

    private static final LocalDate BASE = new LocalDate("2012-03-01");
    private static final LocalDate BASE_AGE = new LocalDate("1930-01-01");

    private static final int AGE_DAYS = 365 * 80;

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 0, 0, 25, 50, 75);

    private static Random random = new Random(SEED);

    @Autowired
    private CommonPersistence persistence;

    @Autowired
    private Receiver receiver;

    @Autowired
    private NationellUpdater nationellUpdater;

    @Autowired
    private DiagnosisGroupsUtil diagnosisGroupsUtil;

    @PostConstruct
    public void init() {
        for (DiagnosisGroup mainGroup: DiagnosisGroupsUtil.getAllDiagnosisGroups()) {
            for (DiagnosisGroup group: diagnosisGroupsUtil.getSubGroups(mainGroup.getId())) {
                DIAGNOSER.add(group.getId().split("-")[0]);
            }
        }

        cleanupDB();
        new Thread(new Runnable() {
            public void run() {
                publishUtlatanden();
                updateNationell();
            }
        }).start();
    }

    private void updateNationell() {
        nationellUpdater.updateSjukskrivningsgrad();
        nationellUpdater.updateSjukfallslangd();
        nationellUpdater.updateDiagnosundergrupp();
        nationellUpdater.updateDiagnosgrupp();
        nationellUpdater.updateAldersgrupp();
        nationellUpdater.updateCasesPerMonth();
    }

    private void cleanupDB() {
        persistence.cleanDb();
    }

    private void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();


        for (int month = 0; month < MONTHS; month++) {
            LocalDate base = BASE.plusMonths(month);
            for (int i = 0; i < INTYG_PER_MONTH; i++) {
                String id = randomPerson();
                JsonNode newPermutation = permutate(builder, id, base);
                try {
                    accept(newPermutation.toString(), newPermutation.path("id").path("root").textValue());
                } catch (Exception e) {
                    LOG.error("Inserting intyg", e);
                }
            }
        }
        LOG.info("Inserting certificates completed");
    }

    private String randomPerson() {
        LocalDate birthDate = BASE_AGE.plusDays(random.nextInt(AGE_DAYS));
        // CHECKSTYLE:OFF MagicNumber
        int lastDigits = random.nextInt(10000);
        // CHECKSTYLE:ON MagicNumber
        return birthDate.toString("yyyyMMdd") + String.format("%1$04d", lastDigits);
    }

    public JsonNode permutate(UtlatandeBuilder builder, String patientId, LocalDate base) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate start = base.plusDays(random.nextInt(SHORT_PERIOD_DAYS));
        LocalDate end = random.nextFloat() < LONG_PERIOD_FRACTION ? start.plusDays(random.nextInt(LONG_PERIOD_DAYS) + 7) : start.plusDays(random.nextInt(SHORT_PERIOD_DAYS) + 7);

        int vardId = random.nextInt(NUMBER_OF_UNITS);
        String vardenhet = "verksamhet" + vardId;
        String vardgivare = "vardgivare" + (vardId / 3);
        // CHECKSTYLE:ON MagicNumber

        String diagnos = random(DIAGNOSER);

        int arbetsformaga = random(ARBETSFORMAGOR);
        return builder.build(patientId, start, end, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private void accept(final String intyg, final String correlationId) {
        receiver.accept(EventType.CREATED, intyg, correlationId, System.currentTimeMillis());
    }
}
