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
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
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

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallQuery.class);

    @Autowired
    private LakareManager lakareManager;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    public SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate start, int perioder, int periodlangd) {
        final Function<SjukfallGroup, String> rowNameFunction = new Function<SjukfallGroup, String>() {
            @Override
            public String apply(SjukfallGroup sjukfallGroup) {
                return ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            }
        };
        return getSjukfall(aisle, filter, start, perioder, periodlangd, rowNameFunction);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate start, int perioder, int periodlangd) {
        final Function<SjukfallGroup, String> rowNameFunction = new Function<SjukfallGroup, String>() {
            @Override
            public String apply(SjukfallGroup sjukfallGroup) {
                return "Totalt";
            }
        };
        return getSjukfall(aisle, filter, start, perioder, periodlangd, rowNameFunction);
    }

    private SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate start, int perioder, int periodlangd, Function<SjukfallGroup, String> rowName) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, filter)) {
            int male = countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            result.add(new SimpleKonDataRow(rowName.apply(sjukfallGroup), female, male));
        }

        return new SimpleKonResponse<>(result);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerEnhet(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, Map<String, String> idsToNames) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
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
        return new SimpleKonResponse<>(rows);
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

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakare(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        final KonDataResponse konDataResponse = getSjukfallPerLakareSomTidsserie(aisle, filter, start, periods, periodLength);
        return SimpleKonResponse.create(konDataResponse);
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

    @VisibleForTesting
    void setLakareManager(LakareManager lakareManager) {
        this.lakareManager = lakareManager;
    }

    public KonDataResponse getSjukfallPerEnhetSeries(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, Map<String, String> idsToNames) {
        final ArrayList<Map.Entry<String, String>> groupEntries = new ArrayList<>(idsToNames.entrySet());
        final List<String> names = Lists.transform(groupEntries, new Function<Map.Entry<String, String>, String>() {
            @Override
            public String apply(Map.Entry<String, String> entry) {
                return entry.getValue();
            }
        });
        final List<Integer> ids = Lists.transform(groupEntries, new Function<Map.Entry<String, String>, Integer>() {
            @Override
            public Integer apply(Map.Entry<String, String> entry) {
                return Warehouse.getEnhet(entry.getKey());
            }
        });

        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                for (Integer enhetid : sjukfall.getEnhets()) {
                    counter.add(enhetid);
                }
            }
        };

        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodSize, names, ids, counterFunction);
    }

    public KonDataResponse getSjukfallPerLakareSomTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        final List<Lakare> allLakaresForVardgivare = lakareManager.getLakares(aisle.getVardgivareId());
        final List<Integer> ids = Lists.transform(allLakaresForVardgivare, new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return Warehouse.getNumLakarId(lakare.getLakareId());
            }
        });
        final List<String> idNames = Lists.transform(ids, new Function<Integer, String>() {
            @Override
            public String apply(Integer id) {
                return String.valueOf(id);
            }
        });
        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                sjukfall.getLakare();
                for (se.inera.statistics.service.warehouse.Lakare lakare : sjukfall.getLakare()) {
                    counter.add(lakare.getId());
                }
            }
        };

        final KonDataResponse response = sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, idNames, ids, counterFunction);
        final KonDataResponse filteredResponse = KonDataResponse.createNewWithoutEmptyGroups(response);
        return changeLakareIdToLakarName(allLakaresForVardgivare, filteredResponse);
    }

    private KonDataResponse changeLakareIdToLakarName(List<Lakare> allLakares, final KonDataResponse response) {
        final Collection<Lakare> allLakareInResponse = Collections2.filter(allLakares, new Predicate<Lakare>() {
            @Override
            public boolean apply(Lakare lakare) {
                return response.getGroups().contains(String.valueOf(Warehouse.getNumLakarId(lakare.getLakareId())));
            }
        });
        final Set<String> nameDuplicates = findDuplicates(allLakareInResponse);
        final List<String> updatedLakareNames = Lists.transform(response.getGroups(), new Function<String, String>() {
            @Override
            public String apply(String lakareId) {
                for (Lakare lakare : allLakareInResponse) {
                    if (lakareId.equals(String.valueOf(Warehouse.getNumLakarId(lakare.getLakareId())))) {
                        final String namn = lakarNamn(lakare);
                        return namn + (nameDuplicates.contains(namn) ? " " + lakare.getLakareId() : "");
                    }
                }
                LOG.warn("Could not find name for lakare: " + lakareId);
                return lakareId;
            }
        });
        return new KonDataResponse(updatedLakareNames, response.getRows());
    }

}
