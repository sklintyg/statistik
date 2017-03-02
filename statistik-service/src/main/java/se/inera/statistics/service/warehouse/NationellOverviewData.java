/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
public class NationellOverviewData {

    public static final int KVARTAL = 3;
    public static final int MAX_LAN = 5;
    public static final int PERCENT = 100;

    @Autowired
    private NationellData data;

    public OverviewResponse getOverview(Range range) {
        OverviewKonsfordelning sexProportion = getSexProportion(range);
        int intygForandring = getForandring(range);
        List<OverviewChartRowExtended> diagnosgrupper = getDiagnosgrupper(range);
        List<OverviewChartRowExtended> aldersgrupper = getAldersgrupper(range);
        List<OverviewChartRowExtended> sjukskrivningsgrader = getSjukskrivningsgrader(range);
        List<OverviewChartRow> sjukskrivningslangdgrupper = getSjukskrivningsgrupper(range);
        int langaSjukskrivningar = getLangaSjukskrivningar(range);
        int forandringLangaSjukskrivningar = getForandringLangaSjukskrivningar(range);
        List<OverviewChartRowExtended> intygPerLan = getIntygPerLan(range);
        return new OverviewResponse(sexProportion, intygForandring, diagnosgrupper, aldersgrupper, sjukskrivningsgrader,
                sjukskrivningslangdgrupper, langaSjukskrivningar, forandringLangaSjukskrivningar, intygPerLan);
    }

    private int getForandring(Range range) {
        SimpleKonResponse<SimpleKonDataRow> intyg = data.getAntalIntyg(ReportUtil.getPreviousPeriod(range).getFrom(), 2, KVARTAL);

        if (intyg.getRows().size() >= 2) {
            SimpleKonDataRow previous = intyg.getRows().get(0);
            SimpleKonDataRow current = intyg.getRows().get(1);
            return percentChange(total(current), total(previous));
        } else {
            return percentChange(0, 0);
        }
    }

    private List<OverviewChartRowExtended> getIntygPerLan(Range range) {
        SimpleKonResponse<SimpleKonDataRow> previousData = data.getSjukfallPerLan(ReportUtil.getPreviousPeriod(range).getFrom(), 1,
                KVARTAL);
        SimpleKonResponse<SimpleKonDataRow> currentData = data.getSjukfallPerLan(range.getFrom(), 1, KVARTAL);

        Set<String> include = getTop(MAX_LAN, currentData);

        return getResult(include, previousData, currentData, null);
    }

    private int getForandringLangaSjukskrivningar(Range range) {
        SimpleKonResponse<SimpleKonDataRow> langaSjukfall = data.getLangaSjukfall(ReportUtil.getPreviousPeriod(range).getFrom(), 2,
                KVARTAL);
        if (langaSjukfall.getRows().isEmpty()) {
            return 0;
        }
        int previous = total(langaSjukfall.getRows().get(0));
        int current = total(langaSjukfall.getRows().get(1));
        return percentChange(current, previous);
    }

    private int getLangaSjukskrivningar(Range range) {
        SimpleKonResponse<SimpleKonDataRow> langaSjukfall = data.getLangaSjukfall(range.getFrom(), 1, KVARTAL);
        if (langaSjukfall.getRows().isEmpty()) {
            return 0;
        }
        return langaSjukfall.getRows().get(0).getFemale() + langaSjukfall.getRows().get(0).getMale();
    }

    private List<OverviewChartRow> getSjukskrivningsgrupper(Range range) {
        SimpleKonResponse<SimpleKonDataRow> previousData = data.getSjukfallslangd(ReportUtil.getPreviousPeriod(range).getFrom(), 1,
                KVARTAL);
        SimpleKonResponse<SimpleKonDataRow> currentData = data.getSjukfallslangd(range.getFrom(), 1, KVARTAL);

        List<OverviewChartRow> result = new ArrayList<>();
        for (int i = 0; i < currentData.getRows().size(); i++) {
            int previous = previousData.getRows().get(i).getFemale() + previousData.getRows().get(i).getMale();
            int current = currentData.getRows().get(i).getFemale() + currentData.getRows().get(i).getMale();
            result.add(new OverviewChartRowExtended(previousData.getRows().get(i).getName(), current, current - previous));
        }

        return result;
    }

    private List<OverviewChartRowExtended> getSjukskrivningsgrader(Range range) {
        KonDataResponse periods = data.getSjukskrivningsgrad(ReportUtil.getPreviousPeriod(range).getFrom(), 2, KVARTAL, true);

        List<OverviewChartRowExtended> result = new ArrayList<>();
        if (periods.getRows().size() >= 2) {
            List<KonField> previousData = periods.getRows().get(0).getData();
            List<KonField> currentData = periods.getRows().get(1).getData();
            for (int i = 0; i < previousData.size(); i++) {
                int previous = previousData.get(i).getFemale() + previousData.get(i).getMale();
                int current = currentData.get(i).getFemale() + currentData.get(i).getMale();
                result.add(new OverviewChartRowExtended(periods.getGroups().get(i) + " %", current, percentChange(current, previous)));
            }
        }
        return result;
    }

