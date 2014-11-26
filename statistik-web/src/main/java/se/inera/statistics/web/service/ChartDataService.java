/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.web.service;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.NationellOverviewData;
import se.inera.statistics.web.model.AgeGroupsData;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SickLeaveLengthData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.overview.OverviewData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics services that does not require authentication. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv variants
 * only contains one data set.
 *
 * Csv files are semicolon separated, otherwise json is used.
 */
@Service("chartService")
public class ChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);
    public static final int YEAR = 12;
    public static final String TEXT_UTF8 = "text/plain; charset=UTF-8";
    public static final int DELAY_BETWEEN_RELOADS = 1000;
    public static final int EIGHTEEN_MONTHS = 18;

    @Autowired
    private NationellData data;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private NationellOverviewData overviewData;

    private volatile SimpleDetailsData numberOfCasesPerMonth;
    private volatile DualSexStatisticsData diagnosgrupper;
    private volatile Map<String, DualSexStatisticsData> diagnoskapitel = new HashMap<>();
    private volatile OverviewData overview;
    private volatile AgeGroupsData aldersgrupper;
    private volatile DualSexStatisticsData sjukskrivningsgrad;
    private volatile SickLeaveLengthData sjukfallslangd;
    private volatile CasesPerCountyData sjukfallPerLan;
    private volatile SimpleDetailsData konsfordelningPerLan;
    private LocalDateTime lastUpdated;

    @Scheduled(fixedDelay = DELAY_BETWEEN_RELOADS)
    public synchronized void buildCache() {
        LocalDateTime last = data.getLastUpdate();
        if (last != null && !last.equals(lastUpdated)) {
            LOG.info("New warehouse timestamp '{}', populating national cache", last);
            buildNumberOfCasesPerMonth();
            buildDiagnosgrupper();
            buildDiagnoskapitel();
            buildAldersgrupper();
            buildSjukskrivningsgrad();
            buildSjukfallslangd();
            buildKonsfordelningPerLan();
            buildSjukfallPerLan();
            buildOverview();
            lastUpdated = last;
            LOG.info("National cache populated");
        }
    }
    public void buildNumberOfCasesPerMonth() {
        final Range range = new Range(EIGHTEEN_MONTHS);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = data.getCasesPerMonth(range);
        numberOfCasesPerMonth = new SimpleDualSexConverter().convert(casesPerMonth, range);
    }

    public void buildDiagnosgrupper() {
        Range range = new Range(EIGHTEEN_MONTHS);
        DiagnosgruppResponse diagnosisGroups = data.getDiagnosgrupper(range);
        diagnosgrupper = new DiagnosisGroupsConverter().convert(diagnosisGroups, range);
    }

    public void buildDiagnoskapitel() {
        final Range range = new Range(EIGHTEEN_MONTHS);
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            String id = kapitel.getId();
            DiagnosgruppResponse diagnosisGroups = data.getDiagnosavsnitt(range, id);
            diagnoskapitel.put(id, new DiagnosisSubGroupsConverter().convert(diagnosisGroups, range));
        }
    }

    public void buildOverview() {
        Range range = Range.quarter();
        OverviewResponse response = overviewData.getOverview(range);
        overview = new OverviewConverter().convert(response, range);

    }

    public void buildAldersgrupper() {
        Range range = new Range(YEAR);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = data.getHistoricalAgeGroups(range);
        aldersgrupper = new AgeGroupsConverter().convert(ageGroups, new Range(range.getMonths()));
    }

    public void buildSjukskrivningsgrad() {
        final Range range = new Range(EIGHTEEN_MONTHS);
        SjukskrivningsgradResponse degreeOfSickLeaveStatistics = data.getSjukskrivningsgrad(range);
        sjukskrivningsgrad = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range);
    }

    public void buildSjukfallslangd() {
        Range range = new Range(YEAR);
        SjukfallslangdResponse sickLeaveLength = data.getSjukfallslangd(range);
        sjukfallslangd =  new SickLeaveLengthConverter().convert(sickLeaveLength, range);
    }

    public void buildSjukfallPerLan() {
        Range range1 = Range.quarter();
        Range range2 = ReportUtil.getPreviousPeriod(range1);

        SimpleKonResponse<SimpleKonDataRow> countyStatRange1 = data.getSjukfallPerLan(range1);
        SimpleKonResponse<SimpleKonDataRow> countyStatRange2 = data.getSjukfallPerLan(range2);
        sjukfallPerLan = new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

    public void buildKonsfordelningPerLan() {
        final Range range = new Range(YEAR);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = data.getSjukfallPerLan(range);
        konsfordelningPerLan = new SjukfallPerSexConverter().convert(casesPerMonth, range);
    }

    /**
     * Get sjukfall per manad.
     *
     * @return data
     */
    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({MediaType.APPLICATION_JSON })
    public SimpleDetailsData getNumberOfCasesPerMonth() {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        return numberOfCasesPerMonth;
    }

    /**
     * Get sjukfall per manad.
     *
     * @return data
     */
    @GET
    @Path("getNumberOfCasesPerMonth/csv")
    @Produces({TEXT_UTF8 })
    public Response getNumberOfCasesPerMonthAsCsv() {
        LOG.info("Calling getNumberOfCasesPerMonthAsCsv for national");
        final TableData tableData = getNumberOfCasesPerMonth().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get the list of diagnoskapitel.
     *
     * @return data
     */
    @GET
    @Path("getDiagnoskapitel")
    @Produces({MediaType.APPLICATION_JSON })
    public List<Kapitel> getDiagnoskapitel() {
        LOG.info("Calling getKapitel");
        List<Kapitel> kapitel = new ArrayList<>();
        for (Icd10.Kapitel k: icd10.getKapitel()) {
            String s =  k.getId();
            if (s.charAt(0) <= 'Z') {
                kapitel.add(new Kapitel(s, k.getName()));
            }
        }
        return kapitel;
    }

    /**
     * Get the list of diagnoskapitel.
     *
     * @return data
     */
    @GET
    @Path("getDiagnosisKapitelAndAvsnitt")
    @Produces({MediaType.APPLICATION_JSON })
    public DiagnosisKapitelAndAvsnittResponse getDiagnosisKapitelAndAvsnitt() {
        LOG.info("Calling getDiagnosisKapitelAndAvsnitt");
        Map<String, List<Avsnitt>> avsnitts = new LinkedHashMap<>();
        List<Icd10.Kapitel> kapitels = icd10.getKapitel();
        for (Icd10.Kapitel k: kapitels) {
            avsnitts.put(k.getId(), convertToAvsnitts(k.getAvsnitt()));
        }
        return new DiagnosisKapitelAndAvsnittResponse(avsnitts, getDiagnoskapitel());
    }

    private List<Avsnitt> convertToAvsnitts(List<Icd10.Avsnitt> avsnitts) {
        List<Avsnitt> converted = new ArrayList<>(avsnitts.size());
        for (Icd10.Avsnitt avsnitt : avsnitts) {
            converted.add(new Avsnitt(avsnitt.getId(), avsnitt.getName()));
        }
        return converted;
    }

    /**
     * Get sjukfall per diagnoskapitel and per diagnosgrupp. The chart data is grouped by diagnosgrupp,
     * the table data by diagnoskapitel. Diagnosgrupp is a diagnoskapitel or a list of diagnoskapitel.
     * Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getDiagnoskapitelstatistik")
    @Produces({MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnoskapitelstatistik() {
        LOG.info("Calling getDiagnoskapitelstatistik for national");
        return diagnosgrupper;
    }

    /**
     * Get sjukfall per diagnoskapitel.
     * Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getDiagnoskapitelstatistik/csv")
    @Produces({TEXT_UTF8 })
    public Response getDiagnoskapitelstatistikAsCsv() {
        LOG.info("Calling getDiagnoskapitelstatistikAsCsv for national");
        final TableData tableData = getDiagnoskapitelstatistik().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     *
     * @param groupId diagnosgruppid
     * @return data
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}")
    @Produces({MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDiagnosavsnittstatistik(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosavsnittstatistik for national with groupId: " + groupId);
        return diagnoskapitel.get(groupId);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel. Csv formatted.
     *
     * @param groupId diagnosgruppid
     * @return data
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}/csv")
    @Produces({TEXT_UTF8 })
    public Response getDiagnosavsnittstatistikAsCsv(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv for national");
        final TableData tableData = getDiagnosavsnittstatistik(groupId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get overview. Sex distribution, change comparing lastUpdated three months to previous three months,
     * top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad, lan. Only chart formatted data.
     *
     * @return data
     */
    @GET
    @Path("getOverview")
    @Produces({MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        return overview;
    }

    /**
     * Get sjukfall grouped by age and sex.
     *
     * @return data
     */
    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    public AgeGroupsData getAgeGroupsStatistics() {
        LOG.info("Calling getAgeGroupsStatistics for national");
        return aldersgrupper;
    }

    /**
     * Get sjukfall grouped by age and sex. Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getAgeGroupsStatistics/csv")
    @Produces({TEXT_UTF8 })
    public Response getAgeGroupsStatisticsAsCsv() {
        LOG.info("Calling getAgeGroupsStatisticsAsCsv for national");
        final TableData tableData = getAgeGroupsStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     *
     * @return data
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    public DualSexStatisticsData getDegreeOfSickLeaveStatistics() {
        LOG.info("Calling getDegreeOfSickLeaveStatistics for national");
        return sjukskrivningsgrad;
    }

    /**
     * Get sjukskrivningsgrad per calendar month. Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics/csv")
    @Produces({TEXT_UTF8 })
    public Response getDegreeOfSickLeaveStatisticsAsCsv() {
        LOG.info("Calling getDegreeOfSickLeaveStatisticsAsCsv for national");
        final TableData tableData = getDegreeOfSickLeaveStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfallslangd (grouped).
     *
     * @return data
     */
    @GET
    @Path("getSickLeaveLengthData")
    @Produces({MediaType.APPLICATION_JSON })
    public SickLeaveLengthData getSickLeaveLengthData() {
        LOG.info("Calling getSickLeaveLengthData for national");
        return sjukfallslangd;
    }

    /**
     * Get sjukfallslangd (grouped). Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getSickLeaveLengthData/csv")
    @Produces({TEXT_UTF8 })
    public Response getSickLeaveLengthDataAsCsv() {
        LOG.info("Calling getSickLeaveLengthDataAsCsv for national");
        final TableData tableData = getSickLeaveLengthData().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfall per lan.
     *
     * @return data
     */
    @GET
    @Path("getCountyStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    public CasesPerCountyData getCountyStatistics() {
        return sjukfallPerLan;
    }

    /**
     * Get sjukfall per lan.
     *
     * @return data
     */
    @GET
    @Path("getCountyStatistics/csv")
    @Produces({TEXT_UTF8 })
    public Response getCountyStatisticsAsCsv() {
        LOG.info("Calling getCountyStatisticsAsCsv for national");
        final TableData tableData = getCountyStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get sjukfall per sex.
     *
     * @return data
     */
    @GET
    @Path("getSjukfallPerSexStatistics")
    @Produces({MediaType.APPLICATION_JSON })
    public SimpleDetailsData getSjukfallPerSexStatistics() {
        LOG.info("Calling getSjukfallPerSexStatistics for national");
        return konsfordelningPerLan;
    }

    /**
     * Get sjukfall per sex. Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getSjukfallPerSexStatistics/csv")
    @Produces({TEXT_UTF8 })
    public Response getSjukfallPerSexStatisticsAsCsv() {
        LOG.info("Calling getSjukfallPerSexStatisticsAsCsv for national");
        final TableData tableData = getSjukfallPerSexStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    public static class Kapitel {
        private final String id;
        private final String name;

        public Kapitel(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class Avsnitt extends Kapitel {
        public Avsnitt(String id, String name) {
            super(id, name);
        }
    }

    public static class DiagnosisKapitelAndAvsnittResponse {
        private final Map<String, List<Avsnitt>> avsnitts;
        private final List<Kapitel> kapitels;

        public DiagnosisKapitelAndAvsnittResponse(Map<String, List<Avsnitt>> avsnitts, List<Kapitel> kapitels) {
            this.avsnitts = avsnitts;
            this.kapitels = kapitels;
        }

        public Map<String, List<Avsnitt>> getAvsnitts() {
            return avsnitts;
        }

        public List<Kapitel> getKapitels() {
            return kapitels;
        }

    }

}
