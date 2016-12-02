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
package se.inera.statistics.web.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SimpleKonResponses;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.CutoffUsage;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;
import se.inera.statistics.service.warehouse.query.LakaresAlderOchKonQuery;
import se.inera.statistics.service.warehouse.query.MessagesQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

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

    public VerksamhetOverviewResponse getOverview(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        VerksamhetOverviewResponse overview = overviewQuery.getOverview(warehouse.get(vardgivarId), filter, ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());

        List<OverviewChartRowExtended> diagnosisGroups = new DiagnosisGroupsConverter().convert(overview.getDiagnosisGroups());
        List<OverviewChartRowExtended> ageGroups = new AldersGroupsConverter().convert(overview.getAgeGroups());

        return new VerksamhetOverviewResponse(overview.getTotalCases(), overview.getCasesPerMonthSexProportionPreviousPeriod(), overview.getCasesPerMonthSexProportionBeforePreviousPeriod(),
                diagnosisGroups, ageGroups, overview.getDegreeOfSickLeaveGroups(), overview.getSickLeaveLengthGroups(),
                overview.getLongSickLeavesTotal(), overview.getLongSickLeavesAlternation());
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, false);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonthTvarsnitt(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), false);
    }

    public SimpleKonResponse<SimpleKonDataRow> getIntygPerMonth(HsaIdVardgivare vardgivarId, FilterSettings filterSettings) {
        return intygCommonManager.getIntyg(vardgivarId, filterSettings.getRange(), filterSettings.getFilter().getEnheter());
    }

    public SimpleKonResponse<SimpleKonDataRow> getMessagesPerMonth(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessages(vardgivarId, filter.getEnheter(), range.getFrom(), range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getMessagesPerMonthTvarsnitt(Filter filter, Range range, HsaIdVardgivare vardgivarId) {
        return messagesQuery.getMessagesTvarsnitt(vardgivarId, filter.getEnheter(), range.getFrom(), range.getMonths());
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getDiagnosgrupperTvarsnitt(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return query.getDiagnosgrupperTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public KonDataResponse getSjukskrivningsgradPerMonth(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukskrivningsgradTvarsnitt(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgradTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukskrivningslangd(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public KonDataResponse getSjukskrivningslangdTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangdomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarTvarsnitt(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public KonDataResponse getAldersgrupperSomTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return AldersgruppQuery.getAldersgrupperSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(FilterPredicates filter, Range range, String kapitelId, HsaIdVardgivare vardgivarId) throws RangeNotFoundException {
        return query.getUnderdiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, kapitelId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getUnderdiagnosgrupperTvarsnitt(FilterPredicates filter, Range range, String kapitelId, HsaIdVardgivare vardgivarId) throws RangeNotFoundException {
        return query.getUnderdiagnosgrupperTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), kapitelId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getJamforDiagnoser(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId, List<String> diagnosis) {
        return query.getJamforDiagnoser(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), diagnosis);
    }

    public KonDataResponse getJamforDiagnoserTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId, List<String> diagnosis) {
        return query.getJamforDiagnoserTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, diagnosis);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhet(FilterPredicates filter, Map<HsaIdEnhet, String> idsToNames, Range range, HsaIdVardgivare vardgivarId) {
        final SimpleKonResponse<SimpleKonDataRow> sjukfallPerEnhet = sjukfallQuery.getSjukfallPerEnhet(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), idsToNames, CutoffUsage.DO_NOT_APPLY_CUTOFF);
        return SimpleKonResponses.addExtrasToNameDuplicates(sjukfallPerEnhet);
    }

    public KonDataResponse getCasesPerEnhetTimeSeries(FilterPredicates filter, Map<HsaIdEnhet, String> idsToNames, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhetSeries(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, idsToNames);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerLakare(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakare(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public KonDataResponse getCasesPerLakareSomTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakareSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerDoctorAgeAndGender(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return new LakaresAlderOchKonQuery(sjukfallUtil).getSjukfallPerLakaresAlderOchKon(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public KonDataResponse getCasesPerDoctorAgeAndGenderTimeSeries(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return new LakaresAlderOchKonQuery(sjukfallUtil).getSjukfallPerLakaresAlderOchKonSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getNumberOfCasesPerLakarbefattning(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return LakarbefattningQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public KonDataResponse getNumberOfCasesPerLakarbefattningSomTidsserie(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return LakarbefattningQuery.getSjukfallSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonthLandsting(final FilterSettings filterSettings) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        Collection<SimpleKonResponse<SimpleKonDataRow>> results = Collections2.transform(enhetsPerVgid.keySet(), new Function<HsaIdVardgivare, SimpleKonResponse<SimpleKonDataRow>>() {
            @Override
            public SimpleKonResponse<SimpleKonDataRow> apply(HsaIdVardgivare vgid) {
                final Aisle aisle = warehouse.get(vgid);
                return sjukfallQuery.getSjukfall(aisle, filterSettings.getFilter().getPredicate(), range.getFrom(), range.getMonths(), 1, true);
            }
        });
        return SimpleKonResponse.merge(results, true);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhetLandsting(final FilterSettings filterSettings) {
        return getCasesPerEnhetLandsting(filterSettings, CutoffUsage.APPLY_CUTOFF_PER_SEX);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerPatientsPerEnhetLandsting(final FilterSettings filterSettings) {
        return getCasesPerEnhetLandsting(filterSettings, CutoffUsage.APPLY_CUTOFF_ON_TOTAL);
    }

    private SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhetLandsting(final FilterSettings filterSettings, final CutoffUsage cutoffUsage) {
        Map<HsaIdVardgivare, Collection<Enhet>> enhetsPerVgid = mapEnhetsToVgids(filterSettings.getFilter().getEnheter());
        final Range range = filterSettings.getRange();
        Collection<SimpleKonResponse<SimpleKonDataRow>> results = Collections2.transform(enhetsPerVgid.entrySet(), new Function<Map.Entry<HsaIdVardgivare, Collection<Enhet>>, SimpleKonResponse<SimpleKonDataRow>>() {
            @Override
            public SimpleKonResponse<SimpleKonDataRow> apply(Map.Entry<HsaIdVardgivare, Collection<Enhet>> entry) {
                final HashMap<HsaIdEnhet, String> idsToNames = Maps.newHashMapWithExpectedSize(entry.getValue().size());
                for (Enhet enhet : entry.getValue()) {
                    idsToNames.put(enhet.getEnhetId(), enhet.getNamn());
                }
                final Aisle aisle = warehouse.get(entry.getKey());
                return sjukfallQuery.getSjukfallPerEnhet(aisle, filterSettings.getFilter().getPredicate(), range.getFrom(), 1, range.getMonths(), idsToNames, cutoffUsage);
            }
        });
        final SimpleKonResponse<SimpleKonDataRow> merged = SimpleKonResponse.merge(results, false);
        return SimpleKonResponses.addExtrasToNameDuplicates(merged);
    }

    private Map<HsaIdVardgivare, Collection<Enhet>> mapEnhetsToVgids(Collection<HsaIdEnhet> enheter) {
        final Multimap<HsaIdVardgivare, Enhet> map = HashMultimap.create();
        final List<Enhet> enhets = enhetManager.getEnhets(enheter);
        for (Enhet enhet : enhets) {
            map.put(enhet.getVardgivareId(), enhet);
        }
        return map.asMap();
    }

    public KonDataResponse getDifferentieratIntygande(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getEnklaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getDifferentieratIntygandeTvarsnitt(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukskrivningslangdQuery.getEnklaSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public List<Sjukfall> getSjukfallForBi(FilterPredicates filter, Range range, HsaIdVardgivare vardgivarId) {
        return SjukfallQuery.getSjukfallForBi(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

}
