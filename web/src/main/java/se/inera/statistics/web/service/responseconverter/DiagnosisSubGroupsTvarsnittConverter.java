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
package se.inera.statistics.web.service.responseconverter;

import java.util.ArrayList;
import java.util.List;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.model.ChartData;

public class DiagnosisSubGroupsTvarsnittConverter extends SimpleDualSexConverter {

    public DiagnosisSubGroupsTvarsnittConverter() {
        super("", "%1$s");
    }

    @Override
    protected ChartData convertToChartData(SimpleKonResponse casesPerMonth) {
        final List<Integer> topColumnIndexes = DiagnosisSubGroupsConverter.getTopColumnIndexes(casesPerMonth);
        return super.convertToChartData(getTopColumns(casesPerMonth, topColumnIndexes));
    }

    private SimpleKonResponse getTopColumns(SimpleKonResponse skr, List<Integer> topIndexes) {
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        if (topIndexes.isEmpty()) {
            simpleKonDataRows.add(new SimpleKonDataRow(MessagesText.REPORT_GROUP_TOTALT, 0, 0));
        } else {
            for (Integer index : topIndexes) {
                if (index == DiagnosisSubGroupsConverter.OTHER_GROUP_INDEX) {
                    final KonField otherData = getDataForOtherGroups(skr, topIndexes);
                    simpleKonDataRows.add(new SimpleKonDataRow(MessagesText.REPORT_GROUP_OTHER, otherData));
                } else {
                    final SimpleKonDataRow row = skr.getRows().get(index);
                    simpleKonDataRows.add(row);
                }
            }
        }
        return new SimpleKonResponse(skr.getAvailableFilters(), simpleKonDataRows);
    }

    private KonField getDataForOtherGroups(SimpleKonResponse skr, List<Integer> topIndexes) {
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
