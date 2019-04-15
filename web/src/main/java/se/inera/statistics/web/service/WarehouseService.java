/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SimpleKonResponses;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.IntygCommonFilter;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.CutoffUsage;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;
import se.inera.statistics.service.warehouse.query.LakaresAlderOchKonQuery;
import se.inera.statistics.service.warehouse.query.MessagesFilter;
import se.inera.statistics.service.warehouse.query.MessagesQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;
import se.inera.statistics.web.service.responseconverter.AldersGroupsConverter;
import se.inera.statistics.web.service.responseconverter.DiagnosisGroupsConverter;

public class WarehouseService {

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private OverviewQuery overviewQuery;
    @Autowired
    private MessagesQuery messagesQuery;

    @Autowired
    private SjukfallQuery sjukfallQuery;

    @Autowired
    private IntygCommonManager intygCommonManager;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private EnhetManager enhetManager;

    public VerksamhetOverviewResponse getOverview(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        final MessagesFilter messagesFilterWithoutRange = new MessagesFilter(vardgivarId, null, null, filter.getEnheter(),
                filter.getAldersgrupp(), filter.getDiagnoser(), filter.getIntygstyper());
        VerksamhetOverviewResponse overview = overviewQuery.getOverview(warehouse.get(vardgivarId), filter.getPredicate(),
                range, ReportUtil.getPreviousOverviewPeriod(range), messagesFilterWithoutRange);

        List<OverviewChartRowExtended> diagnosisGroups = new DiagnosisGroupsConverter().convert(overview.getDiagnosisGroups());
        List<OverviewChartRowExtended> ageGroups = new AldersGroupsConverter().convert(overview.getAgeGroups());

        return new VerksamhetOverviewResponse(AvailableFilters.getForSjukfall(), overview.getTotalCases(),
                overview.getCasesPerMonthSexProportionPreviousPeriod(),
                diagnosisGroups, ageGroups, overview.getDegreeOfSickLeaveGroups(), overview.getSickLeaveLengthGroups(),
                overview.getLongSickLeavesTotal(), overview.getLongSickLeavesAlternation(), overview.getKompletteringar());
    }

