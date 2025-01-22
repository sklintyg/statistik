/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.demo;

import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getVardgivareId;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAService;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kapitel;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.testsupport.UtlatandeBuilder;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.VgNumber;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineConverter;
import se.inera.statistics.service.warehouse.WidelineManager;

public class LargeTestDataGenerator {

    private static final int NUMBER_OF_UNITS = 3000;

    private static final int INTYG_PER_DAY = 666;

    private static final int SEED = 1234;

    private static final Logger LOG = LoggerFactory.getLogger(LargeTestDataGenerator.class);

    private static final int LONG_PERIOD_DAYS = 365;
    private static final int SHORT_PERIOD_DAYS = 30;
    private static final float LONG_PERIOD_FRACTION = 0.1f;

    private static final int MONTHS = 20;
    private int maxIntyg = INTYG_PER_DAY * MONTHS * (SHORT_PERIOD_DAYS + 1);

    private static final LocalDate BASE = LocalDate.parse("2012-03-01");
    private static final LocalDate BASE_AGE = LocalDate.parse("1930-01-01");

    private static final int AGE_DAYS = 365 * 80;

    private static final List<String> DIAGNOSER = new ArrayList<>();
    private static final List<Integer> ARBETSFORMAGOR = Arrays.asList(0, 0, 0, 25, 50, 75);
    public static final int EXTENSION_LIMIT = 10000;

    private static Random random = new Random(SEED);

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private HSAService hsaService;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private Icd10 icd10;

    @PostConstruct
    public void init() {
        for (Kapitel kapitel : icd10.getKapitel(true)) {
            for (Avsnitt avsnitt : kapitel.getAvsnitt()) {
                for (Kategori kategori : avsnitt.getKategori()) {
                    DIAGNOSER.add(kategori.getId());
                }
            }
        }
        maxIntyg = Integer.parseInt(System.getProperty("statistics.test.max.intyg", "" + maxIntyg));
        LOG.info("Max intyg to insert: " + maxIntyg);
    }

    public void setMaxIntyg(int maxIntyg) {
        this.maxIntyg = maxIntyg;
    }

    public void publishUtlatanden() {
        UtlatandeBuilder builder = new UtlatandeBuilder();
        int count = 0;
        LocalDate lastDay = BASE.plusMonths(MONTHS);
        maxIntyg:
        for (LocalDate now = BASE; !now.isAfter(lastDay); now = now.plusDays(1)) {
            for (int i = 0; i < INTYG_PER_DAY; i++) {
                String id = randomPerson();
                JsonNode utlatande = permutate(builder, id, now);
                HSAKey hsaKey = extractHSAKey(utlatande);
                HsaInfo hsaInfo = hsaService.getHSAInfo(hsaKey);
                try {
                    IntygDTO dto = JsonDocumentHelper.convertToDTO(utlatande);
                    widelineManager.accept(dto, hsaInfo, count++, "" + count, EventType.CREATED);
                    if (count > maxIntyg) {
                        break maxIntyg;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Inserting certificates completed");
    }

    public String exportUtlatanden() {
        List<VgNumber> allVardgivare = warehouse.getAllVardgivare();
        StringBuilder result = new StringBuilder("vg;").append(Fact.HEADING).append('\n');
        for (VgNumber vg : allVardgivare) {
            for (Fact line : warehouse.get(vg.getVgid())) {
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
        return birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + String.format("%1$04d", random.nextInt(EXTENSION_LIMIT));
    }

    public JsonNode permutate(UtlatandeBuilder builder, String patientId, LocalDate start) {
        // CHECKSTYLE:OFF MagicNumber
        LocalDate end = random.nextFloat() < LONG_PERIOD_FRACTION ?
            start.plusDays(random.nextInt(LONG_PERIOD_DAYS) + 7) :
            start.plusDays(random.nextInt(SHORT_PERIOD_DAYS) + 7);
        // CHECKSTYLE:ON MagicNumber

        int vardId = random.nextInt(NUMBER_OF_UNITS);
        HsaIdEnhet vardenhet = new HsaIdEnhet("verksamhet" + vardId);
        HsaIdVardgivare vardgivare = new HsaIdVardgivare("vardgivare" + (vardId % 2));

        String diagnos = random(DIAGNOSER);

        int arbetsformaga = random(ARBETSFORMAGOR);
        return builder.build(patientId, start, end, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    private static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
