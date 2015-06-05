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
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.NationellOverviewData;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.overview.OverviewData;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
 * <p/>
 * Csv files are semicolon separated, otherwise json is used.
 */
@Service("chartService")
public class ChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);
    public static final int YEAR = 12;
    public static final String TEXT_CP1252 = "text/plain; charset=cp1252";
    public static final int DELAY_BETWEEN_RELOADS = 30 * 60 * 1000;
    public static final int EIGHTEEN_MONTHS = 18;

    @Autowired
    private NationellData data;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private NationellOverviewData overviewData;

    @Autowired
    private FilterHashHandler filterHashHandler;

    private volatile SimpleDetailsData numberOfCasesPerMonth;
    private volatile DualSexStatisticsData diagnosgrupper;
    private volatile Map<String, DualSexStatisticsData> diagnoskapitel = new HashMap<>();
    private volatile OverviewData overview;
    private volatile SimpleDetailsData aldersgrupper;
    private volatile DualSexStatisticsData sjukskrivningsgrad;
    private volatile SimpleDetailsData sjukfallslangd;
    private volatile CasesPerCountyData sjukfallPerLan;
    private volatile SimpleDetailsData konsfordelningPerLan;
    private LocalDateTime lastUpdated;

    @Scheduled(fixedDelay = DELAY_BETWEEN_RELOADS)
    public synchronized void buildCache() {
        LocalDateTime last = data.getLastUpdate();
        if (last != null && !last.equals(lastUpdated)) {
            LOG.info("New warehouse timestamp '{}', populating national cache", last);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            buildNumberOfCasesPerMonth();
            stopWatch.stop();
            LOG.info("National cache buildNumberOfCasesPerMonth " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildDiagnosgrupper();
            stopWatch.stop();
            LOG.info("National cache buildDiagnosgrupper " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildDiagnoskapitel();
            stopWatch.stop();
            LOG.info("National cache buildDiagnoskapitel " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildAldersgrupper();
            stopWatch.stop();
            LOG.info("National cache buildAldersgrupper " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildSjukskrivningsgrad();
            stopWatch.stop();
            LOG.info("National cache buildSjukskrivningsgrad " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildSjukfallslangd();
            stopWatch.stop();
            LOG.info("National cache buildSjukfallslangd " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildKonsfordelningPerLan();
            stopWatch.stop();
            LOG.info("National cache buildKonsfordelningPerLan " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildSjukfallPerLan();
            stopWatch.stop();
            LOG.info("National cache buildSjukfallPerLan " + stopWatch.getTotalTimeMillis());
            stopWatch.start();
            buildOverview();
            stopWatch.stop();
            LOG.info("National cache buildOverview  " + stopWatch.getTotalTimeMillis());
            lastUpdated = last;
            LOG.info("National cache populated");
        }
    }

    public void buildNumberOfCasesPerMonth() {
        final Range range = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = data.getCasesPerMonth(range);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        numberOfCasesPerMonth = new PeriodConverter().convert(casesPerMonth, filterSettings);
    }

    public void buildDiagnosgrupper() {
        Range range = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS);
        DiagnosgruppResponse diagnosisGroups = data.getDiagnosgrupper(range);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        diagnosgrupper = new DiagnosisGroupsConverter().convert(diagnosisGroups, filterSettings);
    }

    public void buildDiagnoskapitel() {
        final Range range = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS);
        for (Icd10.Kapitel kapitel : icd10.getKapitel(false)) {
            String id = kapitel.getId();
            DiagnosgruppResponse diagnosisGroups = data.getDiagnosavsnitt(range, id);
            final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
            diagnoskapitel.put(id, new DiagnosisSubGroupsConverter().convert(diagnosisGroups, filterSettings));
        }
    }

    public void buildOverview() {
        Range range = Range.quarter();
        OverviewResponse response = overviewData.getOverview(range);
        overview = new OverviewConverter().convert(response, range);

    }

    public void buildAldersgrupper() {
        Range range = Range.createForLastMonthsExcludingCurrent(YEAR);
        SimpleKonResponse<SimpleKonDataRow> ageGroups = data.getHistoricalAgeGroups(range);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), Range.createForLastMonthsExcludingCurrent(range.getMonths()));
        aldersgrupper = new AgeGroupsConverter().convert(ageGroups, filterSettings);
    }

    public void buildSjukskrivningsgrad() {
        final Range range = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS);
        KonDataResponse degreeOfSickLeaveStatistics = data.getSjukskrivningsgrad(range);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        sjukskrivningsgrad = new DegreeOfSickLeaveConverter().convert(degreeOfSickLeaveStatistics, filterSettings);
    }

    public void buildSjukfallslangd() {
        Range range = Range.createForLastMonthsExcludingCurrent(YEAR);
        SimpleKonResponse<SimpleKonDataRow> sickLeaveLength = data.getSjukfallslangd(range);
        final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
        sjukfallslangd = new SickLeaveLengthConverter().convert(sickLeaveLength, filterSettings);
    }

    public void buildSjukfallPerLan() {
        Range range1 = Range.quarter();
        Range range2 = ReportUtil.getPreviousPeriod(range1);

        SimpleKonResponse<SimpleKonDataRow> countyStatRange1 = data.getSjukfallPerLan(range1);
        SimpleKonResponse<SimpleKonDataRow> countyStatRange2 = data.getSjukfallPerLan(range2);
        sjukfallPerLan = new CasesPerCountyConverter(countyStatRange1, countyStatRange2, range1, range2).convert();
    }

    public void buildKonsfordelningPerLan() {
        final Range range = Range.createForLastMonthsExcludingCurrent(YEAR);
        SimpleKonResponse<SimpleKonDataRow> casesPerMonth = data.getSjukfallPerLan(range);
        konsfordelningPerLan = new SjukfallPerSexConverter().convert(casesPerMonth, range);
    }

    private Response getResponse(TableDataReport result, String csv) {
        if (csv == null || csv.isEmpty()) {
            return Response.ok(result).build();
        }
        return CsvConverter.getCsvResponse(result.getTableData(), "export.csv");
    }

    /**
     * Get sjukfall per manad.
     *
     * @return data
     */
    @GET
    @Path("getNumberOfCasesPerMonth{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getNumberOfCasesPerMonth(@PathParam("csv") String csv) {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        return getResponse(numberOfCasesPerMonth, csv);
    }

    /**
     * Get the list of diagnoskapitel.
     *
     * @return data
     */
    @GET
    @Path("getDiagnoskapitel")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Icd> getDiagnoskapitel() {
        LOG.info("Calling getKapitel");
        List<Icd> kapitel = new ArrayList<>();
        for (Icd10.Kapitel k : icd10.getKapitel(false)) {
            String s = k.getId();
            if (s.charAt(0) <= 'Z') {
                kapitel.add(new Icd(s, k.getName(), k.toInt()));
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
    @Path("getDiagnosisKapitelAndAvsnittAndKategori")
    @Produces({ MediaType.APPLICATION_JSON })
    public DiagnosisKapitelAndAvsnittAndKategoriResponse getDiagnosisKapitelAndAvsnittAndKod() {
        LOG.info("Calling getDiagnosisKapitelAndAvsnittAndKategori");
        Map<String, List<Icd>> avsnitts = new LinkedHashMap<>();
        Map<String, List<Icd>> kategoris = new LinkedHashMap<>();
        List<Icd10.Kapitel> kapitels = icd10.getKapitel(false);
        for (Icd10.Kapitel k : kapitels) {
            avsnitts.put(k.getId(), convertToIcds(k.getAvsnitt()));
            for (Icd10.Avsnitt avsnitt : k.getAvsnitt()) {
                kategoris.put(avsnitt.getId(), convertToIcds(avsnitt.getKategori()));
            }
        }
        return new DiagnosisKapitelAndAvsnittAndKategoriResponse(kategoris, avsnitts, getDiagnoskapitel());
    }

    private List<Icd> convertToIcds(List<? extends Icd10.Id> ids) {
        List<Icd> converted = new ArrayList<>(ids.size());
        for (Icd10.Id icdId : ids) {
            converted.add(new Icd(icdId.getId(), icdId.getName(), icdId.toInt()));
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
    @Path("getDiagnoskapitelstatistik{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDiagnoskapitelstatistik(@PathParam("csv") String csv) {
        LOG.info("Calling getDiagnoskapitelstatistik for national");
        return getResponse(diagnosgrupper, csv);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     *
     * @param groupId diagnosgruppid
     * @return data
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDiagnosavsnittstatistik(@PathParam("groupId") String groupId, @PathParam("csv") String csv) {
        LOG.info("Calling getDiagnosavsnittstatistik for national with groupId: " + groupId);
        return getResponse(diagnoskapitel.get(groupId), csv);
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
    @Produces({ MediaType.APPLICATION_JSON })
    public OverviewData getOverviewData() {
        return overview;
    }

    /**
     * Get sjukfall grouped by age and sex.
     *
     * @return data
     */
    @GET
    @Path("getAgeGroupsStatistics{csv:(/csv)?}")
    @Produces({MediaType.APPLICATION_JSON })
    public Response getAgeGroupsStatistics(@PathParam("csv") String csv) {
        LOG.info("Calling getAgeGroupsStatistics for national");
        return getResponse(aldersgrupper, csv);
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     *
     * @return data
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDegreeOfSickLeaveStatistics(@PathParam("csv") String csv) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics for national");
        return getResponse(sjukskrivningsgrad, csv);
    }

    /**
     * Get sjukfallslangd (grouped).
     *
     * @return data
     */
    @GET
    @Path("getSickLeaveLengthData{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSickLeaveLengthData(@PathParam("csv") String csv) {
        LOG.info("Calling getSickLeaveLengthData for national");
        return getResponse(sjukfallslangd, csv);
    }

    /**
     * Get sjukfall per lan.
     *
     * @return data
     */
    @GET
    @Path("getCountyStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCountyStatistics(@PathParam("csv") String csv) {
        return getResponse(sjukfallPerLan, csv);
    }

    /**
     * Get sjukfall per sex.
     *
     * @return data
     */
    @GET
    @Path("getSjukfallPerSexStatistics{csv:(/csv)?}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSjukfallPerSexStatistics(@PathParam("csv") String csv) {
        LOG.info("Calling getSjukfallPerSexStatistics for national");
        return getResponse(konsfordelningPerLan, csv);
    }

    @GET
    @Path("getIcd10Structure")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Icd> getIcd10Structure() {
        LOG.info("Calling getIcd10Structure");
        List<Icd10.Kapitel> kapitel = icd10.getKapitel(false);
        final List<Icd> icds = new ArrayList<>(Lists.transform(kapitel, new Function<Icd10.Kapitel, Icd>() {
            @Override
            public Icd apply(Icd10.Kapitel kapitel) {
                return new Icd(kapitel);
            }
        }));
        icds.add(new Icd("", "Utan giltig ICD-10 kod", Icd10.icd10ToInt(Icd10.OTHER_KATEGORI, Icd10RangeType.KATEGORI)));
        return icds;
    }

    @POST
    @Path("filter")
    @Produces({ MediaType.TEXT_PLAIN })
    public Response getFilterHash(String filterData) {
        LOG.info("Calling post FilterHash: " + filterData);
        try {
            return Response.ok(filterHashHandler.getHash(filterData)).build();
        } catch (FilterException e) {
            LOG.warn("Failed to get filter hash", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("filter/{filterHash}")
    public Response getFilterData(@PathParam("filterHash") String filterHash) {
        LOG.info("Calling get FilterData: " + filterHash);
        final Optional<String> filterData = filterHashHandler.getFilterData(filterHash);
        if (filterData.isPresent()) {
            return Response.ok(filterData.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
