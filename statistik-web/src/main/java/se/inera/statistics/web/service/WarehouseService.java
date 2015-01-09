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

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Fact;
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

    public VerksamhetOverviewResponse getOverview(Predicate<Fact> filter, Range range, String vardgivarId) {
        VerksamhetOverviewResponse overview = overviewQuery.getOverview(warehouse.get(vardgivarId), filter, ReportUtil.getPreviousPeriod(range).getFrom(), range.getMonths());

        return new VerksamhetOverviewResponse(overview.getTotalCases(), overview.getCasesPerMonthSexProportionPreviousPeriod(), overview.getCasesPerMonthSexProportionBeforePreviousPeriod(),
                new DiagnosisGroupsConverter().convert(overview.getDiagnosisGroups()), overview.getAgeGroups(), overview.getDegreeOfSickLeaveGroups(), overview.getSickLeaveLengthGroups(),
                overview.getLongSickLeavesTotal(), overview.getLongSickLeavesAlternation());
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(Predicate<Fact> filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(Predicate<Fact> filter, Range range, String vardgivarId) {
        return query.getDiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukskrivningsgradResponse getSjukskrivningsgradPerMonth(Predicate<Fact> filter, Range range, String vardgivarId) {
        return SjukskrivningsgradQuery.getSjukskrivningsgrad(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SjukfallslangdResponse getSjukskrivningslangd(Predicate<Fact> filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getSjuksrivningslangd(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(Predicate<Fact> filter, Range range, String vardgivarId) {
        return SjukskrivningslangdQuery.getLangaSjukfall(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(Predicate<Fact> filter, Range range, String vardgivarId) {
        return AldersgruppQuery.getAldersgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths());
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(Predicate<Fact> filter, Range range, String kapitelId, String vardgivarId) throws RangeNotFoundException {
        return query.getUnderdiagnosgrupper(warehouse.get(vardgivarId), filter, range.getFrom(), range.getMonths(), 1, kapitelId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getJamforDiagnoser(Predicate<Fact> filter, Range range, String vardgivarId, List<String> diagnosis) {
        return query.getJamforDiagnoser(warehouse.get(vardgivarId), filter, range.getFrom(), 1, range.getMonths(), diagnosis);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerEnhet(Predicate<Fact> filter, Map<String, String> idsToNames, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerEnhet(warehouse.get(vardgivarId), filter, idsToNames, range, range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerLakare(Predicate<Fact> filter, Range range, String vardgivarId) {
        return sjukfallQuery.getSjukfallPerLakare(vardgivarId, warehouse.get(vardgivarId), filter, range, range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerDoctorAgeAndGender(Predicate<Fact> filter, Range range, String vardgivarId) {
        return LakaresAlderOchKonQuery.getSjukfallPerLakaresAlderOchKon(warehouse.get(vardgivarId), filter, range, range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getNumberOfCasesPerLakarbefattning(Predicate<Fact> filter, Range range, String vardgivarId) {
        return LakarbefattningQuery.getSjukfall(warehouse.get(vardgivarId), filter, range, range.getMonths(), 1);
    }

}