    public SimpleKonResponse getCasesPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1, false);
    }

    public SimpleKonResponse getCasesPerMonthTvarsnitt(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths(), false);
    }

    public SimpleKonResponse getTotalIntygPerMonth(HsaIdVardgivare vardgivarId, FilterSettings filterSettings) {
        return intygCommonManager.getIntyg(vardgivarId, getIntygCommonFilter(filterSettings));
    }

    public SimpleKonResponse getTotalIntygTvarsnitt(HsaIdVardgivare vardgivarId, FilterSettings filterSettings) {
        return intygCommonManager.getIntygTvarsnitt(vardgivarId, getIntygCommonFilter(filterSettings));
    }

    public KonDataResponse getIntygPerTypePerMonth(HsaIdVardgivare vardgivarId, FilterSettings filterSettings) {
        return intygCommonManager.getIntygPerTypeTidsserie(vardgivarId, getIntygCommonFilter(filterSettings));
    }

    private IntygCommonFilter getIntygCommonFilter(FilterSettings filterSettings) {
        final Filter filter = filterSettings.getFilter();
        return new IntygCommonFilter(filterSettings.getRange(), filter.getEnheter(),
                filter.getDiagnoser(), filter.getAldersgrupp(), filter.getIntygstyper());
    }

    public SimpleKonResponse getIntygPerTypeTvarsnitt(HsaIdVardgivare vardgivarId, FilterSettings filterSettings) {
        return intygCommonManager.getIntygPerTypeTvarsnitt(vardgivarId, getIntygCommonFilter(filterSettings));
    }

    public SimpleKonResponse getMessagesPerMonth(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessages(vardgivarId, filter.getEnheter(), range.getFrom(), range.getNumberOfMonths());
    }

    public SimpleKonResponse getMessagesPerMonthTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesTvarsnitt(vardgivarId, filter.getEnheter(), range.getFrom(), range.getNumberOfMonths());
    }

    public KonDataResponse getMessagesPerAmneRegion(final FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        return enhetsPerVgid.entrySet().stream().reduce(null, (konDataResponse, entry) -> {
            final MessagesFilter meddelandeFilter = getMeddelandeFilter(entry.getKey(), filterSettings.getFilter(), range);
            return messagesQuery.getMeddelandenPerAmneAggregated(konDataResponse, meddelandeFilter, sjukfallQuery.getCutoff());
        }, (konDataResponse, konDataResponse2) -> konDataResponse2);
    }

    public KonDataResponse getMessagesPerAmne(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesPerAmne(getMeddelandeFilter(vardgivarId, filter, range));
    }

    public SimpleKonResponse getMessagesPerAmneTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesTvarsnittPerAmne(getMeddelandeFilter(vardgivarId, filter, range));
    }

    public KonDataResponse getAndelKompletteringar(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getAndelKompletteringar(getMeddelandeFilter(vardgivarId, filter, range), 0);
    }

    public KonDataResponse getAndelKompletteringarRegion(FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        return enhetsPerVgid.entrySet().stream().reduce(null, (konDataResponse, entry) -> {
            final MessagesFilter meddelandeFilter = getMeddelandeFilter(entry.getKey(), filterSettings.getFilter(), range);
            return messagesQuery.getAndelKompletteringarAggregated(konDataResponse, meddelandeFilter, sjukfallQuery.getCutoff());
        }, (konDataResponse, konDataResponse2) -> konDataResponse2);
    }

    public SimpleKonResponse getAndelKompletteringarTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getAndelKompletteringarTvarsnitt(getMeddelandeFilter(vardgivarId, filter, range));
    }

    public KonDataResponse getKompletteringarPerFraga(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getKompletteringarPerFraga(getMeddelandeFilter(vardgivarId, filter, range), 0);
    }

    public SimpleKonResponse getKompletteringarPerFragaLandsting(FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        return enhetsPerVgid.entrySet().stream().reduce(null, (konDataResponse, entry) -> {
            final MessagesFilter meddelandeFilter = getMeddelandeFilter(entry.getKey(), filterSettings.getFilter(), range);
            final int cutoff = sjukfallQuery.getCutoff();
            return messagesQuery.getKompletteringarPerFragaTvarsnittAggregated(konDataResponse, meddelandeFilter, cutoff);
        }, (konDataResponse, konDataResponse2) -> konDataResponse2);
    }

    public SimpleKonResponse getKompletteringarPerFragaTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getKompletteringarPerFragaTvarsnitt(getMeddelandeFilter(vardgivarId, filter, range), 0);
    }

    public KonDataResponse getMessagesPerAmnePerEnhet(Filter filter, Range range, HsaIdVardgivare vardgivarId,
                                                      Map<HsaIdEnhet, String> idToNameMap) {
        return messagesQuery.getMessagesPerAmnePerEnhet(getMeddelandeFilter(vardgivarId, filter, range), idToNameMap);
    }

    public KonDataResponse getMessagesPerAmnePerLakare(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesPerAmnePerLakare(getMeddelandeFilter(vardgivarId, filter, range));
    }

    private MessagesFilter getMeddelandeFilter(HsaIdVardgivare vardgivarId, Filter filter, Range range) {
        return new MessagesFilter(vardgivarId, range.getFrom(), range.getTo(), filter.getEnheter(),
                filter.getAldersgrupp(), filter.getDiagnoser(), filter.getIntygstyper());
    }

    public KonDataResponse getMessagesPerAmnePerLakareTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesTvarsnittPerAmnePerLakare(getMeddelandeFilter(vardgivarId, filter, range));
    }

    public KonDataResponse getMessagesPerAmnePerEnhetTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId,
                                                               Map<HsaIdEnhet, String> idToNameMap) {
        return messagesQuery.getMessagesTvarsnittPerAmnePerEnhet(getMeddelandeFilter(vardgivarId, filter, range), idToNameMap);
    }

    public KonDataResponse getMessagesPerAmnePerEnhetRegion(FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        return enhetsPerVgid.entrySet().stream().reduce(null, (konDataResponse, entry) -> {
            final Map<HsaIdEnhet, String> idsToNames = entry.getValue().stream()
                    .collect(Collectors.toMap(Enhet::getEnhetId, Enhet::getNamn));
            final MessagesFilter meddelandeFilter = getMeddelandeFilter(entry.getKey(), filterSettings.getFilter(), range);
            final int cutoff = sjukfallQuery.getCutoff();
            return messagesQuery.getMeddelandenPerAmneOchEnhetAggregated(konDataResponse, meddelandeFilter, cutoff, idsToNames);
        }, (konDataResponse, konDataResponse2) -> konDataResponse2);
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1);
    }

    public SimpleKonResponse getDiagnosgrupperTvarsnitt(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return query.getDiagnosgrupperTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths());
    }

    public KonDataResponse getSjukskrivningsgradPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(),
                1, sjukfallUtil);
    }

    public SimpleKonResponse getSjukskrivningsgradTvarsnitt(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgradTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1,
                range.getNumberOfMonths(), sjukfallUtil);
    }

    public SimpleKonResponse getSjukskrivningslangd(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1,
                range.getNumberOfMonths(), sjukfallUtil);
    }

    public KonDataResponse getSjukskrivningslangdTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangdomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(),
                range.getNumberOfMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse getLangaSjukskrivningarPerManad(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1,
                sjukfallUtil);
    }

    public SimpleKonResponse getLangaSjukskrivningarTvarsnitt(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1,
                range.getNumberOfMonths(), sjukfallUtil);
    }

    public SimpleKonResponse getAldersgrupper(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths(),
                sjukfallUtil, AldersgruppQuery.RANGES);
    }

    public KonDataResponse getAldersgrupperSomTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return AldersgruppQuery.getAldersgrupperSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(),
                1, sjukfallUtil);
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(FilterPredicates filter, Range range, String kapitelId, HsaIdVardgivare vardgivarId)
            throws RangeNotFoundException {
        return query.getUnderdiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1, kapitelId);
    }

    public SimpleKonResponse getUnderdiagnosgrupperTvarsnitt(FilterPredicates filter, Range range, String kapitelId,
            HsaIdVardgivare vardgivarId) throws RangeNotFoundException {
        return query.getUnderdiagnosgrupperTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths(),
                kapitelId);
    }

    public SimpleKonResponse getJamforDiagnoser(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId,
            List<String> diagnosis) {
        return query.getJamforDiagnoser(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths(), diagnosis);
    }

    public KonDataResponse getJamforDiagnoserTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId,
            List<String> diagnosis) {
        return query.getJamforDiagnoserTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1,
                diagnosis);
    }

    public SimpleKonResponse getCasesPerEnhet(FilterPredicates filter, Map<HsaIdEnhet, String> idsToNames, Range range,
            HsaIdVardgivare vardgivarId) {
        final SimpleKonResponse sjukfallPerEnhet = sjukfallQuery.getSjukfallPerEnhet(warehouse.get(vardgivarId), filter,
                range.getFrom(), 1, range.getNumberOfMonths(), idsToNames, CutoffUsage.DO_NOT_APPLY_CUTOFF);
        return SimpleKonResponses.addExtrasToNameDuplicates(sjukfallPerEnhet);
    }

    public KonDataResponse getCasesPerEnhetTimeSeries(FilterPredicates filter, Map<HsaIdEnhet, String> idsToNames, Range range,
            HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhetSeries(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(), 1,
                idsToNames);
    }

    public SimpleKonResponse getCasesPerLakare(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakare(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths());
    }

    public KonDataResponse getCasesPerLakareSomTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakareSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(),
                range.getNumberOfMonths(), 1);
    }

    public SimpleKonResponse getCasesPerDoctorAgeAndGender(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return new LakaresAlderOchKonQuery(sjukfallUtil).getSjukfallPerLakaresAlderOchKon(warehouse.get(vardgivarId), filter,
                range.getFrom(), 1, range.getNumberOfMonths());
    }

    public KonDataResponse getCasesPerDoctorAgeAndGenderTimeSeries(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return new LakaresAlderOchKonQuery(sjukfallUtil).getSjukfallPerLakaresAlderOchKonSomTidsserie(warehouse.get(vardgivarId), filter,
                range.getFrom(), range.getNumberOfMonths(), 1);
    }

    public SimpleKonResponse getNumberOfCasesPerLakarbefattning(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return LakarbefattningQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getNumberOfMonths(),
                sjukfallUtil);
    }

    public KonDataResponse getNumberOfCasesPerLakarbefattningSomTidsserie(FilterPredicates filter, Range range,
            HsaIdVardgivare vardgivarId) {
        return LakarbefattningQuery.getSjukfallSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getNumberOfMonths(),
                1, sjukfallUtil);
    }

    public SimpleKonResponse getCasesPerMonthRegion(final FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        Collection<SimpleKonResponse> results = Collections2.transform(enhetsPerVgid.keySet(),
                vgid -> {
                    final Aisle aisle = warehouse.get(vgid);
                    return sjukfallQuery.getSjukfall(aisle, filterSettings.getFilter().getPredicate(), range.getFrom(),
                            range.getNumberOfMonths(), 1, true);
                });
        return SimpleKonResponse.merge(results, true, AvailableFilters.getForSjukfall());
    }

    public SimpleKonResponse getCasesPerEnhetRegion(final FilterSettings filterSettings) {
        return getCasesPerEnhetRegion(filterSettings, CutoffUsage.APPLY_CUTOFF_PER_SEX);
    }

    public SimpleKonResponse getCasesPerPatientsPerEnhetRegion(final FilterSettings filterSettings) {
        return getCasesPerEnhetRegion(filterSettings, CutoffUsage.APPLY_CUTOFF_ON_TOTAL);
    }

    private SimpleKonResponse getCasesPerEnhetRegion(final FilterSettings filterSettings,
                                                     final CutoffUsage cutoffUsage) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        Collection<SimpleKonResponse> results = Collections2.transform(enhetsPerVgid.entrySet(),
                entry -> {
                    final HashMap<HsaIdEnhet, String> idsToNames = Maps.newHashMapWithExpectedSize(entry.getValue().size());
                    for (Enhet enhet : entry.getValue()) {
                        idsToNames.put(enhet.getEnhetId(), enhet.getNamn());
                    }
                    final Aisle aisle = warehouse.get(entry.getKey());
                    return sjukfallQuery.getSjukfallPerEnhet(aisle, filterSettings.getFilter().getPredicate(), range.getFrom(), 1,
                            range.getNumberOfMonths(), idsToNames, cutoffUsage);
                });
        final SimpleKonResponse merged = SimpleKonResponse.merge(results, false, AvailableFilters.getForSjukfall());
        return SimpleKonResponses.addExtrasToNameDuplicates(merged);
    }

    public KonDataResponse getIntygPerTypeRegion(final FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final IntygCommonFilter intygCommonFilter = getIntygCommonFilter(filterSettings);
        return enhetsPerVgid.entrySet().stream().reduce(null, (konDataResponse, entry) -> {
            return intygCommonManager.getIntygPerTypeTidsserieAggregated(konDataResponse, entry.getKey(), intygCommonFilter,
                    sjukfallQuery.getCutoff());
        }, (konDataResponse, konDataResponse2) -> konDataResponse2);
    }


    private Map<HsaIdVardgivare, Collection<Enhet>> mapEnhetsToVgids(Collection<HsaIdEnhet> enheter) {
        final Multimap<HsaIdVardgivare, Enhet> map = HashMultimap.create();
        final List<Enhet> enhets = enhetManager.getEnhets(enheter);
        for (Enhet enhet : enhets) {
            map.put(enhet.getVardgivareId(), enhet);
        }
        return map.asMap();
    }

}
