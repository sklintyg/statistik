/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.service.dto.MessagesText;
import se.inera.statistics.web.model.ChartData;

public class DiagnosisSubGroupsTvarsnittConverterTest {

    @Test
    public void testConvertedResponseDoesNotContainEmptyOvrigtGroupINTYG1821() {
        //Given
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 0));
        final List<SimpleKonDataRow> simpleKonDataRows = toSimpleKonDataRows(data);
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);

        //When
        final ChartData result = new DiagnosisSubGroupsTvarsnittConverter().convertToChartData(casesPerMonth);

        //Then
        assertEquals(6, result.getCategories().size());
        assertTrue(result.getCategories().stream().noneMatch(chartSeries -> MessagesText.REPORT_GROUP_OTHER.equals(chartSeries.getName())));
    }

    private List<SimpleKonDataRow> toSimpleKonDataRows(ArrayList<KonField> data) {
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            KonField konField = data.get(i);
            simpleKonDataRows.add(new SimpleKonDataRow("Name" + i, konField));
        }
        return simpleKonDataRows;
    }

    @Test
    public void testConvertedResponseDoesNotContainOvrigtGroupWhenOnly7NoneEmptySeriesExistsINTYG1821() {
        //Given
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 1));
        final List<SimpleKonDataRow> simpleKonDataRows = toSimpleKonDataRows(data);
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);

        //When
        final ChartData result = new DiagnosisSubGroupsTvarsnittConverter().convertToChartData(casesPerMonth);

        //Then
        assertEquals(7, result.getCategories().size());
        assertTrue(result.getCategories().stream().noneMatch(chartSeries -> MessagesText.REPORT_GROUP_OTHER.equals(chartSeries.getName())));
    }

    @Test
    public void testConvertedResponseDoesContainNoneEmptyOvrigtGroupINTYG1821() {
        //Given
        ArrayList<KonDataRow> rows = new ArrayList<>();
        ArrayList<KonField> data = new ArrayList<>();
        data.add(new KonField(1, 0));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 0));
        data.add(new KonField(1, 2));
        data.add(new KonField(0, 0));
        data.add(new KonField(2, 2));
        data.add(new KonField(5, 0));
        data.add(new KonField(0, 2));
        data.add(new KonField(6, 0));
        data.add(new KonField(0, 1));
        final List<SimpleKonDataRow> simpleKonDataRows = toSimpleKonDataRows(data);
        final SimpleKonResponse casesPerMonth = new SimpleKonResponse(AvailableFilters.getForSjukfall(), simpleKonDataRows);

        //When
        final ChartData result = new DiagnosisSubGroupsTvarsnittConverter().convertToChartData(casesPerMonth);

        //Then
        assertEquals(7, result.getCategories().size());
        assertTrue(result.getCategories().stream().anyMatch(chartSeries -> MessagesText.REPORT_GROUP_OTHER.equals(chartSeries.getName())));
    }

}
