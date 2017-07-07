/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.error.ErrorSeverity;
import se.inera.statistics.web.error.ErrorType;
import se.inera.statistics.web.error.Message;
import se.inera.statistics.web.model.FilteredDataReport;
import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableDataReport;

public class ResponseHandler {

    private static final String NO_DATA_MESSAGE = "Ingen data tillgänglig. Det beror på att det inte finns någon data för verksamheten.";
    private static final String NO_DATA_FILTER_MESSAGE = "Det finns ingen statistik att visa för den angivna filtreringen. Överväg en "
            + "mindre restriktiv filtrering.";
    private static final String TOO_MUCH_DATA_MESSAGE = "Rapporten innehåller mycket data, vilket kan göra diagrammet svårt att läsa. "
            + "Överväg att filtrera resultatet för att minska mängden data.";

    public static final String ALL_AVAILABLE_DXS_SELECTED_IN_FILTER = "allAvailableDxsSelectedInFilter";
    public static final String ALL_AVAILABLE_ENHETS_SELECTED_IN_FILTER = "allAvailableEnhetsSelectedInFilter";
    public static final String ALL_AVAILABLE_SJUKSKRIVNINGSLANGDS_SELECTED_IN_FILTER = "allAvailableSjukskrivningslangdsSelectedInFilter";
    public static final String ALL_AVAILABLE_AGEGROUPS_SELECTED_IN_FILTER = "allAvailableAgeGroupsSelectedInFilter";
    public static final String FILTERED_ENHETS = "filteredEnhets";
    public static final int LIMIT_FOR_TOO_MUCH_DATA_MESSAGE = 100;
    public static final String MESSAGE_KEY = "messages";

    @Autowired
    private Icd10 icd10;

    @Autowired
    private Warehouse warehouse;

    Response getResponse(TableDataReport result, String format, List<HsaIdEnhet> availableEnhetsForUser, Report report) {
        if ("xlsx".equalsIgnoreCase(format)) {
            return getXlsx(result, availableEnhetsForUser, report);
        }
        if ("csv".equalsIgnoreCase(format)) {
            return CsvConverter.getCsvResponse(result.getTableData(), getFilename(report, "csv"));
        }
        return getResponseForDataReport(result, availableEnhetsForUser);
    }

    Response getXlsx(TableDataReport result, List<HsaIdEnhet> availableEnhetsForUser, Report report) {
        return new XlsxConverter(icd10).getXlsxResponse(result,
                getFilename(report, "xlsx"),
                getFilterSelections(result, availableEnhetsForUser), report);
    }

    private String getFilename(Report report, String fileExtension) {
        return report.getStatisticsLevel().getText()
                + "_"
                + report.getShortName()
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYMMdd_HHmmss"))
                + "."
                + fileExtension;
    }

    Response getResponseForDataReport(FilteredDataReport result, List<HsaIdEnhet> availableEnhetsForUser) {
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
            if (containsMoreDataThanLimit(detailReport, LIMIT_FOR_TOO_MUCH_DATA_MESSAGE)) {
                Message message = Message.create(ErrorType.UNSET, ErrorSeverity.INFO, TOO_MUCH_DATA_MESSAGE);
                messages.add(message);
            }
        }

        if (messages.isEmpty() && result != null && result.isEmpty()) {
            if (filterActive(result.getFilter(), filterSelections)) {
                messages.add(Message.create(ErrorType.FILTER, ErrorSeverity.WARN, NO_DATA_FILTER_MESSAGE));
            } else {
                messages.add(Message.create(ErrorType.UNSET, ErrorSeverity.WARN, NO_DATA_MESSAGE));
            }
        }

        mappedResult.put(MESSAGE_KEY, messages);

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

        final List<String> enhetNames = allAvailableEnhetsSelectedInFilter ? getEnhetNames(availableEnhetsForUser)
                : getEnhetNamesFromFilter(result.getFilter());

        return new FilterSelections(allAvailableDxsSelectedInFilter, allAvailableEnhetsSelectedInFilter,
                allAvailableSjukskrivningslangdsSelectedInFilter, allAvailableAgeGroupsSelectedInFilter, enhetNames);
    }

    private boolean filterActive(FilterDataResponse filter, FilterSelections filterSelections) {
        if (filter == null || filterSelections == null) {
            return false;
        }

        boolean aldersGrupp = filterSelections.isAllAvailableAgeGroupsSelectedInFilter() || filter.getAldersgrupp().isEmpty();
        boolean dxs = filterSelections.isAllAvailableDxsSelectedInFilter() || filter.getDiagnoser().isEmpty();
        boolean enhets = filterSelections.isAllAvailableEnhetsSelectedInFilter() || filter.getEnheter().isEmpty();
        boolean sjukskrivningslangd = filterSelections.isAllAvailableSjukskrivningslangdsSelectedInFilter()
                || filter.getSjukskrivningslangd().isEmpty();

        return !(aldersGrupp && dxs && enhets && sjukskrivningslangd);
    }

    private boolean containsMoreDataThanLimit(TableDataReport detailReport, int limitForTooMuchDataMessage) {
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

        return rows.size() >= limitForTooMuchDataMessage;
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
        final List<Enhet> enhets = warehouse.getEnhetsWithHsaId(enhetIds);
        final Map<String, List<Enhet>> enhetsByName = enhets.stream().collect(Collectors.groupingBy(Enhet::getNamn));
        return enhetsByName.entrySet().stream()
                .map(entry -> entry.getValue().size() > 1
                        ? entry.getValue().stream().map(e2 -> e2.getNamn() + " " + e2.getEnhetId()).collect(Collectors.toList())
                        : Collections.singletonList(entry.getKey()))
                .flatMap(List::stream).sorted().collect(Collectors.toList());
    }

}
