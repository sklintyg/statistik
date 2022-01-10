/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.model.NamedData;

public final class ServiceUtil {

    private ServiceUtil() {
    }

    static List<Object> getMergedSexData(KonDataRow row) {
        List<Object> data = new ArrayList<>();
        for (KonField konField : row.getData()) {
            data.add(konField.getFemale() + konField.getMale());
            data.add(konField.getFemale());
            data.add(konField.getMale());
        }
        return data;
    }

    static int getRowSum(KonDataRow row) {
        return row.getData().stream().mapToInt(r -> r.getMale() + r.getFemale()).sum();
    }

    static void addSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        rows.add(getSumRow(rows, includeSumForLastColumn));
    }

    static NamedData getSumRow(List<NamedData> rows, boolean includeSumForLastColumn) {
        final ArrayList<Integer> sumData = new ArrayList<>();
        if (!rows.isEmpty()) {
            for (int i = 0; i < rows.get(0).getData().size(); i++) {
                sumData.add(0);
            }
            for (NamedData namedData : rows) {
                List<Object> datas = namedData.getData();
                for (int i = 0; i < datas.size(); i++) {
                    Object data = datas.get(i);
                    sumData.set(i, sumData.get(i) + Integer.parseInt(data.toString()));
                }
            }
            if (!includeSumForLastColumn) {
                sumData.remove(sumData.size() - 1);
            }
        }
        return new NamedData(MessagesText.REPORT_GROUP_TOTALT, sumData);
    }

}
