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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaId;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FilterHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FilterHandler.class);

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private FilterHashHandler filterHashHandler;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private LandstingEnhetHandler landstingEnhetHandler;

    @Autowired
    private EnhetManager enhetManager;

    List<HsaId> getEnhetsFilterIds(String filterHash, HttpServletRequest request) {
        if (filterHash == null || filterHash.isEmpty()) {
            final LoginInfo info = loginServiceUtil.getLoginInfo(request);
            final List<Verksamhet> businesses = info.getBusinesses();
            return Lists.transform(businesses, new Function<Verksamhet, HsaId>() {
                @Override
                public HsaId apply(Verksamhet verksamhet) {
                    return verksamhet.getId();
                }
            });
        }
        final FilterData filterData = filterHashHandler.getFilterFromHash(filterHash);
        return getEnhetsFiltered(request, filterData);
    }

    FilterSettings getFilterForLandsting(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue));
        }
        final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
        final ArrayList<HsaId> enhetsIDs = getEnhetsFilteredLandsting(request, inFilter);
        try {
            return getFilterSettingsLandsting(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter", e);
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    FilterSettings getFilter(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue));
        }
        final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
        final ArrayList<HsaId> enhetsIDs = getEnhetsFiltered(request, inFilter);
        try {
            return getFilterSettings(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    private FilterSettings getFilterSettings(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter, ArrayList<HsaId> enhetsIDs) throws FilterException {
        final Predicate<Fact> enhetFilter = getEnhetFilter(request, enhetsIDs);
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enhetsIDs, enhetFilter);
    }

    private FilterSettings getFilterSettingsLandsting(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter, ArrayList<HsaId> enhetsIDs) throws FilterException {
        final Predicate<Fact> enhetFilter = getEnhetFilterLandsting(request, enhetsIDs);
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enhetsIDs, enhetFilter);
    }

    private FilterSettings getFilterSettings(String filterHash, int defaultRangeValue, FilterData inFilter, ArrayList<HsaId> enhetsIDs, Predicate<Fact> enhetFilter) throws FilterException {
        final List<String> diagnoser = inFilter.getDiagnoser();
        final Predicate<Fact> diagnosFilter = getDiagnosFilter(diagnoser);
        final SjukfallFilter sjukfallFilter = new SjukfallFilter(Predicates.and(enhetFilter, diagnosFilter), filterHash);
        final Filter filter = new Filter(sjukfallFilter, enhetsIDs, diagnoser);
        final Range range = getRange(inFilter, defaultRangeValue);
        return new FilterSettings(filter, range);
    }

    private Range getRange(@NotNull FilterData inFilter, int defaultRangeValue) throws FilterException {
        if (inFilter.isUseDefaultPeriod()) {
            return Range.createForLastMonthsIncludingCurrent(defaultRangeValue);
        }
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(FilterData.DATE_FORMAT);
        final String fromDate = inFilter.getFromDate();
        final String toDate = inFilter.getToDate();

        if (fromDate == null || toDate == null) {
            throw new FilterException(String.format("Can not parse null range dates. From: {}, To: {}", fromDate, toDate));
        }
        try {
            final LocalDate from = dateStringFormat.parseLocalDate(fromDate);
            final LocalDate to = dateStringFormat.parseLocalDate(toDate);
            validateFilterRange(from, to);
            return new Range(from.withDayOfMonth(1), to.dayOfMonth().withMaximumValue());
        } catch (IllegalArgumentException e) {
            throw new FilterException(String.format("Could not parse range dates. From: {}, To: {}", fromDate, toDate), e);
        }
    }

    private void validateFilterRange(LocalDate from, LocalDate to) throws FilterException {
        final LocalDate lowestAcceptedStartDate = new LocalDate(2013, 10, 1);
        if (from.isBefore(lowestAcceptedStartDate)) {
            throw new FilterException("Start date before 2013-10-01 is not allowed");
        }
        if (to.isBefore(from)) {
            throw new FilterException("Start date must be before end date");
        }
        if (to.isAfter(new LocalDate().dayOfMonth().withMaximumValue())) {
            throw new FilterException("End date may not be after the last day of current month");
        }
    }

    private Filter getFilterForAllAvailableEnhetsLandsting(HttpServletRequest request) {
        final HsaId vardgivarId = getSelectedVgIdForLoggedInUser(request);
        final List<HsaId> enhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vardgivarId);
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(enhets, new Function<HsaId, Integer>() {
            @Override
            public Integer apply(HsaId enhetid) {
                return Warehouse.getEnhet(enhetid);
            }
        }));
        return getFilterForEnhets(availableEnhets, enhets);
    }

    private Filter getFilterForEnhets(final Set<Integer> enhetsIntIds, List<HsaId> enhets) {
        return new Filter(new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return enhetsIntIds.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(enhetsIntIds.toArray())), enhets, null);
    }

    private Filter getFilterForAllAvailableEnhets(HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        if (info.isProcessledare()) {
            return new Filter(SjukfallUtil.ALL_ENHETER, null, null);
        }
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(info.getBusinesses(), new Function<Verksamhet, Integer>() {
            @Override
            public Integer apply(Verksamhet verksamhet) {
                return Warehouse.getEnhet(verksamhet.getId());
            }
        }));
        return getFilterForEnhets(availableEnhets, null);
    }

    private ArrayList<HsaId> getEnhetsFilteredLandsting(HttpServletRequest request, FilterData inFilter) {
        Set<HsaId> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyperLandsting(inFilter, request);
        final HashSet<HsaId> enhets = new HashSet<>(toHsaIds(inFilter.getEnheter()));
        return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
    }

    private List<HsaId> toHsaIds(List<String> enheter) {
        return Lists.transform(enheter, new Function<String, HsaId>() {
            @Override
            public HsaId apply(String id) {
                return new HsaId(id);
            }
        });
    }

    private ArrayList<HsaId> getEnhetsFiltered(HttpServletRequest request, FilterData inFilter) {
        Set<HsaId> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyper(inFilter.getVerksamhetstyper(), request);
        final HashSet<HsaId> enhets = new HashSet<>(toHsaIds(inFilter.getEnheter()));
        return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
    }

    private Set<HsaId> getEnhetsForVerksamhetstyperLandsting(FilterData filterData, HttpServletRequest request) {
        final List<Enhet> enhets = enhetManager.getEnhets(toHsaIds(filterData.getEnheter()));
        Set<HsaId> enhetsIds = new HashSet<>();
        for (Enhet verksamhet : enhets) {
            if (isOfVerksamhetsTypLandsting(verksamhet, filterData.getVerksamhetstyper())) {
                enhetsIds.add(verksamhet.getEnhetId());
            }
        }
        return enhetsIds;
    }

    private Set<HsaId> getEnhetsForVerksamhetstyper(List<String> verksamhetstyper, HttpServletRequest request) {
        Set<HsaId> enhetsIds = new HashSet<>();
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        for (Verksamhet verksamhet : info.getBusinesses()) {
            if (isOfVerksamhetsTyp(verksamhet, verksamhetstyper)) {
                enhetsIds.add(verksamhet.getId());
            }
        }
        return enhetsIds;
    }

    private boolean isOfVerksamhetsTypLandsting(Enhet verksamhet, List<String> verksamhetstyper) {
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = loginServiceUtil.getVerksamhetsTyper(verksamhet.getVerksamhetsTyper());
        return isOfVerksamhetsTyp(verksamhetstyper, verksamhetstyperForCurrentVerksamhet);
    }

    private boolean isOfVerksamhetsTyp(List<String> verksamhetstyper, Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet) {
        if (verksamhetstyperForCurrentVerksamhet == null || verksamhetstyperForCurrentVerksamhet.isEmpty()) {
            return true;
        }
        for (Verksamhet.VerksamhetsTyp verksamhetsTyp : verksamhetstyperForCurrentVerksamhet) {
            if (verksamhetstyper.contains(verksamhetsTyp.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isOfVerksamhetsTyp(Verksamhet verksamhet, List<String> verksamhetstyper) {
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = verksamhet.getVerksamhetsTyper();
        return isOfVerksamhetsTyp(verksamhetstyper, verksamhetstyperForCurrentVerksamhet);
    }

    private Predicate<Fact> getDiagnosFilter(final List<String> diagnosIds) {
        return new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                if (diagnosIds == null || diagnosIds.isEmpty()) {
                    return false;
                }
                String diagnosKapitelString = String.valueOf(fact.getDiagnoskapitel());
                if (diagnosIds.contains(diagnosKapitelString)) {
                    return true;
                }
                String diagnosAvsnittString = String.valueOf(fact.getDiagnosavsnitt());
                if (diagnosIds.contains(diagnosAvsnittString)) {
                    return true;
                }
                String diagnosKategoriString = String.valueOf(fact.getDiagnoskategori());
                if (diagnosIds.contains(diagnosKategoriString)) {
                    return true;
                }
                return false;
            }
        };
    }

    private Predicate<Fact> getEnhetFilter(HttpServletRequest request, List<HsaId> enhetsIDs) {
        Set<HsaId> enheter = getEnhetNameMap(request, enhetsIDs).keySet();
        return sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaId[enheter.size()])).getFilter();
    }

    Map<HsaId, String> getEnhetNameMap(HttpServletRequest request, List<HsaId> enhetsIDs) {
        final HsaId vgid = getSelectedVgIdForLoggedInUser(request);
        LoginInfo info = loginServiceUtil.getLoginInfo(request);
        Map<HsaId, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinesses()) {
            if (userVerksamhet.getVardgivarId().equals(vgid)) {
                if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId())) {
                    enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
                }
            }
        }
        return enheter;
    }

    private Predicate<Fact> getEnhetFilterLandsting(HttpServletRequest request, List<HsaId> enhetsIDs) {
        Set<HsaId> enheter = getEnhetNameMapLandsting(request, enhetsIDs).keySet();
        return sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaId[enheter.size()])).getFilter();
    }

    private Map<HsaId, String> getEnhetNameMapLandsting(HttpServletRequest request, List<HsaId> filteredEnhetsIds) {
        final HsaId vgid = getSelectedVgIdForLoggedInUser(request);
        Map<HsaId, String> enheter = new HashMap<>();
        final List<HsaId> availableEnhetIds = landstingEnhetHandler.getAllEnhetsForVardgivare(vgid);
        final List<Enhet> enhets = enhetManager.getEnhets(availableEnhetIds);
        for (Enhet enhet : enhets) {
            if (filteredEnhetsIds != null && filteredEnhetsIds.contains(enhet.getEnhetId())) {
                enheter.put(enhet.getEnhetId(), enhet.getNamn());
            }
        }
        return enheter;
    }

    private HsaId getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return loginServiceUtil.getLoginInfo(request).getDefaultVerksamhet().getVardgivarId();
    }

}