    private void sortByQuantity(List<OverviewChartRowExtended> result) {
        Collections.sort(result, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
    }

    private List<OverviewChartRowExtended> getAldersgrupper(Range range) {
        SimpleKonResponse<SimpleKonDataRow> previousData = data.getAldersgrupper(ReportUtil.getPreviousPeriod(range).getFrom(), 1, KVARTAL);
        SimpleKonResponse<SimpleKonDataRow> currentData = data.getAldersgrupper(range.getFrom(), 1, KVARTAL);

        List<SimpleKonDataRow> previousDataRows = previousData.getRows();
        List<SimpleKonDataRow> currentDataRows = currentData.getRows();

        List<OverviewChartRowExtended> result = new ArrayList<>();
        for (int i = 0; i < currentDataRows.size(); i++) {
            SimpleKonDataRow previousRow = previousDataRows.get(i);
            SimpleKonDataRow currentRow = currentDataRows.get(i);
            int previous = previousRow.getFemale() + previousRow.getMale();
            int current = currentRow.getFemale() + currentRow.getMale();
            final String rowName = currentRow.getName();
            final OverviewChartRowExtended row = new OverviewChartRowExtended(rowName, current, current - previous);
            result.add(row);
        }

        return result;
    }

    private List<OverviewChartRowExtended> getResult(Set<String> include, SimpleKonResponse<SimpleKonDataRow> previousData,
            SimpleKonResponse<SimpleKonDataRow> currentData, String rest) {
        List<OverviewChartRowExtended> result = new ArrayList<>();

        int restCurrent = 0;
        int restPrevious = 0;

        for (int i = 0; i < currentData.getRows().size(); i++) {
            String rowName = previousData.getRows().get(i).getName();
            int previous = total(previousData.getRows().get(i));
            int current = total(currentData.getRows().get(i));

            if (include.contains(rowName)) {
                result.add(new OverviewChartRowExtended(rowName, current, percentChange(current, previous)));
            } else {
                restCurrent += current;
                restPrevious += previous;
            }
        }

        sortByQuantity(result);

        if (rest != null) {
            result.add(new OverviewChartRowExtended(rest, restCurrent, percentChange(restCurrent, restPrevious)));
        }

        return result;
    }

    private Set<String> getTop(int size, SimpleKonResponse<SimpleKonDataRow> currentData) {
        List<SimpleKonDataRow> sorted = new ArrayList<>(currentData.getRows());
        Collections.sort(sorted, (o1, o2) -> total(o2) - total(o1));

        Set<String> include = new HashSet<>();
        Iterator<SimpleKonDataRow> iterator = sorted.iterator();
        while (include.size() < size && iterator.hasNext()) {
            include.add(iterator.next().getName());
        }
        return include;
    }

    private int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    private List<OverviewChartRowExtended> getDiagnosgrupper(Range range) {
        DiagnosgruppResponse periods = data.getDiagnosgrupper(ReportUtil.getPreviousPeriod(range).getFrom(), 2, KVARTAL, true);
        List<OverviewChartRowExtended> result = new ArrayList<>();
        if (periods.getRows().size() >= 2) {
            List<KonField> previousData = periods.getRows().get(0).getData();
            List<KonField> currentData = periods.getRows().get(1).getData();
            for (int i = 0; i < currentData.size(); i++) {
                int previous = previousData.get(i).getFemale() + previousData.get(i).getMale();
                int current = currentData.get(i).getFemale() + currentData.get(i).getMale();
                final List<? extends Icd> icdTyps = periods.getIcdTyps();
                final Icd icd = icdTyps.get(i);
                final int numericalId = icd.getNumericalId();
                final String rowName = String.valueOf(numericalId);
                final OverviewChartRowExtended row = new OverviewChartRowExtended(rowName, current, current - previous);
                result.add(row);
            }
        }
        return result;
    }

    private int total(SimpleKonDataRow current) {
        return current.getFemale() + current.getMale();
    }

    private OverviewKonsfordelning getSexProportion(Range range) {
        SimpleKonResponse<SimpleKonDataRow> intyg = data.getAntalIntyg(range.getFrom(), 1, KVARTAL);

        if (intyg.getRows().isEmpty()) {
            return new OverviewKonsfordelning(0, 0, range);
        }
        SimpleKonDataRow dataRow = intyg.getRows().get(0);
        return new OverviewKonsfordelning(dataRow.getMale(), dataRow.getFemale(), range);
    }
}
