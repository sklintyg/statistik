/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;
import se.inera.statistics.service.warehouse.query.LakaresAlderOchKonQuery;
import se.inera.statistics.service.warehouse.query.OverviewQuery;
import se.inera.statistics.service.warehouse.query.RangeNotFoundException;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.List;
import java.util.Map;

public class WarehouseService {

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private OverviewQuery overviewQuery;

    @Autowired
    private SjukfallQuery sjukfallQuery;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    public VerksamhetOverviewResponse getOverview(SjukfallFilter filter, Range range, String vardgivarId) {
        VerksamhetOverviewResponse overview = overviewQuery.getOverview(warehouse.get(vardgivarId), filter, ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());

        return new VerksamhetOverviewResponse(overview.getTotalCases(), overview.getCasesPerMonthSexProportionPreviousPeriod(), overview.getCasesPerMonthSexProportionBeforePreviousPeriod(),
                new DiagnosisGroupsConverter().convert(overview.getDiagnosisGroups()), overview.getAgeGroups(), overview.getDegreeOfSickLeaveGroups(), overview.getSickLeaveLengthGroups(),
                overview.getLongSickLeavesTotal(), overview.getLongSickLeavesAlternation());
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(SjukfallFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonthTvarsnitt(SjukfallFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(SjukfallFilter filter, Range range, String vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getDiagnosgrupperTvarsnitt(SjukfallFilter filter, Range range, String vardgivarId) {
        return query.getDiagnosgrupperTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public KonDataResponse getSjukskrivningsgradPerMonth(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukskrivningsgradTvarsnitt(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgradTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public SjukfallslangdResponse getSjukskrivningslangd(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public KonDataResponse getSjukskrivningslangdTidsserie(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangdomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarTvarsnitt(SjukfallFilter filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfallTvarsnitt(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(SjukfallFilter filter, Range range, String vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), sjukfallUtil);
    }

    public KonDataResponse getAldersgrupperSomTidsserie(SjukfallFilter filter, Range range, String vardgivarId) {
        return AldersgruppQuery.getAldersgrupperSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(SjukfallFilter filter, Range range, String kapitelId, String vardgivarId) throws RangeNotFoundException {
        return query.getUnderdiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, kapitelId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getJamforDiagnoser(SjukfallFilter filter, Range range, String vardgivarId, List<String> diagnosis) {
        return query.getJamforDiagnoser(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), diagnosis);
    }

    public KonDataResponse getJamforDiagnoserTidsserie(SjukfallFilter filter, Range range, String vardgivarId, List<String> diagnosis) {
        return query.getJamforDiagnoserTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, diagnosis);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhet(SjukfallFilter filter, Map<String, String> idsToNames, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhet(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), idsToNames);
    }

    public KonDataResponse getCasesPerEnhetTimeSeries(SjukfallFilter filter, Map<String, String> idsToNames, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhetSeries(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, idsToNames);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerLakare(SjukfallFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakare(vardgivarId, warehouse.get(vardgivarId), filter.getFilter(), range, 1, range.getMonths());
    }

    public KonDataResponse getCasesPerLakareSomTidsserie(SjukfallFilter filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakareSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerDoctorAgeAndGender(SjukfallFilter filter, Range range, String vardgivarId) {
        return LakaresAlderOchKonQuery.getSjukfallPerLakaresAlderOchKon(warehouse.get(vardgivarId), filter.getFilter(), range, range.getMonths(), 1, sjukfallUtil);
    }

    public KonDataResponse getCasesPerDoctorAgeAndGenderTimeSeries(SjukfallFilter filter, Range range, String vardgivarId) {
        return new LakaresAlderOchKonQuery(sjukfallUtil).getSjukfallPerLakaresAlderOchKonSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getNumberOfCasesPerLakarbefattning(SjukfallFilter filter, Range range, String vardgivarId) {
        return LakarbefattningQuery.getSjukfall(warehouse.get(vardgivarId), filter.getFilter(), range, range.getMonths(), 1, sjukfallUtil);
    }

    public KonDataResponse getNumberOfCasesPerLakarbefattningSomTidsserie(SjukfallFilter filter, Range range, String vardgivarId) {
        return LakarbefattningQuery.getSjukfallSomTidsserie(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, sjukfallUtil);
    }

}
