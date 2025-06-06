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
package se.inera.statistics.service.warehouse;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.MessagesFilter;
import se.inera.statistics.service.warehouse.query.MessagesQuery;

/**
 * Invokes national calculations per aisle and collects the results.
 */
@Component
public class NationellDataInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(NationellDataInvoker.class);
    private static final int DEFAULT_CUTOFF = 5;
    private static final int YEAR = 12;
    private static final int EIGHTEEN_MONTHS = 18;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private MessagesQuery messagesQuery;

    @Autowired
    private IntygCommonManager intygCommonManager;

    @Autowired
    private Lan lans;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private Clock clock;

    private int cutoff = DEFAULT_CUTOFF;

    @Autowired
    public void initProperty(@Value("${reports.nationell.cutoff}") int cutoff) {
        final int minimumCutoffValue = 3;
        if (cutoff < minimumCutoffValue) {
            LOG.warn("National cutoff value is too low. Using minimum value: " + minimumCutoffValue);
            this.cutoff = minimumCutoffValue;
            return;
        }
        this.cutoff = cutoff;
    }

    public int getCutoff() {
        return cutoff;
    }

    public NationellDataInfo getAll() {
        final NationellData nationellData = new NationellData(lans, query, sjukfallUtil, cutoff);
        final NationellDataInfo result = new NationellDataInfo(); //Will contain the final results
        final NationellDataHolder data = new NationellDataHolder(); //Hold data between calculations per vg

        populateRanges(result);
        populateLan(data);

        for (Aisle aisle : warehouse) {
            calculatePerAisle(nationellData, result, data, aisle);
            calculateMeddelandenPerVg(aisle.getVardgivareId(), result);
            calculateIntygPerVg(aisle.getVardgivareId(), result);
        }
        return populateResults(result, data);
    }

    private void calculateMeddelandenPerVg(HsaIdVardgivare vardgivareId, NationellDataInfo result) {
        calculateMsgPerAmne(vardgivareId, result);
        calulateAndelIntyg(vardgivareId, result);
        calulateKompletteringarPerFraga(vardgivareId, result);
    }

    private void calculateMsgPerAmne(HsaIdVardgivare vardgivareId, NationellDataInfo result) {
        final Range range = result.getMeddelandenPerAmneRange();
        final MessagesFilter messagesFilter = new MessagesFilter(vardgivareId, range.getFrom(),
            range.getTo(), null, null, null, null);
        final KonDataResponse meddelandenPerAmne = result.getMeddelandenPerAmneResult();
        final KonDataResponse resp = messagesQuery.getMeddelandenPerAmneAggregated(meddelandenPerAmne, messagesFilter, cutoff);
        result.setMeddelandenPerAmneResult(resp);
    }

    private void calulateAndelIntyg(HsaIdVardgivare vardgivareId, NationellDataInfo result) {
        final Range range = result.getAndelKompletteringarRange();
        final LocalDate from = range.getFrom();
        final LocalDate to = range.getTo();
        final MessagesFilter messagesFilter = new MessagesFilter(vardgivareId, from, to, null, null, null, null);
        final KonDataResponse konDataResponse = result.getAndelKompletteringarResult();
        final KonDataResponse response = messagesQuery.getAndelKompletteringarAggregated(konDataResponse, messagesFilter, cutoff);
        result.setAndelKompletteringarResult(response);
    }

    private void calulateKompletteringarPerFraga(HsaIdVardgivare vardgivareId, NationellDataInfo result) {
        final Range range = result.getKompletteringarPerFragaRange();
        final LocalDate from = range.getFrom();
        final LocalDate to = range.getTo();
        final MessagesFilter messagesFilter = new MessagesFilter(vardgivareId, from, to, null, null, null, null);
        final SimpleKonResponse konDataResponse = result.getKompletteringarPerFragaResult();
        final SimpleKonResponse response = messagesQuery.getKompletteringarPerFragaTvarsnittAggregated(
            konDataResponse, messagesFilter, cutoff);
        result.setKompletteringarPerFragaResult(response);
    }

    private void calculateIntygPerVg(HsaIdVardgivare vardgivareId, NationellDataInfo result) {
        final Range range = result.getIntygPerTypeRange();
        final IntygCommonFilter intygCommonFilter = new IntygCommonFilter(range, null, null, null, null);
        final KonDataResponse konDataResponse = result.getIntygPerTypResult();
        final KonDataResponse response = intygCommonManager.getIntygPerTypeTidsserieAggregated(konDataResponse,
            vardgivareId, intygCommonFilter, cutoff);
        result.setIntygPerTypResult(response);
    }

    private void calculatePerAisle(NationellData nationellData, NationellDataInfo result, NationellDataHolder data, Aisle aisle) {
        final Range antalRange = result.getAntalIntygRange();
        nationellData.updateAntalIntygResult(aisle, antalRange.getFrom(), antalRange.getNumberOfMonths(), 1, data.getAntalIntygResult());

        data.setDiagnosgrupperResult(
            nationellData.getDiagnosgrupper(aisle, result.getDiagnosgrupperRange(), data.getDiagnosgrupperResult()));

        for (Icd10.Kapitel kapitel : icd10.getKapitel(false)) {
            nationellData.getDiagnosavsnitt(aisle, result.getDiagnosavsnittRange(), kapitel, data.getDiagnosavsnittResult());
        }

        data.setAldersgrupperResult(nationellData.getAldersgrupper(aisle, result.getAldersgrupperRange(),
            data.getAldersgrupperResult(), AldersgruppQuery.RANGES));

        data.setSjukskrivningsgradResult(nationellData.getSjukskrivningsgrad(aisle,
            result.getSjukskrivningsgradRange(), data.getSjukskrivningsgradResult()));

        data.setSjukfallslangdResult(nationellData.getSjukfallslangd(aisle, result.getSjukfallslangdRange(),
            data.getSjukfallslangdResult()));

        nationellData.addSjukfallPerLanToResult(result.getLanRange(), data.getLanResult(), aisle);

        data.setIntygPerSjukfallResult(nationellData.getIntygPerSjukfall(aisle, result.getIntygPerSjukfallRange(),
            data.getIntygPerSjukfallResult()));

        final Range overviewRange = result.getOverviewRange();
        final Range previousOverviewRange = ReportUtil.getPreviousOverviewPeriod(overviewRange);
        data.setOverviewForandringCurrentResult(
            nationellData.getAntalIntygOverviewResult(aisle, overviewRange, data.getOverviewForandringCurrentResult()));
        data.setOverviewForandringPreviousResult(
            nationellData.getAntalIntygOverviewResult(aisle, previousOverviewRange, data.getOverviewForandringPreviousResult()));
        data.setOverviewDiagnosgrupperCurrentResult(
            nationellData.getDiagnosgrupperOverview(aisle, overviewRange, data.getOverviewDiagnosgrupperCurrentResult()));
        data.setOverviewDiagnosgrupperPreviousResult(
            nationellData.getDiagnosgrupperOverview(aisle, previousOverviewRange, data.getOverviewDiagnosgrupperPreviousResult()));

        data.setOverviewPreviousAldersgrupperResult(nationellData.getAldersgrupper(aisle, previousOverviewRange,
            data.getOverviewPreviousAldersgrupperResult(), AldersgruppQuery.OVERVIEW_RANGES));
        data.setOverviewCurrentAldersgrupperResult(nationellData.getAldersgrupper(aisle, overviewRange,
            data.getOverviewCurrentAldersgrupperResult(), AldersgruppQuery.OVERVIEW_RANGES));

        data.setOverviewSjukskrivningsgradCurrentResult(nationellData.getSjukskrivningsgradOverview(aisle,
            overviewRange, data.getOverviewSjukskrivningsgradCurrentResult()));
        data.setOverviewSjukskrivningsgradPreviousResult(nationellData.getSjukskrivningsgradOverview(aisle,
            previousOverviewRange, data.getOverviewSjukskrivningsgradPreviousResult()));

        data.setOverviewSjukfallslangdPreviousResult(
            nationellData.getSjukfallslangd(aisle, previousOverviewRange, data.getOverviewSjukfallslangdPreviousResult()));
        data.setOverviewSjukfallslangdCurrentResult(
            nationellData.getSjukfallslangd(aisle, overviewRange, data.getOverviewSjukfallslangdCurrentResult()));

        data.setOverviewLangaSjukfallDiffPreviousResult(
            nationellData.getLangaSjukfall(aisle, previousOverviewRange, data.getOverviewLangaSjukfallDiffPreviousResult()));
        data.setOverviewLangaSjukfallDiffCurrentResult(
            nationellData.getLangaSjukfall(aisle, overviewRange, data.getOverviewLangaSjukfallDiffCurrentResult()));

        nationellData.addSjukfallPerLanToResult(previousOverviewRange, data.getOverviewLanPreviousResult(), aisle);
        nationellData.addSjukfallPerLanToResult(overviewRange, data.getOverviewLanCurrentResult(), aisle);
    }

    private void populateLan(NationellDataHolder data) {
        for (String lanId : lans) {
            data.getLanResult().add(new SimpleKonDataRow(lans.getNamn(lanId), 0, 0, lanId));
        }
        data.setOverviewLanPreviousResult(new ArrayList<>(data.getLanResult()));
        data.setOverviewLanCurrentResult(new ArrayList<>(data.getLanResult()));
    }

    private void populateRanges(NationellDataInfo result) {
        final Range longRange = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS, clock);
        Range yearRange = Range.createForLastMonthsExcludingCurrent(YEAR, clock);
        Range quarterRange = Range.quarter(clock);

        result.setAntalIntygRange(longRange);
        result.setDiagnosgrupperRange(longRange);
        result.setDiagnosavsnittRange(longRange);
        result.setAldersgrupperRange(Range.createForLastMonthsExcludingCurrent(yearRange.getNumberOfMonths(), clock));
        result.setSjukskrivningsgradRange(longRange);
        result.setSjukfallslangdRange(Range.createForLastMonthsExcludingCurrent(yearRange.getNumberOfMonths(), clock));
        result.setLanRange(yearRange);
        result.setOverviewRange(quarterRange);
        result.setMeddelandenPerAmneRange(longRange);
        result.setIntygPerTypeRange(longRange);
        result.setIntygPerSjukfallRange(Range.createForLastMonthsExcludingCurrent(yearRange.getNumberOfMonths(), clock));
        result.setAndelKompletteringarRange(longRange);
        result.setKompletteringarPerFragaRange(yearRange);
    }

    private NationellDataInfo populateResults(NationellDataInfo result, NationellDataHolder data) {

        result.setAntalIntygResult(new SimpleKonResponse(AvailableFilters.getForNationell(), data.getAntalIntygResult()));

        if (data.getDiagnosgrupperResult() == null) {
            data.setDiagnosgrupperResult(
                new DiagnosgruppResponse(AvailableFilters.getForNationell(), new ArrayList<>(), new ArrayList<>()));
        }
        result.setDiagnosgrupperResult(data.getDiagnosgrupperResult());

        for (Icd10.Kapitel kapitel : icd10.getKapitel(false)) {
            if (!data.getDiagnosavsnittResult().containsKey(kapitel)) {
                DiagnosgruppResponse response = new DiagnosgruppResponse(AvailableFilters.getForNationell(),
                    new ArrayList<>(), new ArrayList<>());
                data.getDiagnosavsnittResult().put(kapitel, response);
            }
        }
        result.setDiagnosavsnittResult(data.getDiagnosavsnittResult());

        if (data.getAldersgrupperResult() == null) {
            data.setAldersgrupperResult(new SimpleKonResponse(AvailableFilters.getForNationell(), new ArrayList<>()));
        }
        result.setAldersgrupperResult(data.getAldersgrupperResult());

        if (data.getSjukskrivningsgradResult() == null) {
            data.setSjukskrivningsgradResult(new KonDataResponse(AvailableFilters.getForNationell(), new ArrayList<>(), new ArrayList<>()));
        }
        result.setSjukskrivningsgradResult(data.getSjukskrivningsgradResult());

        if (data.getSjukfallslangdResult() == null) {
            data.setSjukfallslangdResult(new SimpleKonResponse(AvailableFilters.getForNationell(), new ArrayList<>()));
        }
        result.setSjukfallslangdResult(data.getSjukfallslangdResult());

        result.setLanResult(new SimpleKonResponse(AvailableFilters.getForNationell(), data.getLanResult()));

        result.setOverviewGenderResult(new SimpleKonResponse(AvailableFilters.getForNationell(), data.getOverviewAntalIntygResult()));
        result.setOverviewForandringResult(data.getOverviewForandringResult());
        result.setOverviewDiagnosgrupperResult(data.getOverviewDiagnosgrupperResultNullSafe());
        result.setOverviewPreviousAldersgruppResult(data.getOverviewPreviousAldersgrupperResult());
        result.setOverviewCurrentAldersgruppResult(data.getOverviewCurrentAldersgrupperResult());
        result.setOverviewSjukskrivningsgraderPrevious(data.getOverviewSjukskrivningsgradPreviousResult());
        result.setOverviewSjukskrivningsgraderCurrent(data.getOverviewSjukskrivningsgradCurrentResult());
        result.setOverviewSjukskrivningslangdPreviousResult(data.getOverviewSjukfallslangdPreviousResult());
        result.setOverviewSjukskrivningslangdCurrentResult(data.getOverviewSjukfallslangdCurrentResult());
        result.setOverviewLangaSjukfallResult(toLangaSjukfall(data.getOverviewSjukfallslangdCurrentResult()));
        result.setOverviewLangaSjukfallDiffCurrentResult(data.getOverviewLangaSjukfallDiffCurrentResult());
        result.setOverviewLangaSjukfallDiffPreviousResult(data.getOverviewLangaSjukfallDiffPreviousResult());
        result.setOverviewLanPreviousResult(new SimpleKonResponse(AvailableFilters.getForNationell(), data.getOverviewLanPreviousResult()));
        result.setOverviewLanCurrentResult(new SimpleKonResponse(AvailableFilters.getForNationell(), data.getOverviewLanCurrentResult()));

        if (result.getMeddelandenPerAmneResult() == null) {
            result.setMeddelandenPerAmneResult(
                new KonDataResponse(AvailableFilters.getForNationell(), new ArrayList<>(), new ArrayList<>()));
        }

        if (result.getIntygPerTypResult() == null) {
            result.setIntygPerTypResult(new KonDataResponse(AvailableFilters.getForNationell(), new ArrayList<>(), new ArrayList<>()));
        }

        if (data.getIntygPerSjukfallResult() == null) {
            data.setIntygPerSjukfallResult(new SimpleKonResponse(AvailableFilters.getForNationell(), new ArrayList<>()));
        }
        result.setIntygPerSjukfallResult(data.getIntygPerSjukfallResult());

        if (result.getAndelKompletteringarResult() == null) {
            KonDataResponse response = new KonDataResponse(AvailableFilters.getForNationell(), new ArrayList<>(), new ArrayList<>());
            result.setAndelKompletteringarResult(response);
        }

        if (result.getKompletteringarPerFragaResult() == null) {
            SimpleKonResponse response = new SimpleKonResponse(AvailableFilters.getForNationell(), new ArrayList<>());
            result.setKompletteringarPerFragaResult(response);
        }

        return result;
    }

    private Integer toLangaSjukfall(SimpleKonResponse resp) {
        if (resp == null) {
            return 0;
        }
        final List<SimpleKonDataRow> rows = resp.getRows();
        if (rows == null) {
            return 0;
        }
        return rows.stream()
            .filter(Objects::nonNull)
            .filter(r -> SjukfallsLangdGroup.getByName(r.getName())
                .map(SjukfallsLangdGroup::isLongSjukfallInOverview).orElse(false))
            .map(r -> r.getFemale() + r.getMale())
            .reduce(0, (i1, i2) -> i1 + i2);
    }

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

}
