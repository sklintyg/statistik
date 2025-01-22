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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.countypopulation.CountyPopulationManager;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.OverviewAgeGroup;
import se.inera.statistics.service.report.util.SickLeaveDegree;

/**
 * Contains calculations for each overview report on national statistics.
 */
@Component
public class NationellOverviewData {

    private static final int MAX_LAN = 5;
    private static final int PERCENT = 100;

    @Autowired
    private CountyPopulationManager countyPopulationManager;

    public OverviewResponse getOverview(NationellDataInfo data) {
        OverviewKonsfordelning sexProportion = getSexProportion(data);
        int intygForandring = getForandring(data);
        List<OverviewChartRowExtended> diagnosgrupper = getDiagnosgrupper(data);
        List<OverviewChartRowExtended> aldersgrupper = getAldersgrupper(data);
        List<OverviewChartRowExtended> sjukskrivningsgrader = getSjukskrivningsgrader(data);
        List<OverviewChartRow> sjukskrivningslangdgrupper = getSjukskrivningslangdgrupper(data);
        int langaSjukskrivningar = getLangaSjukskrivningar(data);
        int forandringLangaSjukskrivningar = getForandringLangaSjukskrivningar(data);
        List<OverviewChartRowExtended> intygPerLan = getIntygPerLan(data);
        return new OverviewResponse(sexProportion, intygForandring, diagnosgrupper, aldersgrupper, sjukskrivningsgrader,
            sjukskrivningslangdgrupper, langaSjukskrivningar, forandringLangaSjukskrivningar, intygPerLan);
    }

    private int getForandring(NationellDataInfo data) {
        SimpleKonResponse intyg = data.getOverviewForandringResult();

        if (intyg.getRows().size() >= 2) {
            SimpleKonDataRow previous = intyg.getRows().get(0);
            SimpleKonDataRow current = intyg.getRows().get(1);
            return percentChange(total(current), total(previous));
        } else {
            return percentChange(0, 0);
        }
    }

    private List<OverviewChartRowExtended> getIntygPerLan(NationellDataInfo data) {
        final var range = data.getOverviewRange();
        final var previousData = perPopulation(data.getOverviewLanPreviousResult(), range);
        final var currentData = perPopulation(data.getOverviewLanCurrentResult(), range);
        final var include = getTop(MAX_LAN, currentData);
        return getResult(include, previousData, currentData);
    }

    private List<OverviewChartRow> perPopulation(SimpleKonResponse overviewLanPreviousResult, Range range) {
        final var countyPopulation = countyPopulationManager.getCountyPopulation(range);
        final var populationPerCountyCode = countyPopulation.getPopulationPerCountyCode();
        return overviewLanPreviousResult.getRows().stream().map(simpleKonDataRow -> {
            final var data = simpleKonDataRow.getData();
            final var county = simpleKonDataRow.getExtras();
            final var population = populationPerCountyCode.get(county);
            final var total = data.getFemale() + data.getMale();
            final var totalPerPopulation = getPerPopulation(total, population);
            return new OverviewChartRow(simpleKonDataRow.getName(), totalPerPopulation);
        }).collect(Collectors.toList());
    }

    private int getPerPopulation(int total, KonField konField) {
        if (konField == null) {
            return 0;
        }
        final var totalPopulation = konField.getFemale() + konField.getMale();
        if (totalPopulation == 0) {
            return 0;
        }
        final var million = 1000000L;
        return (int) (million * total / totalPopulation);
    }

    private int getForandringLangaSjukskrivningar(NationellDataInfo data) {
        SimpleKonResponse langaSjukfallPrevious = data.getOverviewLangaSjukfallDiffPreviousResult();
        SimpleKonResponse langaSjukfallCurrent = data.getOverviewLangaSjukfallDiffCurrentResult();
        if (langaSjukfallPrevious == null || langaSjukfallPrevious.getRows().isEmpty()
            || langaSjukfallCurrent == null || langaSjukfallCurrent.getRows().isEmpty()) {
            return 0;
        }
        int previous = total(langaSjukfallPrevious.getRows().get(0));
        int current = total(langaSjukfallCurrent.getRows().get(0));
        return percentChange(current, previous);
    }

    private int getLangaSjukskrivningar(NationellDataInfo data) {
        Integer langaSjukfall = data.getOverviewLangaSjukfallResult();
        if (langaSjukfall == null) {
            return 0;
        }
        return langaSjukfall;
    }

    private List<OverviewChartRow> getSjukskrivningslangdgrupper(NationellDataInfo data) {
        SimpleKonResponse previousData = data.getOverviewSjukskrivningslangdPreviousResult();
        SimpleKonResponse currentData = data.getOverviewSjukskrivningslangdCurrentResult();

        List<OverviewChartRow> result = new ArrayList<>();
        if (previousData == null || currentData == null) {
            return result;
        }
        for (int i = 0; i < currentData.getRows().size(); i++) {
            SimpleKonDataRow previous = previousData.getRows().get(i);
            SimpleKonDataRow current = currentData.getRows().get(i);

            int previousValue = previous.getFemale() + previous.getMale();
            int currentValue = current.getFemale() + current.getMale();

            result.add(new OverviewChartRowExtended(current.getName(), currentValue, currentValue - previousValue, null));
        }

        return result;
    }

