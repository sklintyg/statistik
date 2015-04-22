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

import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartData;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisSubGroupsTvarsnittConverter extends SimpleDualSexConverter {

    public DiagnosisSubGroupsTvarsnittConverter() {
        super("", false, "%1$s");
    }

    @Override
    protected ChartData convertToChartData(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final List<Integer> topColumnIndexes = DiagnosisSubGroupsConverter.getTopColumnIndexes(casesPerMonth);
        return super.convertToChartData(getTopColumns(casesPerMonth, topColumnIndexes));
    }

    public SimpleKonResponse<SimpleKonDataRow> getTopColumns(SimpleKonResponse<SimpleKonDataRow> skr, List<Integer> topIndexes) {
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        if (topIndexes.isEmpty()) {
            simpleKonDataRows.add(new SimpleKonDataRow("Totalt", 0, 0));
        } else {
            for (Integer index : topIndexes) {
                final SimpleKonDataRow row = skr.getRows().get(index);
                simpleKonDataRows.add(row);
            }
            if (skr.getRows().size() > DiagnosisSubGroupsConverter.NUMBER_OF_CHART_SERIES) {
                final KonField otherData = getDataForOtherGroups(skr, topIndexes);
                simpleKonDataRows.add(new SimpleKonDataRow("Övriga", otherData));
            }
        }
        return new SimpleKonResponse<>(simpleKonDataRows);
    }

    private KonField getDataForOtherGroups(SimpleKonResponse<SimpleKonDataRow> skr, List<Integer> topIndexes) {
        int female = 0;
        int male = 0;
        for (int i = 0; i < skr.getRows().size(); i++) {
            if (!topIndexes.contains(i)) {
                female += skr.getRows().get(i).getFemale();
                male += skr.getRows().get(i).getMale();
            }
        }
        return new KonField(female, male);
    }

}
