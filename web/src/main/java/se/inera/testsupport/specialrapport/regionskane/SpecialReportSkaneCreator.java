/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.specialrapport.regionskane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpecialReportSkaneCreator {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialReportSkaneCreator.class);

    private final Iterator<Aisle> aisles;
    private final SjukfallUtil sjukfallUtil;
    private final Icd10 icd10;

    public SpecialReportSkaneCreator(Iterator<Aisle> aisles, SjukfallUtil sjukfallUtil, Icd10 icd10) {
        this.aisles = aisles;
        this.sjukfallUtil = sjukfallUtil;
        this.icd10 = icd10;
    }

    static List<String> getEnhetStrings(List<String> enhetsIn) {
        final List<String> defaultEnhetStringIds = Arrays.asList("SE162321000255-O11200", "SE162321000255-O11178", "SE162321000255-O11198",
                "SE162321000255-O11183", "SE162321000255-O11847", "SE162321000255-O11634", "SE162321000255-O11110", "SE162321000255-O16153",
                "SE162321000255-O11628", "SE162321000255-O13730", "SE162321000255-O13225", "SE162321000255-O11635", "SE162321000255-O11546",
                "SE162321000255-O13729", "SE162321000255-O11629", "SE162321000255-O11633", "SE162321000255-O11637", "SE162321000255-O11632",
                "SE162321000255-O11639", "SE162321000255-O11638", "SE162321000255-O11843", "SE162321000255-O11116", "SE162321000255-O11844",
                "SE162321000255-O11138", "SE162321000255-O11134", "SE162321000255-O11201", "SE162321000255-O11112", "SE162321000255-O15274",
                "SE162321000255-O18986", "SE162321000255-O22206", "SE162321000255-O23959", "SE162321000255-O19291", "SE162321000255-O15785",
                "SE162321000255-O15390", "SE162321000255-O17675", "SE162321000255-O17963", "SE162321000255-O17951", "SE162321000255-O16009",
                "SE162321000255-O16011", "SE162321000255-O16012", "SE162321000255-O16008", "SE162321000255-O16013", "SE162321000255-O15551",
                "SE162321000255-O16010", "SE162321000255-O17999", "SE162321000255-O15230", "SE162321000255-O17998", "SE162321000255-O24779",
                "SE162321000255-O17997", "SE162321000255-O17972", "SE162321000255-O15931", "SE162321000255-O17980", "SE162321000255-O15697",
                "SE162321000255-O15713", "SE162321000255-O19122", "SE162321000255-O17988", "SE162321000255-O17820", "SE162321000255-O15678",
                "SE162321000255-O17824", "SE162321000255-O16387", "SE162321000255-O17974", "SE162321000255-O19485", "SE162321000255-O17670",
                "SE162321000255-O17302", "SE162321000255-O24450", "SE162321000255-O24686", "SE162321000255-O24218", "SE162321000255-O15925",
                "SE162321000255-O24383", "SE162321000255-O17991", "SE162321000255-O15905", "SE162321000255-O22271", "SE162321000255-O18155",
                "SE162321000255-O17658", "SE162321000255-O21821", "SE162321000255-O19863", "SE162321000255-O24528", "SE162321000255-O24353",
                "SE162321000255-O16156", "SE162321000255-O15902", "SE162321000255-O24388", "SE162321000255-O17304", "SE162321000255-O15380",
                "SE162321000255-O17069", "SE162321000255-O23811", "SE162321000255-O19460", "SE162321000255-O23038", "SE162321000255-O19991",
                "SE162321000255-O19748", "SE162321000255-O24197", "SE162321000255-O15493", "SE162321000255-O24351", "SE162321000255-O22654",
                "SE162321000255-O18281", "SE162321000255-O20578", "SE162321000255-O19486", "SE162321000255-O19484", "SE162321000255-O19039",
                "SE162321000255-O20060", "SE162321000255-O19049", "SE162321000255-O17983", "SE162321000255-O17993", "SE162321000255-O24234",
                "SE162321000255-O17311", "SE162321000255-O21489", "SE162321000255-O24344", "SE162321000255-O23983", "SE162321000255-O15444",
                "SE162321000255-O18586", "SE162321000255-O15232", "SE162321000255-O17990", "SE162321000255-O18546", "SE162321000255-O19911",
                "SE162321000255-O24770", "SE162321000255-O17995", "SE162321000255-O15945", "SE162321000255-O16010", "SE162321000255-O16009",
                "SE162321000255-O15931", "SE162321000255-O17990", "SE162321000255-O11167", "SE162321000255-O11137", "SE162321000255-O11189",
                "SE162321000255-O18012", "SE162321000255-O11142", "SE162321000255-O11165", "SE162321000255-O11173", "SE162321000255-O11182",
                "SE162321000255-O11207", "SE162321000255-O21141", "SE162321000255-O11123", "SE162321000255-O11172", "SE162321000255-O11206",
                "SE162321000255-O11171", "SE162321000255-O11118", "SE162321000255-O11136", "SE162321000255-O11181", "SE162321000255-O11209",
                "SE162321000255-O11124", "SE162321000255-O18014", "SE162321000255-O14182", "SE162321000255-O11144", "SE162321000255-O11145",
                "SE162321000255-O11187", "SE162321000255-O11144", "SE162321000255-O11119", "SE162321000255-O11192", "SE162321000255-O11139",
                "SE162321000255-O11186", "SE162321000255-O11164", "SE162321000255-O16446", "SE162321000255-O11151", "SE162321000255-O11164",
                "SE162321000255-O11128", "SE162321000255-O11132", "SE162321000255-O11148", "SE162321000255-O11097", "SE162321000255-O11196",
                "SE162321000255-O11101", "SE162321000255-O11097", "SE162321000255-O11111", "SE162321000255-O11210", "SE162321000255-O11106",
                "SE162321000255-O16291", "SE162321000255-O11188", "SE162321000255-O11129", "SE162321000255-O11100", "SE162321000255-O11105",
                "SE162321000255-O11143", "SE162321000255-O11098", "SE162321000255-O16289", "SE162321000255-O11156", "SE162321000255-O16287",
                "SE162321000255-O11180", "SE162321000255-O11122", "SE162321000255-O11096", "SE162321000255-O11160");
        return enhetsIn == null || enhetsIn.isEmpty() ? defaultEnhetStringIds
                : enhetsIn.stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    public List<SpecialSkaneRow> getReport(boolean includeOngoingSjukfall, List<String> enhetsIn) {
        final List<String> enhetStringIds = getEnhetStrings(enhetsIn);
        final List<HsaIdEnhet> enhets = enhetStringIds.stream().map(HsaIdEnhet::new).collect(Collectors.toList());
        final List<SpecialSkaneComputeRow> rows = getRows(includeOngoingSjukfall, enhetStringIds, enhets);
        final List<SpecialSkaneRow> results = new ArrayList<>();

        // Antal nya sjukfall tot
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE),
                x -> true,
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall "
                        + o.getFormattedDate()));

        // Antal nya sjukfall per kon
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name(),
                x -> true,
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall "
                        + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

        // Antal nya sjukfall tot med M-diagnos
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getDx(),
                x -> x.getKey().endsWith("M"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med M-diagnos "
                        + o.getFormattedDate()));

        // Antal nya sjukfall per kon med M-diagnos
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name() + r.getDx(),
                x -> x.getKey().endsWith("M"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med M-diagnos "
                        + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

        // Antal nya sjukfall tot med F-diagnos
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getDx(),
                x -> x.getKey().endsWith("F"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med F-diagnos "
                        + o.getFormattedDate()));

        // Antal nya sjukfall per kon med F-diagnos
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name() + r.getDx(),
                x -> x.getKey().endsWith("F"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med F-diagnos "
                        + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

        // Antal nya sjukfall tot med ovriga diagnoser
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getDx(),
                x -> !x.getKey().endsWith("F") && !x.getKey().endsWith("M"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med övriga diagnoser "
                        + o.getFormattedDate()));

        // Antal nya sjukfall per kon med ovriga diagnoser
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name() + r.getDx(),
                x -> !x.getKey().endsWith("F") && !x.getKey().endsWith("M"),
                o -> "Antal " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall med övriga diagnoser "
                        + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

        if (includeOngoingSjukfall) {
            //Antal unika individer i pågående sjukfall
            results.addAll(resultadderWithCounter(rows,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE),
                    x -> true,
                    o -> "Antal unika individer i pågående sjukfall " + o.getFormattedDate(),
                    e -> e.getValue().stream().map(SpecialSkaneComputeRow::getPatient).distinct().count()));

            // Antal sjukfall den 25e totalt
            final List<SpecialSkaneComputeRow> rowsTouchingDate25 = rows.stream().filter(specialSkaneComputeRow -> {
                final LocalDate date25 = specialSkaneComputeRow.getRange().withDayOfMonth(25);
                final int day25 = WidelineConverter.toDay(date25);
                return specialSkaneComputeRow.getSjukfall().getStart() <= day25 && specialSkaneComputeRow.getSjukfall().getEnd() >= day25;
            }).collect(Collectors.toList());

            // Antal sjukfall den 25e totalt
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e " + o.getFormattedDate()));

            // Antal sjukfall den 25e per kon
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name(),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e " + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

            // Antal sjukfall den 25e totalt per grad
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e " + o.getFormattedDate()));

            // Antal sjukfall den 25e per kon per grad
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name() + r.getSjukskrivningsgrad(),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e med sjukskrivningsgrad "
                            + o.getSjukskrivningsgrad() + " " + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

            // Antal sjukfall den 25e totalt per längd
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + getLengthGroup(r),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e "
                            + getLengthGroup(o).text + " " + o.getFormattedDate()));

            // Antal sjukfall den 25e per kon per längd
            results.addAll(resultadder(rowsTouchingDate25,
                    r -> r.getEnhet() + r.getRange().format(DateTimeFormatter.ISO_DATE) + r.getKon().name() + getLengthGroup(r),
                    x -> true,
                    o -> "Antal pågående sjukfall per den 25e "
                            + getLengthGroup(o).text + " " + getGenderName(o.getKon()) + " " + o.getFormattedDate()));

        }

        return results;
    }

    private List<SpecialSkaneComputeRow> getRows(boolean includeOngoingSjukfall, List<String> enhetStringIds, List<HsaIdEnhet> enhets) {
        final List<SpecialSkaneComputeRow> rows = new ArrayList<>();

        for (Iterator<Aisle> it = aisles; it.hasNext();) {
            Aisle aisle = it.next();
            final LocalDate fromDate = LocalDate.of(2016, 9, 1);
            final LocalDate toDate = LocalDate.of(2018, 12, 31);
            final int startDay = WidelineConverter.toDay(fromDate);
            final Predicate<Fact> intygFilter = in -> enhetStringIds.contains(in.getVardenhet().getId());
            final Predicate<Sjukfall> sjukfallPredicate = s -> includeOngoingSjukfall || s.getStart() >= startDay;
            final FilterPredicates filter = new FilterPredicates(intygFilter, sjukfallPredicate,
                    "SpecialSkane2019" + includeOngoingSjukfall, true);

            final Range range = new Range(fromDate, toDate);

            final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(range.getFrom(), range.getNumberOfMonths(),
                    1, aisle, filter);

            for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                final Collection<Sjukfall> sjukfalls = sjukfallGroup.getSjukfall();
                final Range currentRange = sjukfallGroup.getRange();
                final LocalDate startDateMonth = currentRange.getFrom();
                final int startDayOfMonth = WidelineConverter.toDay(startDateMonth);
                for (Sjukfall sjukfall : sjukfalls) {
                    if (includeOngoingSjukfall || sjukfall.getStart() >= startDayOfMonth) {
                        final long intygId = sjukfall.getFirstIntygId();
                        final Optional<Fact> fact = StreamSupport.stream(aisle.spliterator(), false)
                                .filter(f -> f.getLakarintyg() == intygId).findAny();
                        if (!fact.isPresent()) {
                            LOG.warn("Fact from sjukfall not found..");
                            continue;
                        }

                        final int kat = sjukfall.getDiagnoskategori();
                        final Icd10.Id icd10FromNumericId = icd10.findIcd10FromNumericId(kat);
                        final String dx = icd10FromNumericId.getId().substring(0, 1);
                        final int sjukskrivningsgrad = sjukfall.getSjukskrivningsgrad();
                        final Collection<HsaIdEnhet> enhetsInSjukfall = sjukfall.getEnhets();
                        final Optional<HsaIdEnhet> enhet = enhetsInSjukfall.stream().filter(enhets::contains).findAny();
                        if (enhet.isPresent()) {
                            final SpecialSkaneComputeRow sosRow = new SpecialSkaneComputeRow(dx, enhet.get(),
                                    startDateMonth, sjukfall.getKon(), sjukskrivningsgrad, sjukfall, fact.get().getPatient());
                            rows.add(sosRow);
                        }
                    }
                }
            }
        }
        return rows;
    }

    static String getGenderName(Kon kon) {
        switch (kon) {
            case MALE: return "män";
            case FEMALE: return "kvinnor";
            default: return "okänt kön";
        }
    }

    enum LengthGroup {
        GRUPP_1TO90("1-90 dagar"),
        GRUPP_91TO180("91-180 dagar"),
        GRUPP_180TO365("180-365 dagar"),
        GRUPP_MORETHAN365("365< dagar");

        private final String text;

        LengthGroup(String text) {
            this.text = text;
        }

    }

    private LengthGroup getLengthGroup(SpecialSkaneComputeRow r) {
        final LocalDate date25 = r.getRange().withDayOfMonth(25);
        final int day25 = WidelineConverter.toDay(date25);
        final int days = day25 - r.getSjukfall().getStart();
        final int day90 = 90;
        final int day180 = 180;
        final int day365 = 365;
        if (days <= day90) {
            return LengthGroup.GRUPP_1TO90;
        } else if (days <= day180) {
            return LengthGroup.GRUPP_91TO180;
        } else if (days <= day365) {
            return LengthGroup.GRUPP_180TO365;
        } else {
            return LengthGroup.GRUPP_MORETHAN365;
        }
    }

    private List<SpecialSkaneRow> resultadder(List<SpecialSkaneComputeRow> rows,
                                              Function<SpecialSkaneComputeRow, String> groupByFunction,
                                              Predicate<Map.Entry<String, List<SpecialSkaneComputeRow>>> streamFilter,
                                              Function<SpecialSkaneComputeRow, String> rowName) {
        return resultadderWithCounter(rows, groupByFunction, streamFilter, rowName, entry -> entry.getValue().size());
    }

    private List<SpecialSkaneRow> resultadderWithCounter(List<SpecialSkaneComputeRow> rows,
                                              Function<SpecialSkaneComputeRow, String> groupByFunction,
                                              Predicate<Map.Entry<String,  List<SpecialSkaneComputeRow>>> streamFilter,
                                              Function<SpecialSkaneComputeRow, String> rowName,
                                              Function<Map.Entry<String, List<SpecialSkaneComputeRow>>, Number> counter) {
        final List<SpecialSkaneRow> results = new ArrayList<>();
        final Map<String, List<SpecialSkaneComputeRow>> rowsGroupedByFunc = rows.stream().collect(Collectors.groupingBy(groupByFunction));
        final Set<Map.Entry<String, List<SpecialSkaneComputeRow>>> entries = rowsGroupedByFunc.entrySet().stream()
                .filter(streamFilter).collect(Collectors.toSet());
        for (Map.Entry<String, List<SpecialSkaneComputeRow>> entry : entries) {
            final SpecialSkaneComputeRow firstRow = entry.getValue().get(0);
            results.add(new SpecialSkaneRow(rowName.apply(firstRow), firstRow.getEnhet().getId(), counter.apply(entry)));
        }
        return results;
    }

}
