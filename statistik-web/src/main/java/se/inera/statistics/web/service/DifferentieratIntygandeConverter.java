/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 * <p/>
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 * <p/>
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.NamedData;

import java.util.ArrayList;
import java.util.List;

public class DifferentieratIntygandeConverter extends MultiDualSexConverter<KonDataResponse> {

    public static final float PERCENTAGE = 100F;

    DualSexStatisticsData convert(KonDataResponse degreeOfSickLeave, FilterSettings filterSettings) {
        return super.convert(degreeOfSickLeave, filterSettings, null, "%1$s");
    }

    @Override
    List<NamedData> getTableRows(KonDataResponse resp) {
        List<NamedData> rows = new ArrayList<>();
        for (KonDataRow row : resp.getRows()) {
            int sumF = 0;
            int sumM = 0;
            for (KonField konField : row.getData()) {
                sumF += konField.getFemale();
                sumM += konField.getMale();
            }

            final ArrayList<String> data = new ArrayList<>();
            data.add(String.valueOf(sumF + sumM));
            for (KonField konField : row.getData()) {
                data.add(createTableFieldText(konField.getFemale(), sumF));
                data.add(createTableFieldText(konField.getMale(), sumM));
            }
            rows.add(new NamedData(row.getName(), data));
        }
        return rows;
    }

    static String createTableFieldText(int value, int sum) {
        return Math.round(PERCENTAGE * value / sum) + "% (" + value + ")";
    }

}
