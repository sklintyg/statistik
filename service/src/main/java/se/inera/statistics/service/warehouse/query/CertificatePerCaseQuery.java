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
package se.inera.statistics.service.warehouse.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.CertificatePerCaseGroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.*;

@Component
public class CertificatePerCaseQuery {

    private static final Logger LOG = LoggerFactory.getLogger(CertificatePerCaseQuery.class);

    public static final Ranges RANGES = CertificatePerCaseGroupUtil.RANGES;

    private static final int DEFAULT_REGION_CUTOFF = 5;
    private int regionCutoff = DEFAULT_REGION_CUTOFF;

    @Autowired
    public void initProperty(@Value("${reports.landsting.cutoff}") int cutoff) {
        final int minimumCutoffValue = 3;
        if (cutoff < minimumCutoffValue) {
            LOG.warn("Region cutoff value is too low. Using minimum value: " + minimumCutoffValue);
            this.regionCutoff = minimumCutoffValue;
            return;
        }
        this.regionCutoff = cutoff;
    }

    public void setRegionCutoff(int cutoff) {
        this.regionCutoff = cutoff;
    }

    private static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, Ranges ranges) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(ranges);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getCertificateCount()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    private static SimpleKonResponse getCertificatePerCaseTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
                                                                    int periodLength, SjukfallUtil sjukfallUtil, int cutoff) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = count(sjukfallGroup.getSjukfall(), RANGES);
            for (Ranges.Range i : RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), ResponseUtil.filterCutoff(counter.getCountFemale(), cutoff),
                        ResponseUtil.filterCutoff(counter.getCountMale(), cutoff)));
            }
        }
        return new SimpleKonResponse(AvailableFilters.getForSjukfall(), rows);
    }

    public static SimpleKonResponse getCertificatePerCaseTvarsnitt(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
                                                                   int periodLength, SjukfallUtil sjukfallUtil) {
        return getCertificatePerCaseTvarsnitt(aisle, filter, from, periods, periodLength, sjukfallUtil, 0);
    }

    public SimpleKonResponse getCertificatePerCaseTvarsnittRegion(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
                                                            int periodLength, SjukfallUtil sjukfallUtil) {
        return getCertificatePerCaseTvarsnitt(aisle, filter, from, periods, periodLength, sjukfallUtil, this.regionCutoff);
    }

    public static KonDataResponse getCertificatePerCaseTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
                                                                 int periodLength, SjukfallUtil sjukfallUtil) {
        final Ranges ranges = RANGES;
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(ranges);
        final List<String> names = Lists.transform(rangesList, Ranges.Range::getName);
        final List<Integer> ids = Lists.transform(rangesList, Ranges.Range::getCutoff);
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            final int certificateCount = sjukfall.getCertificateCountIncludingBeforeCurrentPeriod();
            final int rangeId = ranges.getRangeCutoffForValue(certificateCount);
            counter.add(rangeId);
        };
        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

}
