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

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.report.api.Aldersgrupp;
import se.inera.statistics.service.report.api.Diagnoskapitel;
import se.inera.statistics.service.report.api.Overview;
import se.inera.statistics.service.report.api.RollingLength;
import se.inera.statistics.service.report.api.SjukfallPerLan;
import se.inera.statistics.service.report.api.SjukfallPerManad;
import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.util.DiagnosUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;
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
import java.util.List;

/**
 * Statistics services that does not require authentication. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv variants
 * only contains one data set.
 *
 * Csv files are semicolon separated, otherwise json is used.
 */
@Service("chartService")
public class ChartDataService {

    private static final String NATIONELL = Verksamhet.NATIONELL.toString();

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);

    @Autowired
    private Overview datasourceOverview;
    @Autowired
    private SjukfallPerManad datasourceSjukfallPerManad;
    @Autowired
    private se.inera.statistics.service.report.api.Diagnosgrupp datasourceDiagnosgrupp;
    @Autowired
    private Diagnoskapitel datasourceDiagnoskapitel;
    @Autowired
    private Aldersgrupp datasourceAldersgrupp;
    @Autowired
    private Sjukskrivningsgrad datasourceSjukskrivningsgrad;
    @Autowired
    private SjukfallslangdGrupp datasourceSickLeaveLength;
    @Autowired
    private SjukfallPerLan datasourceSjukfallPerLan;

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
        final Range range = new Range(18);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = datasourceSjukfallPerManad.getCasesPerMonth(NATIONELL, range);
        return new SimpleDualSexConverter().convert(casesPerMonth, range);
    }

    /**
     * Get sjukfall per manad.
     *
     * @return data
     */
    @GET
    @Path("getNumberOfCasesPerMonth/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
    public List<Avsnitt> getDiagnoskapitel() {
        LOG.info("Calling getKapitel");
        return DiagnosUtil.getKapitel();
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
        final Range range = new Range(18);
        DiagnosgruppResponse diagnosisGroups = datasourceDiagnosgrupp.getDiagnosisGroups(NATIONELL, range);
        return new DiagnosisGroupsConverter().convert(diagnosisGroups, range);
    }

    /**
     * Get sjukfall per diagnoskapitel.
     * Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getDiagnoskapitelstatistik/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
        final Range range = new Range(18);
        DiagnosgruppResponse diagnosisGroups = datasourceDiagnoskapitel.getDiagnosisGroups(NATIONELL, range, groupId);
        return new DiagnosisSubGroupsConverter().convert(diagnosisGroups, range);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel. Csv formatted.
     *
     * @param groupId diagnosgruppid
     * @return data
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}/csv")
    @Produces({"text/plain; charset=UTF-8" })
    public Response getDiagnosavsnittstatistikAsCsv(@PathParam("groupId") String groupId) {
        LOG.info("Calling getDiagnosavsnittstatistikAsCsv for national");
        final TableData tableData = getDiagnosavsnittstatistik(groupId).getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    /**
     * Get overview. Sex distribution, change comparing last three months to previous three months,
     * top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad, lan. Only chart formatted data.
     *
     * @return data
     */
    @GET
    @Path("getOverview")
    @Produces({MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        Range range = Range.quarter();
        OverviewResponse response = datasourceOverview.getOverview(range);
        return new OverviewConverter().convert(response, range);
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
        final RollingLength period = RollingLength.YEAR;
        SimpleKonResponse<SimpleKonDataRow> ageGroups = datasourceAldersgrupp.getHistoricalAgeGroups(NATIONELL, previousMonth(), period);
        return new AgeGroupsConverter().convert(ageGroups, new Range(period.getPeriods()));
    }

    /**
     * Get sjukfall grouped by age and sex. Csv formatted.
     *
     * @return
     */
    @GET
    @Path("getAgeGroupsStatistics/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
        final Range range = new Range(18);
        SjukskrivningsgradResponse degreeOfSickLeaveStatistics = datasourceSjukskrivningsgrad.getStatistics(NATIONELL, range);
        return new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, range);
    }

    /**
     * Get sjukskrivningsgrad per calendar month. Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
        final RollingLength period = RollingLength.YEAR;
        SjukfallslangdResponse sickLeaveLength = datasourceSickLeaveLength.getHistoricalStatistics(NATIONELL, previousMonth(), period);
        return new SickLeaveLengthConverter().convert(sickLeaveLength, new Range(period.getPeriods()));
    }

    /**
     * Get sjukfallslangd (grouped). Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getSickLeaveLengthData/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
        Range range1 = Range.quarter();
        Range range2 = ReportUtil.getPreviousPeriod(range1);

        SimpleKonResponse<SimpleKonDataRow> countyStatRange1 = datasourceSjukfallPerLan.getStatistics(range1);
        SimpleKonResponse<SimpleKonDataRow> countyStatRange2 = datasourceSjukfallPerLan.getStatistics(range2);
        return new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

    /**
     * Get sjukfall per lan.
     *
     * @return data
     */
    @GET
    @Path("getCountyStatistics/csv")
    @Produces({"text/plain; charset=UTF-8" })
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
        final Range range = new Range(12);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = datasourceSjukfallPerLan.getStatistics(range);
        return new SjukfallPerSexConverter().convert(casesPerMonth, range);
    }

    /**
     * Get sjukfall per sex. Csv formatted.
     *
     * @return data
     */
    @GET
    @Path("getSjukfallPerSexStatistics/csv")
    @Produces({"text/plain; charset=UTF-8" })
    public Response getSjukfallPerSexStatisticsAsCsv() {
        LOG.info("Calling getSjukfallPerSexStatisticsAsCsv for national");
        final TableData tableData = getSjukfallPerSexStatistics().getTableData();
        return CsvConverter.getCsvResponse(tableData, "export.csv");
    }

    private LocalDate previousMonth() {
        return new LocalDate().withDayOfMonth(1).minusMonths(1);
    }

}
