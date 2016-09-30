/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.testsupport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.UtlatandeBuilder;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HsaDataInjectable;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.report.util.Icd10;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.warehouse.WidelineConverter;

public class TestIntygInjector {
    private static final int SEED = 1235;

    private static final Logger LOG = LoggerFactory.getLogger(TestIntygInjector.class);

    private static final int LONG_PERIOD_DAYS = 365;
    private static final int SHORT_PERIOD_DAYS = 30;
    private static final float LONG_PERIOD_FRACTION = 0.1f;

    private static final int MONTHS = 19;

    private LocalDate base;

    private static final List<HsaIdVardgivare> VG = Arrays.asList(new HsaIdVardgivare("vg1"), new HsaIdVardgivare("vg2"), new HsaIdVardgivare("vg3"), new HsaIdVardgivare("vg4"), new HsaIdVardgivare("vg5"));

    private static final List<HsaIdLakare> LAKARE = new ArrayList<>();

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final Multimap<HsaIdVardgivare, HsaIdEnhet> VARDGIVARE = ArrayListMultimap.create();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 25, 50, 75);
    public static final float FEL_DIAGNOS_THRESHOLD = 0.1f;

    private static Random random = new Random(SEED);

    public static final int VARDGIVARE_ANTAL = 20;

    public static final int NUMBER_OF_LAKARE_TO_ADD = 100;

    static {
        for (HsaIdVardgivare vardgivare : VG) {
            for (int i = 1; i <= VARDGIVARE_ANTAL; i++) {
                VARDGIVARE.put(vardgivare, new HsaIdEnhet(vardgivare + "-enhet-" + i));
            }
        }
        for (int i = 1; i < NUMBER_OF_LAKARE_TO_ADD; i++) {
            LAKARE.add(new HsaIdLakare("hsa-" + i));
        }
    }

    @Autowired
    private Icd10 icd10;

    @Autowired
    private HsaDataInjectable hsaDataInjectable;

    @Autowired
    private RestSupportService restSupportService;

    @Autowired
    private Clock clock;

    private List<String> getDiagnoser() {
        if (DIAGNOSER.isEmpty()) {
            for (Icd10.Kapitel kapitel : icd10.getKapitel(true)) {
                for (Icd10.Avsnitt avsnitt : kapitel.getAvsnitt()) {
                    for (Icd10.Kategori kategori : avsnitt.getKategori()) {
                        for (Icd10.Id id : kategori.getKods()) {
                            DIAGNOSER.add(id.getId());
                        }
                    }
                }
            }
        }
        return DIAGNOSER;
    }

    @PostConstruct
    public void init() {
        base = LocalDate.now(clock).minusMonths(MONTHS);
        restSupportService.clearDatabase();
        publishUtlatanden();
        restSupportService.processIntyg();
    }

    private void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        System.out.println(System.getProperty("statistics.test.max.intyg"));
        int maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.intyg", "0"));
        List<String> personNummers = readList("/personnr/testpersoner.log", maxIntyg);

        LOG.info("Inserting " + personNummers.size() + " certificates");
        for (String id : personNummers) {
            createAndInsertIntyg(builder, id);
        }
        LOG.info("Inserting " + personNummers.size() + " certificates completed. Use -Dstatistics.test.max.intyg=<x> to limit inserts.");
    }

    private void createAndInsertIntyg(UtlatandeBuilder builder, String patientId) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate start = base.plusMonths(random.nextInt(MONTHS)).plusDays(random.nextInt(SHORT_PERIOD_DAYS));
        LocalDate end = random.nextFloat() < LONG_PERIOD_FRACTION ? start.plusDays(random.nextInt(LONG_PERIOD_DAYS) + 7) : start.plusDays(random.nextInt(SHORT_PERIOD_DAYS) + 7);
        // CHECKSTYLE:ON MagicNumber

        HsaIdVardgivare vardgivare = random(VG);

        HsaIdEnhet vardenhet = random(Lists.newArrayList((VARDGIVARE.get(vardgivare))));

        HsaIdLakare lakare = random(LAKARE);

        String diagnos = random(getDiagnoser());
        if (random.nextFloat() < FEL_DIAGNOS_THRESHOLD) {
            diagnos = WidelineConverter.UNKNOWN_DIAGNOS;
        }

        int arbetsformaga = random(ARBETSFORMAGOR);

        hsaDataInjectable.setHsaKey(new HSAKey(vardgivare, vardenhet, lakare));

        final JsonNode data = builder.build(patientId, start, end, lakare, vardenhet, vardgivare, diagnos, arbetsformaga);
        final EventType type = EventType.CREATED;
        final String id = data.path("id").textValue();
        final long timestamp = System.currentTimeMillis();
        final String vgId = vardgivare.getId();
        final String enhetId = vardenhet.getId();
        final String lakareId = lakare.getId();
        final String enhetName = "Enheten " + enhetId;
        final Intyg intyg = new Intyg(type, data.toString(), id, timestamp, null, null, enhetName, vgId, enhetId, lakareId);
        restSupportService.insertIntygWithoutLogging(intyg);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private List<String> readList(final String path, final int maxIntyg) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "utf8"))) {
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
