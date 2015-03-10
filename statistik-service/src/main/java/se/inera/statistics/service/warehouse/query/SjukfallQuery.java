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
package se.inera.statistics.service.warehouse.query;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public final class SjukfallQuery {

    @Autowired
    private LakareManager lakareManager;

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : SjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, filter)) {
            int male = countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            result.add(new SimpleKonDataRow(displayDate, female, male));
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfallPerEnhet(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, Map<String, String> idsToNames) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            final Multiset<Integer> femaleSjukfallPerEnhet = HashMultiset.create();
            final Multiset<Integer> maleSjukfallPerEnhet = HashMultiset.create();

            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                Multiset<Integer> sjukfallPerEnhet = sjukfall.getKon() == Kon.Female ? femaleSjukfallPerEnhet : maleSjukfallPerEnhet;
                for (Integer enhetId : sjukfall.getEnhets()) {
                    sjukfallPerEnhet.add(enhetId);
                }
            }
            for (Map.Entry<String, String> enhetIdAndName : idsToNames.entrySet()) {
                final int enhetIntId = Warehouse.getEnhet(enhetIdAndName.getKey());
                if (enhetIntId >= 0) {
                    final String enhetName = enhetIdAndName.getValue();
                    final int femaleCount = femaleSjukfallPerEnhet.count(enhetIntId);
                    final int maleCount = maleSjukfallPerEnhet.count(enhetIntId);
                    rows.add(new SimpleKonDataRow(enhetName, femaleCount, maleCount));
                }
            }
        }
        return new SimpleKonResponse<>(rows, periodLength);
    }

    public static int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == Kon.Male) {
                count++;
            }
        }
        return count;
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakare(String vardgivarId, Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        List<Lakare> allLakaresForVardgivare = lakareManager.getLakares(vardgivarId);
        // Two counters for sjukfall per sex
        final Multiset<Lakare> femaleSjukfallPerLakare = HashMultiset.create();
        final Multiset<Lakare> maleSjukfallPerLakare = HashMultiset.create();

        for (Sjukfall sjukfall : sjukfalls) {
            for (se.inera.statistics.service.warehouse.Lakare warehousLakare : sjukfall.getLakare()) {
                Lakare lakare = getLakare(allLakaresForVardgivare, warehousLakare.getId());
                if (lakare != null) {
                    if (sjukfall.getKon() == Kon.Female) {
                        femaleSjukfallPerLakare.add(lakare);
                    } else {
                        maleSjukfallPerLakare.add(lakare);
                    }
                }
            }
        }

        // All lakares who have male or female sjukfalls
        Set<Lakare> allLakaresWithSjukfall = Multisets.union(femaleSjukfallPerLakare, maleSjukfallPerLakare).elementSet();
        final Set<String> duplicateNames = findDuplicates(allLakaresWithSjukfall);

        List<SimpleKonDataRow> result = new ArrayList<>();
        for (Lakare lakare : allLakaresWithSjukfall) {
            String lakarNamn = lakarNamn(lakare);
            if (duplicateNames.contains(lakarNamn)) {
                lakarNamn = lakarNamn + " " + lakare.getLakareId();
            }
            result.add(new SimpleKonDataRow(lakarNamn, femaleSjukfallPerLakare.count(lakare), maleSjukfallPerLakare.count(lakare)));
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    // Collect a list of all "läkar-namn" that exist more than once in the set of lakare
    private Set<String> findDuplicates(Collection<Lakare> lakares) {
        Set<String> duplicates = new HashSet<>();
        Set<String> seenLakarNames = new HashSet<>();
        for (Lakare lakare : lakares) {
            String lakarNamn = lakarNamn(lakare);
            if (seenLakarNames.contains(lakarNamn)) {
                duplicates.add(lakarNamn);
            }
            seenLakarNames.add(lakarNamn);
        }
        return duplicates;
    }

    private String lakarNamn(Lakare lakare) {
        return lakare.getTilltalsNamn() + " " + lakare.getEfterNamn();
    }

    private Lakare getLakare(List<Lakare> lakares, final Integer lakarId) {
        return Iterables.find(lakares, new Predicate<Lakare>() {
            @Override
            public boolean apply(Lakare lakare) {
                return lakarId == Warehouse.getNumLakarId(lakare.getLakareId());
            }
        }, null);
    }

    @VisibleForTesting
    public void setLakareManager(LakareManager lakareManager) {
        this.lakareManager = lakareManager;
    }

}
