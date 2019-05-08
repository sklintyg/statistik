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

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.countypopulation.CountyPopulation;
import se.inera.statistics.service.countypopulation.CountyPopulationManager;
import se.inera.statistics.service.report.model.*;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.NationellDataInfo;
import se.inera.statistics.service.warehouse.NationellDataInvoker;
import se.inera.statistics.service.warehouse.NationellOverviewData;
import se.inera.statistics.web.model.*;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.service.responseconverter.*;

/**
 * Invokes calculation of national statistics and converts the results into the correct format for the reports.
 */
@Component
public class NationellDataCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(NationellDataCalculator.class);

    @Autowired
    private NationellOverviewData overviewData;

    @Autowired
    private CountyPopulationManager countyPopulationManager;

    @Autowired
    private Clock clock;

    @Autowired
    private NationellDataInvoker nationellDataInvoker;

    public NationellDataResult getData() {
        LOG.info("National data calculation: Start");
        final NationellDataInfo data = nationellDataInvoker.getAll();
        final NationellDataResult result = new NationellDataResult();
        result.setNumberOfCasesPerMonth(buildNumberOfCasesPerMonth(data));
        result.setDiagnosgrupper(buildDiagnosgrupper(data));
        result.setDiagnoskapitel(buildDiagnoskapitel(data));
        result.setAldersgrupper(buildAldersgrupper(data));
        result.setSjukskrivningsgrad(buildSjukskrivningsgrad(data));
        result.setSjukfallslangd(buildSjukfallslangd(data));
        result.setSjukfallPerLan(buildSjukfallPerLan(data));
        result.setKonsfordelningPerLan(buildKonsfordelningPerLan(data));
        result.setOverview(buildOverview(data));
        result.setMeddelandenPerAmne(buildNumberOfMeddelandenPerAmne(data));
        result.setIntygPerTyp(buildIntygPerTyp(data));
        result.setCertificatePerCase(buildCertificatePerCase(data));
        result.setAndelKompletteringar(buildAndelKompletteringar(data));
        result.setKompletteringarPerFraga(buildKompletteringarPerFraga(data));
        LOG.info("National data calculation: Done");
        return result;
    }

    private SimpleDetailsData buildNumberOfCasesPerMonth(NationellDataInfo data) {
        SimpleKonResponse casesPerMonth = data.getAntalIntygResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getAntalIntygRange());
        return new PeriodConverter().convert(casesPerMonth, filterSettings);
    }

    private DualSexStatisticsData buildDiagnosgrupper(NationellDataInfo data) {
        DiagnosgruppResponse diagnosisGroups = data.getDiagnosgrupperResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getDiagnosgrupperRange());
        return new DiagnosisGroupsConverter().convert(diagnosisGroups, filterSettings);
    }

    private Map<String, DiagnosisSubGroupStatisticsData> buildDiagnoskapitel(NationellDataInfo data) {
        Map<String, DiagnosisSubGroupStatisticsData> diagnoskapitels = new HashMap<>();
        final Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnittResult = data.getDiagnosavsnittResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getDiagnosavsnittRange());
        for (Map.Entry<Icd10.Kapitel, DiagnosgruppResponse> entry : diagnosavsnittResult.entrySet()) {
            Icd10.Kapitel kapitel = entry.getKey();
            DiagnosgruppResponse diagnosisGroup = entry.getValue();
            final DualSexStatisticsData dssd = new DiagnosisSubGroupsConverter().convert(diagnosisGroup, filterSettings);
            diagnoskapitels.put(kapitel.getId(), new DiagnosisSubGroupStatisticsData(dssd, kapitel));
        }
        return diagnoskapitels;
    }

    private OverviewData buildOverview(NationellDataInfo data) {
        Range range = Range.quarter(clock);
        OverviewResponse response = overviewData.getOverview(data);
        return new OverviewConverter().convert(response, range);
    }

    private SimpleDetailsData buildAldersgrupper(NationellDataInfo data) {
        SimpleKonResponse ageGroups = data.getAldersgrupperResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getAldersgrupperRange());
        return SimpleDualSexConverter.newGenericTvarsnitt().convert(ageGroups, filterSettings);
    }

    private DualSexStatisticsData buildSjukskrivningsgrad(NationellDataInfo data) {
        KonDataResponse degreeOfSickLeaveStatistics = data.getSjukskrivningsgradResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getSjukskrivningsgradRange());
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, filterSettings);
    }

    private SimpleDetailsData buildSjukfallslangd(NationellDataInfo data) {
        SimpleKonResponse sickLeaveLength = data.getSjukfallslangdResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getSjukfallslangdRange());
        return SimpleDualSexConverter.newGenericTvarsnitt().convert(sickLeaveLength, filterSettings);
    }

    private CasesPerCountyData buildSjukfallPerLan(NationellDataInfo data) {
        SimpleKonResponse calculatedSjukfallPerLan = data.getLanResult();
        final Range range = data.getLanRange();
        final CountyPopulation countyPopulation = countyPopulationManager.getCountyPopulation(range);
        return new CasesPerCountyConverter(calculatedSjukfallPerLan, countyPopulation, range).convert();
    }

    private SimpleDetailsData buildKonsfordelningPerLan(NationellDataInfo data) {
        SimpleKonResponse casesPerMonth = data.getLanResult();
        return new SjukfallPerSexConverter().convert(casesPerMonth, data.getLanRange());
    }

    private DualSexStatisticsData buildNumberOfMeddelandenPerAmne(NationellDataInfo data) {
        KonDataResponse casesPerMonth = data.getMeddelandenPerAmneResult();
        final Range range = data.getMeddelandenPerAmneRange();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        return new MessageAmneConverter().convert(casesPerMonth, filterSettings);
    }

    private TableDataReport buildIntygPerTyp(NationellDataInfo data) {
        final KonDataResponse intygPerTyp = data.getIntygPerTypResult();
        final Range range = data.getIntygPerTypeRange();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        return new SimpleMultiDualSexConverter("Antal intyg totalt").convert(intygPerTyp, filterSettings);
    }

    private SimpleDetailsData buildCertificatePerCase(NationellDataInfo data) {
        SimpleKonResponse certificatePerCase = data.getCertificatePerCaseResult();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), data.getCertificatePerCaseRange());
        return SimpleDualSexConverter.newGenericTvarsnitt().convert(certificatePerCase, filterSettings);
    }

    private TableDataReport buildAndelKompletteringar(NationellDataInfo data) {
        final KonDataResponse andelKompletteringar = data.getAndelKompletteringarResult();
        final Range range = data.getAndelKompletteringarRange();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        return new AndelKompletteringarConverter().convert(andelKompletteringar, filterSettings);
    }

    private TableDataReport buildKompletteringarPerFraga(NationellDataInfo data) {
        final SimpleKonResponse kompletteringar = data.getKompletteringarPerFragaResult();
        final Range range = data.getKompletteringarPerFragaRange();
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        return SimpleDualSexConverter.newGenericKompletteringarTvarsnitt().convert(kompletteringar, filterSettings);
    }

}
