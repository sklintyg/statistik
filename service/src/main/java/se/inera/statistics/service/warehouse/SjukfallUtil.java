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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.caching.SjukfallGroupCacheKey;
import se.inera.statistics.service.report.model.ActiveFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.CounterFunction;

import com.google.common.collect.HashMultiset;

@Component
public class SjukfallUtil {

    public static final FilterPredicates ALL_ENHETER = new FilterPredicates(fact -> true, sjukfall -> true,
            FilterPredicates.HASH_EMPTY_FILTER, false);

    @Autowired
    private Cache cache;

    public FilterPredicates createEnhetFilter(HsaIdEnhet... enhetIds) {
        final Set<Integer> availableEnhets = Arrays.stream(enhetIds).map(Warehouse::getEnhet).collect(Collectors.toSet());
        final String hashValue = FilterPredicates.getHashValueForEnhets(availableEnhets);
        return new FilterPredicates(fact -> fact != null && availableEnhets.contains(fact.getEnhet()), sjukfall -> true, hashValue, false);
    }

    public List<SjukfallGroup> sjukfallGrupper(LocalDate from, int periods, int periodSize, Aisle aisle, FilterPredicates sjukfallFilter) {
        return cache.getSjukfallGroups(
                new SjukfallGroupCacheKey(from, periods, periodSize, aisle, sjukfallFilter));
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public <T> KonDataResponse calculateKonDataResponse(Aisle aisle, FilterPredicates filter, LocalDate start, int periods, int periodSize,
            List<String> groupNames, List<T> groupIds, CounterFunction<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallGrupper(start, periods, periodSize, aisle, filter)) {
            final HashMultiset<T> maleCounter = HashMultiset.create();
            final HashMultiset<T> femaleCounter = HashMultiset.create();
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                final HashMultiset<T> currentCounter = Kon.FEMALE.equals(sjukfall.getKon()) ? femaleCounter : maleCounter;
                counterFunction.addCount(sjukfall, currentCounter);
            }
            List<KonField> list = new ArrayList<>(groupIds.size());
            for (T id : groupIds) {
                list.add(new KonField(femaleCounter.count(id), maleCounter.count(id)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        return new KonDataResponse(ActiveFilters.getForSjukfall(), groupNames, rows);
    }

    public <T> KonDataResponse calculateKonDataResponse(Aisle aisle, FilterPredicates filter, LocalDate start, int periods, int periodSize,
            Function<Sjukfall, Collection<T>> groupsFunction, CounterFunction<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        final Iterable<SjukfallGroup> sjukfallGroups = sjukfallGrupper(start, periods, periodSize, aisle, filter);
        final HashSet<T> hashSet = new LinkedHashSet<>();
        for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                hashSet.addAll(groupsFunction.apply(sjukfall));
            }
        }
        for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
            final HashMultiset<T> maleCounter = HashMultiset.create();
            final HashMultiset<T> femaleCounter = HashMultiset.create();
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                final HashMultiset<T> currentCounter = Kon.FEMALE.equals(sjukfall.getKon()) ? femaleCounter : maleCounter;
                counterFunction.addCount(sjukfall, currentCounter);
            }
            List<KonField> list = new ArrayList<>();
            for (T id : hashSet) {
                list.add(new KonField(femaleCounter.count(id), maleCounter.count(id)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        final List<String> groupNames = hashSet.stream().map(String::valueOf).collect(Collectors.toList());
        return new KonDataResponse(ActiveFilters.getForSjukfall(), groupNames, rows);
    }
    // CHECKSTYLE:ON

    public SimpleKonResponse calculateSimpleKonResponse(Aisle aisle, FilterPredicates filter, LocalDate from, int periods,
            int periodLength, CounterFunction<Integer> toCount, List<Integer> groups) {
        return calculateSimpleKonResponse(toCount, groups, sjukfallGrupper(from, periods, periodLength, aisle, filter));
    }

    private SimpleKonResponse calculateSimpleKonResponse(CounterFunction<Integer> toCount, List<Integer> groups,
            Iterable<SjukfallGroup> sjukfallGroups) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        HashMultiset<Integer> maleCounter = HashMultiset.create();
        HashMultiset<Integer> femaleCounter = HashMultiset.create();
        for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                HashMultiset<Integer> counter = Kon.MALE.equals(sjukfall.getKon()) ? maleCounter : femaleCounter;
                toCount.addCount(sjukfall, counter);
            }
        }
        for (Integer group : groups) {
            rows.add(new SimpleKonDataRow(String.valueOf(group), femaleCounter.count(group), maleCounter.count(group)));
        }
        return new SimpleKonResponse(ActiveFilters.getForSjukfall(), rows);
    }

}