    private List<OverviewChartRowExtended> getSjukskrivningsgrader(NationellDataInfo data) {
        KonDataResponse periodsPrevious = data.getOverviewSjukskrivningsgraderPrevious();
        KonDataResponse periodsCurrent = data.getOverviewSjukskrivningsgraderCurrent();

        Map<String, String> colors = SickLeaveDegree.getColors();

        List<OverviewChartRowExtended> result = new ArrayList<>();
        if (periodsPrevious == null || periodsCurrent == null) {
            return result;
        }
        List<KonField> previousData = periodsPrevious.getRows().get(0).getData();
        List<KonField> currentData = periodsCurrent.getRows().get(0).getData();
        for (int i = 0; i < previousData.size(); i++) {
            int previous = previousData.get(i).getFemale() + previousData.get(i).getMale();
            int current = currentData.get(i).getFemale() + currentData.get(i).getMale();
            String id = periodsCurrent.getGroups().get(i);
            String color = colors.get(id);

            result.add(new OverviewChartRowExtended(id, current, percentChange(current, previous), color));
        }
        return result;
    }

    private void sortByQuantity(List<OverviewChartRowExtended> result) {
        Collections.sort(result, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
    }

    private List<OverviewChartRowExtended> getAldersgrupper(NationellDataInfo data) {
        SimpleKonResponse previousData = data.getOverviewPreviousAldersgruppResult();
        SimpleKonResponse currentData = data.getOverviewCurrentAldersgruppResult();

        List<OverviewChartRowExtended> result = new ArrayList<>();
        if (previousData == null || currentData == null) {
            return result;
        }

        List<SimpleKonDataRow> previousDataRows = previousData.getRows();
        List<SimpleKonDataRow> currentDataRows = currentData.getRows();
        Map<String, String> colors = OverviewAgeGroup.getColors();

        for (int i = 0; i < currentDataRows.size(); i++) {
            SimpleKonDataRow previousRow = previousDataRows.get(i);
            SimpleKonDataRow currentRow = currentDataRows.get(i);
            int previous = previousRow.getFemale() + previousRow.getMale();
            int current = currentRow.getFemale() + currentRow.getMale();
            final String rowName = currentRow.getName();
            String color = colors.get(rowName);

            final OverviewChartRowExtended row = new OverviewChartRowExtended(rowName, current, current - previous, color);
            result.add(row);
        }

        return result;
    }

    private List<OverviewChartRowExtended> getResult(Set<String> include, List<OverviewChartRow> previousData,
        List<OverviewChartRow> currentData) {
        final var result = new ArrayList<OverviewChartRowExtended>(include.size());

        for (int i = 0; i < currentData.size(); i++) {
            final var rowName = previousData.get(i).getName();
            final var previous = previousData.get(i).getQuantity();
            final var current = currentData.get(i).getQuantity();

            if (include.contains(rowName)) {
                result.add(new OverviewChartRowExtended(rowName, current, percentChange(current, previous), null));
            }
        }

        sortByQuantity(result);

        return result;
    }

    private Set<String> getTop(int size, List<OverviewChartRow> currentData) {
        final var include = new HashSet<String>();
        currentData.stream()
            .sorted(Comparator.comparingInt(OverviewChartRow::getQuantity).reversed())
            .forEach(data -> {
                if (include.size() < size) {
                    include.add(data.getName());
                }
            });

        return include;
    }

    private int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    private List<OverviewChartRowExtended> getDiagnosgrupper(NationellDataInfo data) {
        DiagnosgruppResponse periods = data.getOverviewDiagnosgrupperResult();
        List<OverviewChartRowExtended> result = new ArrayList<>();
        if (periods == null) {
            return result;
        }
        final List<KonDataRow> rows = periods.getRows();
        if (rows.size() >= 2) {
            List<KonField> previousData = rows.get(0).getData();
            List<KonField> currentData = rows.get(1).getData();
            final List<? extends Icd> icdTyps = periods.getIcdTyps();
            for (int i = 0; i < icdTyps.size(); i++) {
                int previous = previousData.size() > i ? previousData.get(i).getFemale() + previousData.get(i).getMale() : 0;
                int current = currentData.size() > i ? currentData.get(i).getFemale() + currentData.get(i).getMale() : 0;
                final Icd icd = icdTyps.get(i);
                final int numericalId = icd.getNumericalId();
                final String rowName = String.valueOf(numericalId);
                final OverviewChartRowExtended row = new OverviewChartRowExtended(rowName, current, current - previous, null);
                result.add(row);
            }
        }
        return result;
    }

    private int total(SimpleKonDataRow current) {
        return current.getFemale() + current.getMale();
    }

    private OverviewKonsfordelning getSexProportion(NationellDataInfo data) {
        final SimpleKonResponse intyg = data.getOverviewGenderResult();
        final Range range = data.getOverviewRange();
        if (intyg.getRows().isEmpty()) {
            return new OverviewKonsfordelning(0, 0, range);
        }
        final SimpleKonDataRow dataRow = intyg.getRows().get(0);
        return new OverviewKonsfordelning(dataRow.getMale(), dataRow.getFemale(), range);
    }
}
