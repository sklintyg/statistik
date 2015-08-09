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

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.web.model.ChartData;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static se.inera.statistics.web.service.DifferentieratIntygandeConverter.createTableFieldText;

public class DifferentieratIntygandeTvarsnittConverter extends SimpleDualSexConverter {

    public static final float PERCENTAGE = 100F;

    public DifferentieratIntygandeTvarsnittConverter() {
        super("", false, "%1$s");
    }

    @Override
    protected TableData convertToTableData(List<SimpleKonDataRow> list) {
        final int sumFemale = sumForGender(list, Kon.Female);
        final int sumMale = sumForGender(list, Kon.Male);

        List<NamedData> data = new ArrayList<>();
        for (SimpleKonDataRow row : list) {
            final Integer female = row.getFemale();
            final Integer male = row.getMale();
            final String seriesName = String.format(getSeriesNameTemplate(), row.getName());
            data.add(new NamedData(seriesName, Arrays.asList(female + male, createTableFieldText(female, sumFemale), createTableFieldText(male, sumMale)), isMarked(row)));
        }

        return TableData.createWithSingleHeadersRow(data, Arrays.asList(getTableGroupTitle(), "Antal sjukfall totalt", "Antal sjukfall för kvinnor", "Antal sjukfall för män"));
    }

    private int sumForGender(List<SimpleKonDataRow> list, Kon kon) {
        int sum = 0;
        for (SimpleKonDataRow row : list) {
            sum += row.getDataForSex(kon);
        }
        return sum;
    }

    //TODO Is not doing anything, remove?
    @Override
    protected ChartData convertToChartData(SimpleKonResponse<SimpleKonDataRow> casesPerMonth) {
        final int sumFemale = sum(casesPerMonth.getDataForSex(Kon.Female));
        final int sumMale = sum(casesPerMonth.getDataForSex(Kon.Male));
        ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        for (SimpleKonDataRow row : casesPerMonth.getRows()) {
            rows.add(new SimpleKonDataRow(row.getName(), percentage(row.getFemale(), sumFemale), percentage(row.getMale(), sumMale), row.getExtras()));
        }
        SimpleKonResponse<SimpleKonDataRow> response = new SimpleKonResponse<>(rows);
//        return super.convertToChartData(response);
        return super.convertToChartData(casesPerMonth);
    }

    private int percentage(int part, int total) {
        return Math.round(PERCENTAGE * part / total);
    }

    private int sum(List<Integer> values) {
        int sum = 0;
        for (Integer value : values) {
            sum += value;
        }
        return sum;
    }

}
