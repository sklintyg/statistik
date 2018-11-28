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
package se.inera.statistics.web.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.report.util.AgeGroup;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.SjukfallsLangdGroup;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.MessagesText;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.FilteredDataReport;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableDataReport;

public class ResponseHandler {

    public static final int LIMIT_TOO_MUCH_DATA_MESSAGE_SINGLE_CHART = 100;
    public static final int LIMIT_TOO_MUCH_DATA_MESSAGE_DUAL_CHART = 50;
    public static final String MESSAGE_KEY = "messages";

    @Autowired
    private Icd10 icd10;

    @Autowired
    private Warehouse warehouse;

    public Response getResponse(TableDataReport result, String format, List<HsaIdEnhet> availableEnhetsForUser, ReportInfo report) {
        return getResponse(result, format, availableEnhetsForUser, report, null);
    }

    public Response getResponse(TableDataReport result, String format, List<HsaIdEnhet> availableEnhetsForUser,
                         ReportInfo report, Map<String, Object> extras) {
        if ("xlsx".equalsIgnoreCase(format)) {
            return getXlsx(result, availableEnhetsForUser, report.getReport());
        }
        if ("csv".equalsIgnoreCase(format)) {
            return CsvConverter.getCsvResponse(result.getTableData(), getFilename(report.getReport(), "csv"));
        }
        return getResponseForDataReport(result, availableEnhetsForUser, extras, report);
    }

    public Response getXlsx(TableDataReport result, List<HsaIdEnhet> availableEnhetsForUser, Report report) {
        return new XlsxConverter(icd10).getXlsxResponse(result,
                getFilename(report, "xlsx"),
                getFilterSelections(result, availableEnhetsForUser), report);
    }

    private String getFilename(Report report, String fileExtension) {
        final String filename = report.getStatisticsLevel().getText()
                + "_"
                + report.getShortName()
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYMMdd_HHmmss"))
                + "."
                + fileExtension;
        return filename.replaceAll("\\s", "");
    }

    public Response getResponseForDataReport(FilteredDataReport result, List<HsaIdEnhet> availableEnhetsForUser) {
        return getResponseForDataReport(result, availableEnhetsForUser, null, null);
    }

    private Response getResponseForDataReport(FilteredDataReport result, List<HsaIdEnhet> availableEnhetsForUser,
                                              Map<String, Object> extras, ReportInfo report) {
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> mappedResult = result != null ? mapper.convertValue(result, Map.class) : Maps.newHashMap();

        FilterSelections filterSelections = getFilterSelections(result, availableEnhetsForUser);
        @SuppressWarnings("unchecked")
        final Map<String, Object> filterSelectionMap = mapper.convertValue(filterSelections, Map.class);
        mappedResult.putAll(filterSelectionMap);

        List<Message> oldMessages = result != null ? result.getMessages() : null;

        List<Message> messages = new ArrayList<>();
        if (oldMessages != null) {
            messages.addAll(oldMessages);
        }

        if (result instanceof TableDataReport) {
            final TableDataReport detailReport = (TableDataReport) result;
            if (containsMoreDataThanLimit(detailReport, report)) {
                Message message = Message.create(ErrorType.CHART, ErrorSeverity.INFO, MessagesText.MESSAGE_TOO_MUCH_DATA);
                messages.add(message);
            }
        }

        if (result != null && result.isEmpty()) {
            if (filterActive(result.getFilter(), filterSelections)) {
                messages.add(Message.create(ErrorType.FILTER, ErrorSeverity.WARN, MessagesText.MESSAGE_NO_DATA_FILTER));
            } else {
                messages.add(Message.create(ErrorType.UNSET, ErrorSeverity.INFO, MessagesText.MESSAGE_NO_DATA));
            }
        }

        mappedResult.put(MESSAGE_KEY, messages);

        if (extras != null) {
            mappedResult.putAll(extras);
        }

        return Response.ok(mappedResult).build();
    }

