/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import com.google.common.collect.Sets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.region.RegionEnhetHandler;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.web.service.dto.Messages;
import se.inera.statistics.web.service.dto.MessagesText;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.LoginInfoVg;
import se.inera.statistics.web.model.RangeMessageDTO;
import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.service.dto.Filter;
import se.inera.statistics.web.service.dto.FilterData;
import se.inera.statistics.web.service.dto.FilterSettings;
import se.inera.statistics.web.service.exception.FilterException;
import se.inera.statistics.web.service.exception.FilterHashException;
import se.inera.statistics.web.service.exception.TooEarlyEndDateException;
import se.inera.statistics.web.service.exception.TooLateStartDateException;

@Component
public class FilterHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FilterHandler.class);
    private static final LocalDate LOWEST_ACCEPTED_START_DATE = LocalDate.of(2013, 10, 1);

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private FilterHashHandler filterHashHandler;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private RegionEnhetHandler regionEnhetHandler;

    @Autowired
    private EnhetManager enhetManager;

    @Autowired
    private Clock clock;

    public List<HsaIdEnhet> getEnhetsFilterIds(Collection<HsaIdEnhet> filteredEnhets, HttpServletRequest request) {
        if (filteredEnhets == null) {
            final LoginInfo info = loginServiceUtil.getLoginInfo();
            final List<Verksamhet> businesses = info.getBusinessesForVg(getSelectedVgIdForLoggedInUser(request));
            return businesses.stream().map(Verksamhet::getId).collect(Collectors.toList());
        }
        return new ArrayList<>(filteredEnhets);
    }

    public FilterSettings getFilterForRegion(HttpServletRequest request, String filterHash, int defaultRangeValue) {
        if (filterHash == null || filterHash.isEmpty()) {
            return new FilterSettings(getFilterForAllAvailableEnhetsRegion(request),
                Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock));
        }
        final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
        final ArrayList<HsaIdEnhet> enhetsIDs = getEnhetsFilteredRegion(request, inFilter);
        try {
            return getFilterSettingsRegion(request, filterHash, defaultRangeValue, inFilter, enhetsIDs);
        } catch (FilterException e) {
            LOG.warn("Could not use selected region filter. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Could not use selected region filter. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhetsRegion(request),
                Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_013));
        } catch (TooEarlyEndDateException e) {
            LOG.warn("End date too early. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("End date too early. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request, true),
                Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_004));
        } catch (TooLateStartDateException e) {
            LOG.warn("Start date too late. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Start date too late. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request, true),
                Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_009));
        }
    }

    public List<Verksamhet> getAllVerksamhetsForLoggedInRegionsUser(HttpServletRequest request) {
        final HsaIdVardgivare vgIdForLoggedInUser = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> allEnhets = regionEnhetHandler.getAllEnhetsForVardgivare(vgIdForLoggedInUser);
        final List<Enhet> enhets = enhetManager.getEnhets(allEnhets);
        return enhets.stream().map(enhet -> loginServiceUtil.enhetToVerksamhet(enhet)).collect(Collectors.toList());
    }


    public FilterSettings getFilter(HttpServletRequest request, String filterHash, int defaultNumberOfMonthsInRange) {
        return getFilter(request, filterHash, defaultNumberOfMonthsInRange, true);
    }

    public FilterSettings getFilter(HttpServletRequest request, String filterHash,
        int defaultNumberOfMonthsInRange, boolean vardenhetsdepth) {
        try {
            if (filterHash == null || filterHash.isEmpty()) {
                return new FilterSettings(getFilterForAllAvailableEnhets(request, vardenhetsdepth),
                    Range.createForLastMonthsIncludingCurrent(defaultNumberOfMonthsInRange, clock));
            }
            final FilterData inFilter = filterHashHandler.getFilterFromHash(filterHash);
            final List<HsaIdEnhet> enhetsIDs = getEnhetsFiltered(request, inFilter);
            return getFilterSettings(request, filterHash, defaultNumberOfMonthsInRange, inFilter, enhetsIDs, vardenhetsdepth);
        } catch (FilterException | FilterHashException e) {
            LOG.warn("Could not use selected filter. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Could not use selected filter. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request, vardenhetsdepth),
                Range.createForLastMonthsIncludingCurrent(defaultNumberOfMonthsInRange, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_013));
        } catch (TooEarlyEndDateException e) {
            LOG.warn("End date too early. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("End date too early. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request, vardenhetsdepth),
                Range.createForLastMonthsIncludingCurrent(defaultNumberOfMonthsInRange, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_004));
        } catch (TooLateStartDateException e) {
            LOG.warn("Start date too late. Falling back to default filter. Msg: " + e.getMessage());
            LOG.debug("Start date too late. Falling back to default filter.", e);
            return new FilterSettings(getFilterForAllAvailableEnhets(request, vardenhetsdepth),
                Range.createForLastMonthsIncludingCurrent(defaultNumberOfMonthsInRange, clock),
                Message.create(ErrorType.FILTER, ErrorSeverity.WARN, Messages.ST_F_FI_009));
        }
    }

    private FilterSettings getFilterSettings(HttpServletRequest request, String filterHash, int defaultRangeValue, FilterData inFilter,
        List<HsaIdEnhet> enhetsInFilter, boolean veDepth) throws FilterException, TooEarlyEndDateException, TooLateStartDateException {
        Set<HsaIdEnhet> enheter = getEnhetNameMapInclusive(request, enhetsInFilter, veDepth).keySet();
        final Predicate<Fact> enhetFilter = sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaIdEnhet[enheter.size()]))
            .getIntygFilter();
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enheter, enhetFilter, veDepth);
    }

    private FilterSettings getFilterSettingsRegion(HttpServletRequest request, String filterHash, int defaultRangeValue,
        FilterData inFilter, ArrayList<HsaIdEnhet> enhetsInFilter) throws FilterException, TooEarlyEndDateException,
        TooLateStartDateException {
        Set<HsaIdEnhet> enheter = getEnhetNameMapRegion(request, enhetsInFilter).keySet();
        final Predicate<Fact> enhetFilter = sjukfallUtil.createEnhetFilter(enheter.toArray(new HsaIdEnhet[enheter.size()]))
            .getIntygFilter();
        return getFilterSettings(filterHash, defaultRangeValue, inFilter, enheter, enhetFilter, true);
    }

    private FilterSettings getFilterSettings(String filterHash, int defaultRangeValue, FilterData inFilter,
        Collection<HsaIdEnhet> enhetsIDs, Predicate<Fact> enhetFilter, boolean veDepth)
        throws FilterException, TooEarlyEndDateException, TooLateStartDateException {
        final List<String> diagnoser = inFilter.getDiagnoser();
        final Predicate<Fact> diagnosFilter = getDiagnosFilter(diagnoser);
        final List<String> aldersgrupp = inFilter.getAldersgrupp();
        final Predicate<Fact> aldersgruppFilter = getAldersgruppFilter(aldersgrupp);
        final List<String> sjukskrivningslangds = inFilter.getSjukskrivningslangd();
        final Predicate<Sjukfall> sjukfallLengthFilter = getSjukfallLengthFilter(sjukskrivningslangds);
        final String hash = getHash(filterHash, enhetsIDs) + veDepth;
        final Predicate<Fact> predicate = enhetFilter.and(diagnosFilter).and(aldersgruppFilter);
        final boolean sjukfallangdfilterActive = !sjukskrivningslangds.isEmpty();
        final FilterPredicates sjukfallFilter = new FilterPredicates(predicate, sjukfallLengthFilter, hash, sjukfallangdfilterActive);
        final Filter filter = new Filter(sjukfallFilter, enhetsIDs, diagnoser, filterDataToReadableSjukskrivningslangdName(inFilter),
            toReadableAgeGroupNames(aldersgrupp), filterHash, filterDataToReadableIntygTypeName(inFilter),
            inFilter.isUseDefaultPeriod());
        final RangeMessageDTO rangeMessageDTO = getRange(inFilter, defaultRangeValue);
        return new FilterSettings(filter, rangeMessageDTO);
    }

    private String getHash(String filterHash, Collection<HsaIdEnhet> enhetsIDs) {
        return filterHash + enhetsIDs.stream().map(HsaIdAny::getId).collect(Collectors.joining());
    }

    private List<String> filterDataToReadableIntygTypeName(FilterData inFilter) {
        return toReadableIntygTypeName(inFilter.getIntygstyper());
    }

    private List<String> toReadableIntygTypeName(List<String> intygstyper) {
        if (intygstyper == null) {
            return IntygType.getInIntygtypFilter().stream().map(IntygType::getText).collect(Collectors.toList());
        }
        return intygstyper.stream()
            .map(intygstyp -> IntygType.parseStringOptional(intygstyp)
                .flatMap(slg -> Optional.of(slg.getText()))
                .orElse(intygstyp))
            .collect(Collectors.toList());
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

    private RangeMessageDTO getRange(FilterData inFilter, int defaultRangeValue)
        throws FilterException, TooEarlyEndDateException, TooLateStartDateException {
        if (inFilter.isUseDefaultPeriod()) {
            return new RangeMessageDTO(Range.createForLastMonthsIncludingCurrent(defaultRangeValue, clock), null);
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

            return validateAndGetFilterRangeMessage(from, to);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new FilterException("Could not parse range dates. From: " + fromDate + ", To: " + toDate, e);
        }
    }

    private RangeMessageDTO validateAndGetFilterRangeMessage(LocalDate originalFrom, LocalDate originalTo)
        throws FilterException, TooEarlyEndDateException, TooLateStartDateException {
        String message = null;
        LocalDate from = originalFrom;
        LocalDate to = originalTo;

        LocalDate highestAcceptedEndDate = LocalDate.now(clock).plusMonths(1).withDayOfMonth(1).minusDays(1);

        boolean isBefore = from.isBefore(LOWEST_ACCEPTED_START_DATE);
        boolean isAfter = to.isAfter(highestAcceptedEndDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        if (isBefore && isAfter) {
            from = LOWEST_ACCEPTED_START_DATE;
            to = highestAcceptedEndDate;

            final String formattedLowestStartDate = LOWEST_ACCEPTED_START_DATE.format(formatter);
            final String formattedHighestEndDate = highestAcceptedEndDate.format(formatter);
            message = String.format(MessagesText.FILTER_WRONG_FROM_AND_END_DATE,
                formattedLowestStartDate, formattedHighestEndDate, formattedLowestStartDate, formattedHighestEndDate);
        } else if (isBefore) {
            if (to.isBefore(LOWEST_ACCEPTED_START_DATE)) {
                throw new TooEarlyEndDateException();
            } else {
                from = LOWEST_ACCEPTED_START_DATE;
                message = String.format(MessagesText.FILTER_WRONG_FROM_DATE,
                    LOWEST_ACCEPTED_START_DATE.format(formatter));
            }
        } else if (isAfter && from.isAfter(highestAcceptedEndDate)) {
            throw new TooLateStartDateException();
        } else if (isAfter) {
            to = highestAcceptedEndDate;
            message = String.format(MessagesText.FILTER_WRONG_END_DATE,
                highestAcceptedEndDate.format(formatter));
        }

        if (to.isBefore(from)) {
            throw new FilterException("Start date must be before end date");
        }

        Range range = new Range(from.withDayOfMonth(1), to.plusMonths(1).withDayOfMonth(1).minusDays(1));

        return new RangeMessageDTO(range, Message.create(ErrorType.FILTER, ErrorSeverity.INFO, message));
    }

    private Filter getFilterForAllAvailableEnhetsRegion(HttpServletRequest request) {
        final HsaIdVardgivare vardgivarId = getSelectedVgIdForLoggedInUser(request);
        final List<HsaIdEnhet> enhets = regionEnhetHandler.getAllEnhetsForVardgivare(vardgivarId);
        return getFilterForEnhets(enhets, true);
    }

    private Filter getFilterForEnhets(@Nonnull List<HsaIdEnhet> enhetsAsHsaIds, boolean vardenhetsdepth) {
        final String hashValue = FilterPredicates.getHashValueForEnhets(enhetsAsHsaIds) + vardenhetsdepth;
        final FilterPredicates predicate = new FilterPredicates(fact ->
            enhetsAsHsaIds.contains(vardenhetsdepth ? fact.getVardenhet() : fact.getUnderenhet()),
            sjukfall -> true, hashValue, false);
        final List<String> sjukskrivningslangd = toReadableSjukskrivningslangdName(null);
        final List<String> aldersgrupp = toReadableAgeGroupNames(null);
        final List<String> intygstyper = toReadableIntygTypeName(null);
        return new Filter(predicate, enhetsAsHsaIds, null, sjukskrivningslangd, aldersgrupp, hashValue, intygstyper, true);
    }

    private Filter getFilterForAllAvailableEnhets(HttpServletRequest request, boolean vardenhetsdepth) {
        LoginInfo info = loginServiceUtil.getLoginInfo();
        final HsaIdVardgivare vgId = loginServiceUtil.getSelectedVgIdForLoggedInUser(request);
        if (info.getLoginInfoForVg(vgId).map(LoginInfoVg::isProcessledare).orElse(false)) {
            final FilterPredicates filter = SjukfallUtil.ALL_ENHETER;
            return new Filter(filter, null, null, toReadableSjukskrivningslangdName(null), toReadableAgeGroupNames(null),
                FilterPredicates.HASH_EMPTY_FILTER + vardenhetsdepth, toReadableIntygTypeName(null), true);
        }
        List<HsaIdEnhet> hsaIds = info.getBusinessesForVg(vgId).stream()
            .map(Verksamhet::getId)
            .collect(Collectors.toList());
        return getFilterForEnhets(hsaIds, vardenhetsdepth);
    }

    private ArrayList<HsaIdEnhet> getEnhetsFilteredRegion(HttpServletRequest request, FilterData inFilter) {
        Set<HsaIdEnhet> enhetsForRegion = getEnhetsForRegion(request);
        final List<String> enheter = inFilter.getEnheter();
        final HashSet<HsaIdEnhet> enhets = new HashSet<>(toHsaIds(enheter));
        if (enheter.isEmpty()) {
            return new ArrayList<>(enhetsForRegion);
        } else {
            return new ArrayList<>(Sets.intersection(enhetsForRegion, enhets));
        }
    }

    private List<HsaIdEnhet> toHsaIds(List<String> enheter) {
        return enheter.stream().map(HsaIdEnhet::new).collect(Collectors.toList());
    }

    private List<HsaIdEnhet> getEnhetsFiltered(HttpServletRequest request, FilterData inFilter) {
        Set<HsaIdEnhet> allEnheter = getEnhets(request);
        final List<String> enheter = inFilter.getEnheter();
        final HashSet<HsaIdEnhet> enhets = new HashSet<>(toHsaIds(enheter));
        if (enheter.isEmpty()) {
            return new ArrayList<>(allEnheter);
        } else {
            return new ArrayList<>(Sets.intersection(allEnheter, enhets));
        }
    }

    private Set<HsaIdEnhet> getEnhetsForRegion(HttpServletRequest request) {
        final List<Verksamhet> verksamhets = getAllVerksamhetsForLoggedInRegionsUser(request);
        Set<HsaIdEnhet> enhetsIds = new HashSet<>();
        for (Verksamhet verksamhet : verksamhets) {
            enhetsIds.add(verksamhet.getIdUnencoded());
        }
        return enhetsIds;
    }

    private Set<HsaIdEnhet> getEnhets(HttpServletRequest request) {
        Set<HsaIdEnhet> enhetsIds = new HashSet<>();
        LoginInfo info = loginServiceUtil.getLoginInfo();
        for (Verksamhet verksamhet : info.getBusinessesForVg(getSelectedVgIdForLoggedInUser(request))) {
            enhetsIds.add(verksamhet.getIdUnencoded());
        }
        return enhetsIds;
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
            if (diagnosIds.contains(diagnosKategoriString)) {
                return true;
            }
            String diagnosKodString = String.valueOf(fact.getDiagnoskod());
            return diagnosIds.contains(diagnosKodString);
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

    // When editing this make sure to also update getEnhetNameMapInclusive
    public Map<HsaIdEnhet, String> getEnhetNameMapExclusive(HttpServletRequest request, Collection<HsaIdEnhet> enhetsIDs,
        boolean vardenhetsdepth) {
        final HsaIdVardgivare vgid = getSelectedVgIdForLoggedInUser(request);
        LoginInfo info = loginServiceUtil.getLoginInfo();
        Map<HsaIdEnhet, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinessesForVg(vgid)) {
            if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId()) && userVerksamhet.isVardenhet() == vardenhetsdepth) {
                enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
            }
        }
        return enheter;
    }

    // When editing this make sure to also update getEnhetNameMapExclusive
    public Map<HsaIdEnhet, String> getEnhetNameMapInclusive(HttpServletRequest request, Collection<HsaIdEnhet> enhetsIDs,
        boolean vardenhetsdepth) {
        final HsaIdVardgivare vgid = getSelectedVgIdForLoggedInUser(request);
        LoginInfo info = loginServiceUtil.getLoginInfo();
        Map<HsaIdEnhet, String> enheter = new HashMap<>();
        for (Verksamhet userVerksamhet : info.getBusinessesForVg(vgid)) {
            if (enhetsIDs != null && enhetsIDs.contains(userVerksamhet.getId()) && (vardenhetsdepth || !userVerksamhet.isVardenhet())) {
                enheter.put(userVerksamhet.getId(), userVerksamhet.getName());
            }
        }
        return enheter;
    }

    private Map<HsaIdEnhet, String> getEnhetNameMapRegion(HttpServletRequest request, List<HsaIdEnhet> filteredEnhetsIds) {
        final HsaIdVardgivare vgid = getSelectedVgIdForLoggedInUser(request);
        Map<HsaIdEnhet, String> enheter = new HashMap<>();
        final List<HsaIdEnhet> availableEnhetIds = regionEnhetHandler.getAllEnhetsForVardgivare(vgid);
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