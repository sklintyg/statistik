/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.endpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.overview.OverviewData;
import se.inera.statistics.web.service.DiagnosisKapitelAndAvsnittAndKategoriResponse;
import se.inera.statistics.web.service.FilterException;
import se.inera.statistics.web.service.FilterHashHandler;
import se.inera.statistics.web.service.NationellDataCalculator;
import se.inera.statistics.web.service.NationellDataResult;
import se.inera.statistics.web.service.Report;
import se.inera.statistics.web.service.ResponseHandler;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

/**
 * Statistics services that does not require authentication. Unless otherwise noted, the data returned
 * contains two data sets, one suitable for chart display, and one suited for tables. Csv and xlsx variants
 * only contains one data set.
 *
 * Csv files are semicolon separated, xlsx files can also be requested, otherwise json is used.
 */
@Service("chartService")
public class ChartDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ChartDataService.class);
    public static final String TEXT_CP1252 = "text/plain; charset=cp1252";
    private static final String JOB_NAME = "ChartDataService.reloadJob";

    @Autowired
    private Icd10 icd10;

    @Autowired
    private FilterHashHandler filterHashHandler;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private ResponseHandler responseHandler;

    @Autowired
    private NationellDataCalculator nationellDataCalculator;

    @Autowired
    private LogMDCHelper logMDCHelper;

    @Autowired
    private Cache cache;

    private NationellDataResult getNationellDataResult() {
        return buildNationalDataCache();
    }

    // always populate national cache on startup
    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        new Thread(this::getNationellDataResult).start();
    }

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    @SchedulerLock(name = JOB_NAME)
    public void reloadJob() {
        cache.clearCaches();
        buildNationalDataCache();
    }

    @PrometheusTimeMethod(help = "Jobb för att uppdatera information i cache")
    public NationellDataResult buildNationalDataCache() {
        final List<NationellDataResult> nationalData = new ArrayList<>();
        logMDCHelper.run(() -> {
            LOG.info("National data requested");
            if (!cache.getAndSetNationaldataCalculationOngoing(true)) {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    nationalData.add(cache.getNationalData(nationellDataCalculator::getData));
                    stopWatch.stop();
                    LOG.info("National data fetched " + stopWatch.getTotalTimeMillis());
                } catch (Exception e) {
                    LOG.error("National data generation failed", e);
                } finally {
                    cache.getAndSetNationaldataCalculationOngoing(false);
                }
            } else {
                LOG.info("National cache population is already ongoing. This population request is therefore skipped.");
            }
        });
        return nationalData.isEmpty() ? new NationellDataResult() : nationalData.get(0);
    }

    /*
     * private void buildNumberOfMeddelandenPerMonth() {
     * final Range range = Range.createForLastMonthsExcludingCurrent(EIGHTEEN_MONTHS, clock);
     * SimpleKonResponse casesPerMonth = data.getMeddelandenPerMonth(range);
     * final FilterSettings filterSettings = new FilterSettings(Filter.empty(), range);
     * numberOfMeddelandenPerMonth = new MessagePeriodConverter().convert(casesPerMonth, filterSettings);
     * }
     */

    private Response getResponse(TableDataReport result, String format, Report report) {
        if (format == null || format.isEmpty()) {
            return Response.ok(result).build();
        }
        return responseHandler.getXlsx(result, Collections.emptyList(), report);
    }

    /**
     * Get sjukfall per manad.
     */
    @GET
    @Path("getNumberOfCasesPerMonth")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till antal sjukfall per måndad på nationell nivå")
    public Response getNumberOfCasesPerMonth(@QueryParam("format") String format) {
        LOG.info("Calling getNumberOfCasesPerMonth for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getNumberOfCasesPerMonth");
        return getResponse(getNationellDataResult().getNumberOfCasesPerMonth(), format, Report.N_SJUKFALLTOTALT);
    }

    /**
     * Get meddelanden per manad.
     *
     * @GET
     *      @Path("getNumberOfMeddelandenPerMonth")
     * @Produces({ MediaType.APPLICATION_JSON })
     *             public Response getNumberOfMeddelandenPerMonth(@QueryParam("format") String format) {
     *             LOG.info("Calling getNumberOfMeddelandenPerMonth for national");
     *             monitoringLogService.logTrackAccessAnonymousChartData("getNumberOfMeddelandenPerMonth");
     *             return getResponse(numberOfMeddelandenPerMonth, format);
     *             }
     */

    /**
     * Get the list of diagnoskapitel.
     */
    @GET
    @Path("getDiagnoskapitel")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(help = "API-tjänst för åtkomst till ICD-10 diagnoskapitel")
    public List<Icd> getDiagnoskapitel() {
        LOG.info("Calling getKapitel");
        monitoringLogService.logTrackAccessAnonymousChartData("getKapitel");
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
     */
    @GET
    @Path("getDiagnosisKapitelAndAvsnittAndKategori")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till ICD-10 diagnoskapitel, avsnitt och kategorier")
    public DiagnosisKapitelAndAvsnittAndKategoriResponse getDiagnosisKapitelAndAvsnittAndKod() {
        LOG.info("Calling getDiagnosisKapitelAndAvsnittAndKategori");
        monitoringLogService.logTrackAccessAnonymousChartData("getDiagnosisKapitelAndAvsnittAndKategori");
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
     */
    @GET
    @Path("getDiagnoskapitelstatistik")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till nationell statistik för sjukfall per diagnoskaptiel och -grupp")
    public Response getDiagnoskapitelstatistik(@QueryParam("format") String format) {
        LOG.info("Calling getDiagnoskapitelstatistik for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getDiagnoskapitelstatistik");
        return getResponse(getNationellDataResult().getDiagnosgrupper(), format, Report.N_DIAGNOSGRUPP);
    }

    /**
     * Get sjukfall per diagnosavsnitt for given diagnoskapitel.
     */
    @GET
    @Path("getDiagnosavsnittstatistik/{groupId}")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till nationell statistik för sjukfall per diagnosavsnitt för ett kapitel")
    public Response getDiagnosavsnittstatistik(@PathParam("groupId") String groupId, @QueryParam("format") String format) {
        LOG.info("Calling getDiagnosavsnittstatistik for national with groupId: " + groupId);
        monitoringLogService.logTrackAccessAnonymousChartData("getDiagnosavsnittstatistik");
        return getResponse(getNationellDataResult().getDiagnoskapitel().get(groupId), format, Report.N_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL);
    }

    /**
     * Get overview. Sex distribution, change comparing lastUpdated three months to previous three months,
     * top lists for diagnosgrupp, aldersgrupp, sjukskrivningslangd,
     * sjukskrivningsgrad, lan. Only chart formatted data.
     */
    @GET
    @Path("getOverview")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till senaste 3 mån. kön, ålder, diagnos, sjukskrivningslängd.")
    public OverviewData getOverviewData() {
        return getNationellDataResult().getOverview();
    }

    /**
     * Get sjukfall grouped by age and sex.
     */
    @GET
    @Path("getAgeGroupsStatistics")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till statistk för sjukfall grupperad på ålder och kön")
    public Response getAgeGroupsStatistics(@QueryParam("format") String format) {
        LOG.info("Calling getAgeGroupsStatistics for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getAgeGroupsStatistics");
        return getResponse(getNationellDataResult().getAldersgrupper(), format, Report.N_ALDERSGRUPP);
    }

    /**
     * Get sjukskrivningsgrad per calendar month.
     */
    @GET
    @Path("getDegreeOfSickLeaveStatistics")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till sjukskrivningsgrad per kalendermånad")
    public Response getDegreeOfSickLeaveStatistics(@QueryParam("format") String format) {
        LOG.info("Calling getDegreeOfSickLeaveStatistics for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getDegreeOfSickLeaveStatistics");
        return getResponse(getNationellDataResult().getSjukskrivningsgrad(), format, Report.N_SJUKSKRIVNINGSGRAD);
    }

    /**
     * Get sjukfallslangd (grouped).
     */
    @GET
    @Path("getSickLeaveLengthData")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(help = "API-tjänst för åtkomst till grupperad sjukfallslängd")
    public Response getSickLeaveLengthData(@QueryParam("format") String format) {
        LOG.info("Calling getSickLeaveLengthData for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getSickLeaveLengthData");
        return getResponse(getNationellDataResult().getSjukfallslangd(), format, Report.N_SJUKSKRIVNINGSLANGD);
    }

    /**
     * Get sjukfall per lan.
     */
    @GET
    @Path("getCountyStatistics")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till sjukfall per län")
    public Response getCountyStatistics(@QueryParam("format") String format) {
        LOG.info("Calling getCountyStatistics for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getCountyStatistics");
        return getResponse(getNationellDataResult().getSjukfallPerLan(), format, Report.N_LAN);
    }

    /**
     * Get sjukfall per sex.
     */
    @GET
    @Path("getSjukfallPerSexStatistics")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till sjukfall per kön")
    public Response getSjukfallPerSexStatistics(@QueryParam("format") String format) {
        LOG.info("Calling getSjukfallPerSexStatistics for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getSjukfallPerSexStatistics");
        return getResponse(getNationellDataResult().getKonsfordelningPerLan(), format, Report.N_LANANDELSJUKFALLPERKON);
    }

    @GET
    @Path("getMeddelandenPerAmne")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till meddelande per ämne")
    public Response getMeddelandenPerAmne(@QueryParam("format") String format) {
        LOG.info("Calling getMeddelandenPerAmne for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getMeddelandenPerAmne");
        return getResponse(getNationellDataResult().getMeddelandenPerAmne(), format, Report.N_MEDDELANDENPERAMNE);
    }

    @GET
    @Path("getIntygPerTyp")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till antal sjukintyg per typ")
    public Response getIntygPerTyp(@QueryParam("format") String format) {
        LOG.info("Calling getIntygPerTyp for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getIntygPerTyp");
        return getResponse(getNationellDataResult().getIntygPerTyp(), format, Report.N_INTYGPERTYP);
    }

    @GET
    @Path("getIntygPerSjukfallTvarsnitt")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till intyg per sjukfall")
    public Response getIntygPerSjukfallTvarsnitt(@QueryParam("format") String format) {
        LOG.info("Calling getIntygPerSjukfallTvarsnitt for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getIntygPerSjukfallTvarsnitt");
        return getResponse(getNationellDataResult().getIntygPerSjukfall(), format, Report.N_INTYGPERSJUKFALL);
    }

    @GET
    @Path("getAndelKompletteringar")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till andel kompletteringar")
    public Response getAndelKompletteringar(@QueryParam("format") String format) {
        LOG.info("Calling getAndelKompletteringar for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getAndelKompletteringar");
        return getResponse(getNationellDataResult().getAndelKompletteringar(), format, Report.N_ANDELKOMPLETTERINGAR);
    }

    @GET
    @Path("getKompletteringarPerFragaTvarsnitt")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till tvärsnittet av kompletteringar per fråga")
    public Response getKompletteringarPerFragaTvarsnitt(@QueryParam("format") String format) {
        LOG.info("Calling getKompletteringarPerFragaTvarsnitt for national");
        monitoringLogService.logTrackAccessAnonymousChartData("getKompletteringarPerFragaTvarsnitt");
        return getResponse(getNationellDataResult().getKompletteringarPerFraga(), format, Report.N_KOMPLETTERINGARPERFRAGA);
    }

    @GET
    @Path("getIcd10Structure")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till ICD-10 kodder")
    public List<Icd> getIcd10Structure() {
        LOG.info("Calling getIcd10Structure");
        monitoringLogService.logTrackAccessAnonymousChartData("getIcd10Structure");
        return icd10.getIcdStructure();
    }

    @POST
    @Path("filter")
    @Produces({MediaType.TEXT_PLAIN})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till signatur (hash) för filterdata")
    public Response getFilterHash(String filterData) {
        LOG.info("Calling post FilterHash: " + filterData);
        monitoringLogService.logTrackAccessAnonymousChartData("getFilterHash");
        try {
            return Response.ok(filterHashHandler.getHash(filterData)).build();
        } catch (FilterException e) {
            LOG.warn("Failed to get filter hash", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("filter/{filterHash}")
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till filterdata med given signatur")
    public Response getFilterData(@PathParam("filterHash") String filterHash) {
        LOG.info("Calling get FilterData: " + filterHash);
        monitoringLogService.logTrackAccessAnonymousChartData("getFilterData");
        final Optional<String> filterData = filterHashHandler.getFilterData(filterHash);
        if (filterData.isPresent()) {
            return Response.ok(filterData.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