    private FilterSelections getFilterSelections(FilteredDataReport result, List<HsaIdEnhet> availableEnhetsForUser) {
        final boolean allAvailableDxsSelectedInFilter = result == null || areAllAvailableDxsSelectedInFilter(result.getFilter());

        final boolean allAvailableEnhetsSelectedInFilter = result == null
                || areAllAvailableEnhetsSelectedInFilter(result.getFilter(), availableEnhetsForUser);

        final boolean allAvailableSjukskrivningslangdsSelectedInFilter = result == null
                || areAllAvailableSjukskrivningslangdsSelectedInFilter(result.getFilter());

        final boolean allAvailableAgeGroupsSelectedInFilter = result == null
                || areAllAvailableAgeGroupsSelectedInFilter(result.getFilter());

        final boolean allAvailableIntygTypesSelectedInFilter = result == null
                || areAllAvailableIntygTypesSelectedInFilter(result.getFilter());

        final List<String> enhetNames = allAvailableEnhetsSelectedInFilter ? getEnhetNames(availableEnhetsForUser)
                : getEnhetNamesFromFilter(result.getFilter());

        return new FilterSelections(allAvailableDxsSelectedInFilter, allAvailableEnhetsSelectedInFilter,
                allAvailableSjukskrivningslangdsSelectedInFilter, allAvailableAgeGroupsSelectedInFilter,
                allAvailableIntygTypesSelectedInFilter, enhetNames);
    }

    private boolean filterActive(FilterDataResponse filter, FilterSelections filterSelections) {
        if (filter == null || filterSelections == null) {
            return false;
        }

        boolean useDefaultPeriod = filter.isUseDefaultPeriod();
        boolean aldersGrupp = filterSelections.isAllAvailableAgeGroupsSelectedInFilter() || filter.getAldersgrupp().isEmpty();
        boolean dxs = filterSelections.isAllAvailableDxsSelectedInFilter() || filter.getDiagnoser().isEmpty();
        boolean enhets = filterSelections.isAllAvailableEnhetsSelectedInFilter() || filter.getEnheter().isEmpty();
        boolean sjukskrivningslangd = filterSelections.isAllAvailableSjukskrivningslangdsSelectedInFilter()
                || filter.getSjukskrivningslangd().isEmpty();
        boolean intystyper = filterSelections.isAllAvailableIntygTypesSelectedInFilter()
                || filter.getIntygstyper().isEmpty();

        return !(useDefaultPeriod && aldersGrupp && dxs && enhets && sjukskrivningslangd && intystyper);
    }

    private boolean containsMoreDataThanLimit(TableDataReport detailReport, ReportInfo report) {
        if (detailReport == null) {
            return false;
        }

        final TableData tableData = detailReport.getTableData();
        if (tableData == null) {
            return false;
        }

        final List<NamedData> rows = tableData.getRows();
        if (rows == null) {
            return false;
        }

        if (report == null) {
            return false;
        }

        final int limit = report.isDualChartReport() ? LIMIT_TOO_MUCH_DATA_MESSAGE_DUAL_CHART : LIMIT_TOO_MUCH_DATA_MESSAGE_SINGLE_CHART;
        return rows.size() >= limit;
    }

    private boolean areAllAvailableEnhetsSelectedInFilter(FilterDataResponse filter, List<HsaIdEnhet> availableEnhetsForUser) {
        if (filter == null) {
            return true;
        }
        final List<String> enheter = filter.getEnheter();
        if (enheter == null) {
            return true;
        }
        final List<String> enhetsFilter = enheter.stream().sorted().collect(Collectors.toList());
        final List<String> availableEnhetIds = availableEnhetsForUser.stream().map(HsaIdEnhet::getId).sorted().collect(Collectors.toList());
        return availableEnhetIds.equals(enhetsFilter);
    }

