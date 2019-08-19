/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import java.util.Iterator;
import java.util.List;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.warehouse.query.AndelExtras;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static KonDataResponse createEmptyKonDataResponse(KonDataResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new KonDataResponse(kdr.getAvailableFilters(), kdr.getGroups(), rows);
    }

    static DiagnosgruppResponse createEmptyDiagnosgruppResponse(DiagnosgruppResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new DiagnosgruppResponse(kdr.getAvailableFilters(), kdr.getIcdTyps(), rows);
    }

    private static <T extends KonDataResponse> ArrayList<KonDataRow> getKonDataRows(T kdr) {
        final ArrayList<KonDataRow> rows = new ArrayList<>();
        for (KonDataRow row : kdr.getRows()) {
            final ArrayList<KonField> data = new ArrayList<>();
            for (int i = 0; i < row.getData().size(); i++) {
                Object extras = row.getData().get(i).getExtras() instanceof AndelExtras ? new AndelExtras(0, 0, 0, 0) : null;
                data.add(new KonField(0, 0, extras));
            }
            rows.add(new KonDataRow(row.getName(), data));
        }
        return rows;
    }

    public static List<KonDataRow> getKonDataRows(int perioder, Iterator<KonDataRow> rowsNew, Iterator<KonDataRow> rowsOld, int cutoff) {
        List<KonDataRow> list = new ArrayList<>(perioder);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            KonDataRow a = rowsNew.next();
            KonDataRow b = rowsOld.next();

            List<KonField> c = new ArrayList<>();
            for (int i = 0; i < a.getData().size(); i++) {
                final KonField aKonField = a.getData().get(i);
                final KonField bKonField = b.getData().get(i);
                final int female = filterCutoff(aKonField.getFemale(), cutoff) + bKonField.getFemale();
                final int male = filterCutoff(aKonField.getMale(), cutoff) + bKonField.getMale();
                final Object extras = getMergedExtras(aKonField, bKonField, cutoff);
                c.add(new KonField(female, male, extras));
            }
            list.add(new KonDataRow(a.getName(), c));
        }
        return list;
    }

    private static Object getMergedExtras(KonField aKonField, KonField bKonField, int cutoff) {
        Object aExtras = aKonField.getExtras();
        Object bExtras = bKonField.getExtras();
        if (aExtras instanceof AndelExtras && bExtras instanceof AndelExtras) {
            AndelExtras aAndelExtra = (AndelExtras) aExtras;
            AndelExtras bAndelExtra = (AndelExtras) bExtras;

            final int femaleKomplA = filterCutoff(aAndelExtra.getFemaleKompl(), cutoff);
            final int femaleKompl = femaleKomplA + bAndelExtra.getFemaleKompl();
            final int femaleIntyg = (isApplyCutoff(femaleKomplA, cutoff) ? 0 : aAndelExtra.getFemaleIntyg()) + bAndelExtra.getFemaleIntyg();

            final int maleKomplA = filterCutoff(aAndelExtra.getMaleKompl(), cutoff);
            final int maleKompl = maleKomplA + bAndelExtra.getMaleKompl();
            final int maleIntyg = (isApplyCutoff(maleKomplA, cutoff) ? 0 : aAndelExtra.getMaleIntyg()) + bAndelExtra.getMaleIntyg();

            return new AndelExtras(femaleIntyg, femaleKompl, maleIntyg, maleKompl);
        }
        return null;
    }

    public static int filterCutoff(int actual, int cutoff) {
        return isApplyCutoff(actual, cutoff) ? 0 : actual;
    }

    private static boolean isApplyCutoff(int actual, int cutoff) {
        return actual < cutoff;
    }

}
