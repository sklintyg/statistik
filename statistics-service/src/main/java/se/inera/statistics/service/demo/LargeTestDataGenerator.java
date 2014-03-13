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

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.report.util.DiagnosUtil;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Kapitel;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FactPopulator;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.helper.UtlatandeBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.WidelineConverter;
import se.inera.statistics.service.warehouse.model.db.WideLine;

public class LargeTestDataGenerator {
    private static final int NUMBER_OF_UNITS = 3000;

    private static final int INTYG_PER_DAY = 666;

    private static final int SEED = 1234;

    private static final Logger LOG = LoggerFactory.getLogger(LargeTestDataGenerator.class);

    private static final int LONG_PERIOD_DAYS = 365;
    private static final int SHORT_PERIOD_DAYS = 30;
    private static final float LONG_PERIOD_FRACTION = 0.1f;

    private static final int MONTHS = 20;

    private static final LocalDate BASE = new LocalDate("2012-03-01");
    private static final LocalDate BASE_AGE = new LocalDate("1930-01-01");

    private static final int AGE_DAYS = 365 * 80;

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 0, 0, 25, 50, 75);

    private static Random random = new Random(SEED);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private DiagnosUtil diagnosisGroupsUtil;

    @Autowired
    private HSAService hsaService;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private FactPopulator factPopulator;

    @Autowired
    private Icd10 icd10;

    @PostConstruct
    public void init() {
        for (Kapitel kapitel: icd10.getKapitel()) {
            for (Avsnitt avsnitt : kapitel.getAvsnitt()) {
                for (Kategori kategori: avsnitt.getKategori()) {
                    DIAGNOSER.add(kategori.getId());
                }
            }
        }
    }

    public void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        int count = 0;
        LocalDate lastDay = BASE.plusMonths(MONTHS);
        for (LocalDate now = BASE; !now.isAfter(lastDay); now = now.plusDays(1)) {
            for (int i = 0; i < INTYG_PER_DAY; i++) {
                String id = randomPerson();
                JsonNode utlatande = permutate(builder, id, now);
                HSAKey hsaKey = extractHSAKey(utlatande);
                JsonNode hsaInfo = hsaService.getHSAInfo(hsaKey);
                JsonNode document = DocumentHelper.prepare(utlatande, hsaInfo);
                try {
                    WideLine wideLine = widelineConverter.toWideline(document, hsaInfo, count++);
                    factPopulator.accept(wideLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Inserting certificates completed");
    }

    public String exportUtlatanden() {
        Map<String,Aisle> allVardgivare = warehouse.getAllVardgivare();
        StringBuilder result = new StringBuilder("vg;").append(Fact.HEADING).append('\n');
        for (Map.Entry<String, Aisle> entry: allVardgivare.entrySet()) {
            String vg = entry.getKey();
            for (Fact line : entry.getValue()) {
                result.append(vg).append(line.toCSVString(';'));
            }
        }
        return result.toString();
    }

    protected HSAKey extractHSAKey(JsonNode document) {
        String vardgivareId = getVardgivareId(document);
        String enhetId = getEnhetId(document);
        String lakareId = getLakarId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

    private String randomPerson() {
        LocalDate birthDate = BASE_AGE.plusDays(random.nextInt(AGE_DAYS));
        return birthDate.toString("yyyyMMdd") + String.format("%1$04d", random.nextInt(10000));
    }

    public JsonNode permutate(UtlatandeBuilder builder, String patientId, LocalDate start) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate end = random.nextFloat() < LONG_PERIOD_FRACTION ? start.plusDays(random.nextInt(LONG_PERIOD_DAYS) + 7) : start.plusDays(random.nextInt(SHORT_PERIOD_DAYS) + 7);
        // CHECKSTYLE:ON MagicNumber

        int vardId = random.nextInt(NUMBER_OF_UNITS);
        String vardenhet = "verksamhet" + vardId;
        String vardgivare = "vardgivare" + (vardId % 2);

        String diagnos = random(DIAGNOSER);

        int arbetsformaga = random(ARBETSFORMAGOR);
        return builder.build(patientId, start, end, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