    private boolean areAllAvailableSjukskrivningslangdsSelectedInFilter(FilterDataResponse filter) {
        if (filter == null) {
            return true;
        }
        final List<String> sjukskrivningslangds = filter.getSjukskrivningslangd();
        if (sjukskrivningslangds == null) {
            return true;
        }
        if (new HashSet<>(sjukskrivningslangds).size() != SjukfallsLangdGroup.values().length) {
            return false;
        }
        return sjukskrivningslangds.stream().allMatch(s -> SjukfallsLangdGroup.getByName(s).isPresent());
    }

    private boolean areAllAvailableAgeGroupsSelectedInFilter(FilterDataResponse filter) {
        if (filter == null) {
            return true;
        }
        final List<String> aldersgrupp = filter.getAldersgrupp();
        if (aldersgrupp == null) {
            return true;
        }
        if (new HashSet<>(aldersgrupp).size() != AgeGroup.values().length) {
            return false;
        }
        return aldersgrupp.stream().allMatch(s -> AgeGroup.getByName(s).isPresent());
    }

    private boolean areAllAvailableIntygTypesSelectedInFilter(FilterDataResponse filter) {
        if (filter == null) {
            return true;
        }
        final List<String> intygstyper = filter.getIntygstyper();
        if (intygstyper == null) {
            return true;
        }
        if (new HashSet<>(intygstyper).size() != IntygType.getInIntygtypFilter().size()) {
            return false;
        }
        return intygstyper.stream().allMatch(s -> IntygType.getByName(s).isPresent());
    }

    private boolean areAllAvailableDxsSelectedInFilter(FilterDataResponse filter) {
        if (filter == null) {
            return true;
        }
        final List<String> diagnoser = filter.getDiagnoser();
        if (diagnoser == null || diagnoser.isEmpty()) {
            return true;
        }
        final List<String> dxFilter = Lists.newArrayList(diagnoser);
        final List<String> topLevelIcdCodes = Lists
                .newArrayList(Lists.transform(icd10.getIcdStructure(), icd -> String.valueOf(icd.getNumericalId())));
        dxFilter.sort(String.CASE_INSENSITIVE_ORDER);
        topLevelIcdCodes.sort(String.CASE_INSENSITIVE_ORDER);

        return topLevelIcdCodes.equals(dxFilter);
    }

    private List<String> getEnhetNamesFromFilter(FilterDataResponse filter) {
        if (filter == null || filter.getEnheter() == null) {
            return null;
        }
        final List<HsaIdEnhet> enhetIds = filter.getEnheter().stream().map(HsaIdEnhet::new).collect(Collectors.toList());
        return getEnhetNames(enhetIds);
    }

    private List<String> getEnhetNames(Collection<HsaIdEnhet> enhetIds) {
        if (enhetIds == null) {
            return new ArrayList<>();
        }

        final Collection<Enhet> enhets = warehouse.getEnhetsWithHsaId(enhetIds);

        final Map<String, List<Enhet>> enhetsByName = enhets.stream().collect(Collectors.groupingBy(Enhet::getNamn));

        List<String> enhetNames = enhetsByName.entrySet().stream()
                .map(entry -> entry.getValue().size() > 1
                        ? entry.getValue().stream().map(e2 -> e2.getNamn() + " " + e2.getEnhetId()).collect(Collectors.toList())
                        : Collections.singletonList(entry.getKey()))
                .flatMap(List::stream)
                .collect(Collectors.toList());


        // Add enheter without name
        Set<HsaIdEnhet> foundEnhetsIds = enhets.stream().map(Enhet::getEnhetId).collect(Collectors.toSet());
        Set<HsaIdEnhet> noNameFound = new HashSet<>(enhetIds);
        noNameFound.removeAll(foundEnhetsIds);

        return Stream.concat(
                    enhetNames.stream(),
                    noNameFound.stream().map(HsaIdEnhet::getId)
                )
                .sorted()
                .collect(Collectors.toList());
    }

}
