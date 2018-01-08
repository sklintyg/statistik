/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.jetbrains.annotations.NotNull;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static KonDataResponse createEmptyKonDataResponse(KonDataResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new KonDataResponse(kdr.getGroups(), rows);
    }

    static DiagnosgruppResponse createEmptyDiagnosgruppResponse(DiagnosgruppResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new DiagnosgruppResponse(kdr.getIcdTyps(), rows);
    }

    private static <T extends KonDataResponse> ArrayList<KonDataRow> getKonDataRows(T kdr) {
        final ArrayList<KonDataRow> rows = new ArrayList<>();
        for (KonDataRow row : kdr.getRows()) {
            final ArrayList<KonField> data = new ArrayList<>();
            for (int i = 0; i < row.getData().size(); i++) {
                data.add(new KonField(0, 0));
            }
            rows.add(new KonDataRow(row.getName(), data));
        }
        return rows;
    }

    @NotNull
    public static List<KonDataRow> getKonDataRows(int perioder, Iterator<KonDataRow> rowsNew, Iterator<KonDataRow> rowsOld, int cutoff) {
        List<KonDataRow> list = new ArrayList<>(perioder);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            KonDataRow a = rowsNew.next();
            KonDataRow b = rowsOld.next();

            List<KonField> c = new ArrayList<>();
            for (int i = 0; i < a.getData().size(); i++) {
                c.add(new KonField(filterCutoff(a.getData().get(i).getFemale(), cutoff) + b.getData().get(i).getFemale(),
                        filterCutoff(a.getData().get(i).getMale(), cutoff) + b.getData().get(i).getMale()));
            }
            list.add(new KonDataRow(a.getName(), c));
        }
        return list;
    }

    static int filterCutoff(int actual, int cutoff) {
        return actual < cutoff ? 0 : actual;
    }

}
