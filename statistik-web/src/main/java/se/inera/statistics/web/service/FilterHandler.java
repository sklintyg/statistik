/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.NotNull;
import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.landsting.LandstingEnhetHandler;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.Verksamhet;

import java.util.Collection;

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

    @Autowired
    private Clock clock;

    List<HsaIdEnhet> getEnhetsFilterIds(String filterHash, HttpServletRequest request) {
        if (filterHash == null || filterHash.isEmpty()) {
            final LoginInfo info = loginServiceUtil.getLoginInfo();
            final List<Verksamhet> businesses = info.getBusinessesForVg(getSelectedVgIdForLoggedInUser(request));
            return Lists.transform(businesses, Verksamhet::getId);
        }
        final FilterData filterData = filterHashHandler.getFilterFromHash(filterHash);
        return getEnhetsFiltered(request, filterData);
    }

    FilterSettings getFilterForLandsting(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock));
        }
        final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
        final ArrayList<HsaIdEnhet> enhetsIDs = getEnhetsFilteredLandsting(request, inFilter);
        try {
            return getFilterSettingsLandsting(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected landsting filter. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Could not use selected landsting filter. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhetsLandsting(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    List<Verksamhet> getAllVerksamhetsForLoggedInLandstingsUser(HttpServletRequest request) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> allEnhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vgIdForLoggedInUser);
        final List<Enhet> enhets = enhetManager.getEnhets(allEnhets);
        return Lists.transform(enhets, new Function<Enhet, Verksamhet>() {
            @Override
            public Verksamhet apply(Enhet enhet) {
                return loginServiceUtil.toVerksamhet(enhet);
            }
        });
    }


    FilterSettings getFilter(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        try {
            if (filterHash == null || filterHash.isEmpty()) {
                return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock));
            }
            final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
            final List<HsaIdEnhet> enhetsIDs = getEnhetsFiltered(request, inFilter);
            return getFilterSettings(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException | FilterHashException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Could not use selected filter. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request), Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock), "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.");
        }
    }

    private FilterSettings getFilterSettings(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter, List<HsaIdEnhet> enhetsInFilter) throws FilterException {
        Set<HsaIdEnhet> enheter = getEnhetNameMap(request, enhetsInFilter).keySet();
        final Predicate<Fact> enhetFilter = sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaIdEnhet[enheter.size()])).getIntygFilter();
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enheter, enhetFilter);
    }

    private FilterSettings getFilterSettingsLandsting(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter, ArrayList<HsaIdEnhet> enhetsInFilter) throws FilterException {
        Set<HsaIdEnhet> enheter = getEnhetNameMapLandsting(request, enhetsInFilter).keySet();
        final Predicate<Fact> enhetFilter = sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaIdEnhet[enheter.size()])).getIntygFilter();
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enheter, enhetFilter);
    }

    private FilterSettings getFilterSettings(String filterHash, int defaultRangeValue, FilterData inFilter, Collection<HsaIdEnhet> enhetsIDs, Predicate<Fact> enhetFilter) throws FilterException {
        final List<String> diagnoser = inFilter.getDiagnoser();
        final Predicate<Fact> diagnosFilter = getDiagnosFilter(diagnoser);
        final List<String> aldersgrupp = inFilter.getAldersgrupp();
        final Predicate<Fact> aldersgruppFilter = getAldersgruppFilter(aldersgrupp);
        final List<String> sjukskrivningslangds = inFilter.getSjukskrivningslangd();
        final Predicate<Sjukfall> sjukfallLengthFilter = getSjukfallLengthFilter(sjukskrivningslangds);
        final String predicatesHash = getHash(filterHash, enhetsIDs);
        final FilterPredicates sjukfallFilter = new FilterPredicates(Predicates.and(enhetFilter, diagnosFilter, aldersgruppFilter), sjukfallLengthFilter, predicatesHash, !sjukskrivningslangds.isEmpty());
        final Filter filter = new Filter(sjukfallFilter, enhetsIDs, diagnoser, filterDataToReadableSjukskrivningslangdName(inFilter), toReadableAgeGroupNames(aldersgrupp), filterHash);
        final Range rangeMessageDTO = getRange(inFilter, defaultRangeValue);
        return new FilterSettings(filter, rangeMessageDTO);
    }

    private String getHash(String filterHash, Collection<HsaIdEnhet> enhetsIDs) {
        return filterHash + enhetsIDs.stream().map(HsaIdAny::getId).collect(Collectors.joining());
    }

    private List<String> filterDataToReadableSjukskrivningslangdName(FilterData inFilter) {
        return toReadableSjukskrivningslangdName(inFilter.getSjukskrivningslangd());
    }

    private List<String> toReadableSjukskrivningslangdName(List<String> sjukskrivningslangds) {
        if (sjukskrivningslangds == null) {
            return Arrays.stream(SjukfallsLangdGroup.values()).map(SjukfallsLangdGroup::getGroupName).collect(Collectors.toList());
        }
        return sjukskrivningslangds.stream()
                .map(sjukskrivningslangd -> SjukfallsLangdGroup.parse(sjukskrivningslangd)
                    .flatMap(slg -> Optional.of(slg.getGroupName()))
                    .orElse(sjukskrivningslangd))
                .collect(Collectors.toList());
    }

    private Predicate<Sjukfall> getSjukfallLengthFilter(List<String> filterLangds) {
        if (filterLangds == null || filterLangds.isEmpty()) {
            return sjukfall -> true;
        }
        final List<SjukfallsLangdGroup> filteredLangdGroups = filterLangds.stream()
                .map(SjukfallsLangdGroup::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return sjukfall -> {
            final int realDays = sjukfall.getRealDays();
            return filteredLangdGroups.stream().anyMatch(group -> group.getFrom() <= realDays && group.getTo() >= realDays);
        };
    }

    private Range getRange(@NotNull FilterData inFilter, int defaultRangeValue) throws FilterException {
        if (inFilter.isUseDefaultPeriod()) {
            return Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock);
        }
        DateTimeFormatter dateStringFormat = DateTimeFormatter.ofPattern(FilterData.DATE_FORMAT);
        final String fromDate = inFilter.getFromDate();
        final String toDate = inFilter.getToDate();

        if (fromDate == null || toDate == null) {
            throw new FilterException("Can not parse null range dates. From: " + fromDate + ", To: " + toDate);
        }
        try {
            final LocalDate from = LocalDate.from(dateStringFormat.parse(fromDate));
            final LocalDate to = LocalDate.from(dateStringFormat.parse(toDate));
            validateFilterRange(from, to);
            return new Range(from.withDayOfMonth(1), to.plusMonths(1).withDayOfMonth(1).minusDays(1));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new FilterException("Could not parse range dates. From: " + fromDate + ", To: " + toDate, e);
        }
    }

    private void validateFilterRange(LocalDate from, LocalDate to) throws FilterException {
        final LocalDate lowestAcceptedStartDate = LocalDate.of(2013, 10, 1);
        if (from.isBefore(lowestAcceptedStartDate)) {
            throw new FilterException("Start date before 2013-10-01 is not allowed");
        }
        if (to.isBefore(from)) {
            throw new FilterException("Start date must be before end date");
        }
        if (to.isAfter(LocalDate.now(clock).plusMonths(1).withDayOfMonth(1).minusDays(1))) {
            throw new FilterException("End date may not be after the last day of current month");
        }
    }

    private Filter getFilterForAllAvailableEnhetsLandsting(HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> enhets = landstingEnhetHandler.getAllEnhetsForVardgivare(vardgivarId);
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(enhets, new Function<HsaIdEnhet, Integer>() {
            @Override
            public Integer apply(HsaIdEnhet enhetid) {
                return Warehouse.getEnhet(enhetid);
            }
        }));
        return getFilterForEnhets(availableEnhets, enhets);
    }

    private Filter getFilterForEnhets(final Set<Integer> enhetsIntIds, List<HsaIdEnhet> enhets) {
        final String hashValue = FilterPredicates.getHashValueForEnhets(enhetsIntIds);
        final FilterPredicates predicate = new FilterPredicates(fact -> enhetsIntIds.contains(fact.getEnhet()), sjukfall -> true, hashValue, false);
        final List<String> sjukskrivningslangd = toReadableSjukskrivningslangdName(null);
        final List<String> aldersgrupp = toReadableAgeGroupNames(null);
        return new Filter(predicate, enhets, null, sjukskrivningslangd, aldersgrupp, hashValue);
    }

    private Filter getFilterForAllAvailableEnhets(HttpServletRequest request) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        if (info.getLoginInfoForVg(vgId).map(LoginInfoVg::isProcessledare).orElse(false)) {
            return new Filter(SjukfallUtil.ALL_ENHETER, null, null, toReadableSjukskrivningslangdName(null), toReadableAgeGroupNames(null), FilterPredicates.HASH_EMPTY_FILTER);
        }
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(info.getBusinessesForVg(vgId), verksamhet -> Warehouse.getEnhet(verksamhet.getId())));
        return getFilterForEnhets(availableEnhets, null);
    }

    private ArrayList<HsaIdEnhet> getEnhetsFilteredLandsting(HttpServletRequest request, FilterData inFilter) {
        final List<String> verksamhetstyper = inFilter.getVerksamhetstyper();
        Set<HsaIdEnhet> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyperLandsting(inFilter, request);
        final List<String> enheter = inFilter.getEnheter();
        final HashSet<HsaIdEnhet> enhets = new HashSet<>(toHsaIds(enheter));
        if (verksamhetstyper.isEmpty() && enheter.isEmpty()) {
            return new ArrayList<>(enhetsMatchingVerksamhetstyp); //All available enhets for user
        } else if (verksamhetstyper.isEmpty()) {
            return new ArrayList<>(enhets);
        } else if (enheter.isEmpty()) {
            return new ArrayList<>(enhetsMatchingVerksamhetstyp);
        } else {
            return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
        }
    }

    private List<HsaIdEnhet> toHsaIds(List<String> enheter) {
        return Lists.transform(enheter, new Function<String, HsaIdEnhet>() {
            @Override
            public HsaIdEnhet apply(String id) {
                return new HsaIdEnhet(id);
            }
        });
    }

    private List<HsaIdEnhet> getEnhetsFiltered(HttpServletRequest request, FilterData inFilter) {
        final List<String> verksamhetstyper = inFilter.getVerksamhetstyper();
        Set<HsaIdEnhet> enhetsMatchingVerksamhetstyp = getEnhetsForVerksamhetstyper(verksamhetstyper, request);
        final List<String> enheter = inFilter.getEnheter();
        final HashSet<HsaIdEnhet> enhets = new HashSet<>(toHsaIds(enheter));
        if (verksamhetstyper.isEmpty() && enheter.isEmpty()) {
            return new ArrayList<>(enhetsMatchingVerksamhetstyp); //All available enhets for user
        } else if (verksamhetstyper.isEmpty()) {
            return new ArrayList<>(enhets);
        } else if (enheter.isEmpty()) {
            return new ArrayList<>(enhetsMatchingVerksamhetstyp);
        } else {
            return new ArrayList<>(Sets.intersection(enhetsMatchingVerksamhetstyp, enhets));
        }
    }

    private Set<HsaIdEnhet> getEnhetsForVerksamhetstyperLandsting(FilterData filterData, HttpServletRequest request) {
        final List<Verksamhet> verksamhets = getAllVerksamhetsForLoggedInLandstingsUser(request);
        Set<HsaIdEnhet> enhetsIds = new HashSet<>();
        for (Verksamhet verksamhet : verksamhets) {
            if (isOfVerksamhetsTyp(filterData.getVerksamhetstyper(), verksamhet.getVerksamhetsTyper())) {
                enhetsIds.add(verksamhet.getIdUnencoded());
            }
        }
        return enhetsIds;
    }

    private Set<HsaIdEnhet> getEnhetsForVerksamhetstyper(List<String> verksamhetstyper, HttpServletRequest request) {
        Set<HsaIdEnhet> enhetsIds = new HashSet<>();
        LoginInfo info = loginServiceUtil.getLoginInfo();
        for (Verksamhet verksamhet : info.getBusinessesForVg(getSelectedVgIdForLoggedInUser(request))) {
            if (isOfVerksamhetsTyp(verksamhet, verksamhetstyper)) {
                enhetsIds.add(verksamhet.getIdUnencoded());
            }
        }
        return enhetsIds;
    }

    private boolean isOfVerksamhetsTyp(List<String> verksamhetstyper, Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet) {
        if (verksamhetstyper == null || verksamhetstyper.isEmpty() || verksamhetstyperForCurrentVerksamhet == null || verksamhetstyperForCurrentVerksamhet.isEmpty()) {
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
        if (verksamhetstyper.isEmpty()) {
            return true;
        }
        final Set<Verksamhet.VerksamhetsTyp> verksamhetstyperForCurrentVerksamhet = verksamhet.getVerksamhetsTyper();
        return isOfVerksamhetsTyp(verksamhetstyper, verksamhetstyperForCurrentVerksamhet);
    }

    private Predicate<Fact> getDiagnosFilter(final List<String> diagnosIds) {
        return fact -> {
            if (diagnosIds == null || diagnosIds.isEmpty()) {
                return true;
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
            return diagnosIds.contains(diagnosKategoriString);
        };
    }

    private List<String> toReadableAgeGroupNames(List<String> ageGroups) {
        if (ageGroups == null) {
            return Arrays.stream(AgeGroup.values()).map(AgeGroup::getGroupName).collect(Collectors.toList());
        }
        return ageGroups.stream()
                .map(ageGroup -> AgeGroup.parse(ageGroup)
                        .flatMap(ag -> Optional.of(ag.getGroupName()))
                        .orElse(ageGroup))
                .collect(Collectors.toList());
    }

    private Predicate<Fact> getAldersgruppFilter(List<String> ageGroups) {
        if (ageGroups == null || ageGroups.isEmpty()) {
            return fact -> true;
        }
        final List<AgeGroup> filteredAgeGroups = ageGroups.stream()
                .map(AgeGroup::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return fact -> {
            final int age = fact.getAlder();
            return filteredAgeGroups.stream().anyMatch(group -> group.getFrom() <= age && group.getTo() >= age);
        };
    }

    Map<HsaIdEnhet, String> getEnhetNameMap(HttpServletRequest request, List<HsaIdEnhet> enhetsIDs) {
        final HsaIdVardgivare vgid = getSelectedVgIdForLoggedInUser(request);
        LoginInfo info = loginServiceUtil.getLoginInfo();
        Map<HsaIdEnhet, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinessesForVg(vgid)) {
            if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId())) {
                enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
            }
        }
        return enheter;
    }

    private Map<HsaIdEnhet, String> getEnhetNameMapLandsting(HttpServletRequest request, List<HsaIdEnhet> filteredEnhetsIds) {
        final HsaIdVardgivare vgid = getSelectedVgIdForLoggedInUser(request);
        Map<HsaIdEnhet, String> enheter = new HashMap<>();
        final List<HsaIdEnhet> availableEnhetIds = landstingEnhetHandler.getAllEnhetsForVardgivare(vgid);
        final List<Enhet> enhets = enhetManager.getEnhets(availableEnhetIds);
        for (Enhet enhet : enhets) {
            if (filteredEnhetsIds != null && filteredEnhetsIds.contains(enhet.getEnhetId())) {
                enheter.put(enhet.getEnhetId(), enhet.getNamn());
            }
        }
        return enheter;
    }

    private HsaIdVardgivare getSelectedVgIdForLoggedInUser(HttpServletRequest request) {
        return loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
    }

}
