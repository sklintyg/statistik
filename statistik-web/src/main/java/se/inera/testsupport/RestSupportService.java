/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.testsupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.countypopulation.CountyPopulationManagerForTest;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.hsa.HSAStore;
import se.inera.statistics.service.hsa.HsaDataInjectable;
import se.inera.statistics.service.hsa.HsaWsResponderMock;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhet;
import se.inera.statistics.service.landsting.persistance.landstingenhet.LandstingEnhetManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateManager;
import se.inera.statistics.service.landsting.persistance.landstingenhetupdate.LandstingEnhetUpdateOperation;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.IntygEvent;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.processlog.message.MessageEvent;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;
import se.inera.statistics.service.processlog.message.ProcessMessageLog;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.NationellDataInvoker;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WidelineConverter;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.time.ChangableClock;
import se.inera.statistics.web.service.ChartDataService;
import se.inera.testsupport.fkrapport.FkReportCreator;
import se.inera.testsupport.fkrapport.FkReportDataRow;
import se.inera.testsupport.socialstyrelsenspecial.SosCalculatedRow;
import se.inera.testsupport.socialstyrelsenspecial.SosReportCreator;
import se.inera.testsupport.socialstyrelsenspecial.SosRow;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("restSupportService")
public class RestSupportService {

    private static final Logger LOG = LoggerFactory.getLogger(RestSupportService.class);
    public static final String SOC_PARAM_FROMYEAR = "fromyear";
    public static final String SOC_PARAM_TOYEAR = "toyear";
    public static final String SOC_PARAM_DX = "dx";

    @Autowired
    private ChartDataService nationalChartDataService;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Receiver receiver;
    @Autowired
    private ProcessMessageLog processMessageLog;
    @Autowired
    private MessageLogConsumer messageLogConsumer;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellDataInvoker nationellData;

    @Autowired(required = false)
    private HsaDataInjectable hsaDataInjectable;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private LandstingManager landstingManager;

    @Autowired
    private LandstingEnhetManager landstingEnhetManager;

    @Autowired
    private LandstingEnhetUpdateManager landstingEnhetUpdateManager;

    @Autowired
    private SjukfallQuery sjukfallQuery;

    @Autowired
    private CountyPopulationManagerForTest countyPopulationManager;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private CountyPopulationInjector countyPopulationInjector;

    @Autowired
    private ChangableClock changableClock;

    @GET
    @Path("converteddate/{internalDate}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getConvertedDate(@PathParam("internalDate") int internalDate) {
        return Response.ok(WidelineConverter.toDate(internalDate).toString()).build();
    }

    @POST
    @Path("cutoff")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response setCutoff(int cutoff) {
        nationellData.setCutoff(cutoff);
        sjukfallQuery.setCutoff(cutoff);
        return Response.ok().build();
    }

    @GET
    @Path("now")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCurrentDateTime() {
        return Response.ok(changableClock.millis()).build();
    }

    @POST
    @Path("now")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response setCurrentDateTime(long timeMillis) {
        changableClock.setCurrentClock(Clock.offset(Clock.systemDefaultZone(), Duration.ofMillis(timeMillis - System.currentTimeMillis())));
        return Response.ok().build();
    }

    @POST
    @Path("clearDatabase")
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response clearDatabase() {

        manager.createNativeQuery("TRUNCATE TABLE " + IntygEvent.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + WideLine.TABLE).executeUpdate();
        manager.createQuery("UPDATE EventPointer SET eventId = 0").executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + Enhet.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + Lakare.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + HSAStore.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + MessageWideLine.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + MessageEvent.TABLE).executeUpdate();
        manager.createNativeQuery("TRUNCATE TABLE " + IntygCommon.TABLE).executeUpdate();
        sjukfallUtil.clearSjukfallGroupCache();
        warehouse.clearAisleCache();
        nationalChartDataService.buildCache();
        return Response.ok().build();
    }

    @PUT
    @Path("intyg")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response insertIntyg(Intyg intyg) {
        LOG.info("Insert intyg. id: " + intyg.getDocumentId() + ", data: " + intyg.getData());
        insertIntygWithoutLogging(intyg);
        return Response.ok().build();
    }

    @Transactional
    public void insertIntygWithoutLogging(Intyg intyg) {
        if (hsaDataInjectable != null) {
            hsaDataInjectable.setCountyForNextIntyg(intyg.getCounty());
            hsaDataInjectable.setKommunForNextIntyg(intyg.getKommun());
            hsaDataInjectable.setHuvudenhetIdForNextIntyg(intyg.getHuvudenhetId());
            hsaDataInjectable.setHsaKey(new HSAKey(intyg.getVardgivareId(), intyg.getEnhetId(), intyg.getLakareId()));
        }
        receiver.accept(intyg.getType(), intyg.getData(), intyg.getDocumentId(), intyg.getTimestamp());
        setEnhetName(intyg);
    }

    private void setEnhetName(Intyg intyg) {
        if (intyg.getEnhetId() == null || intyg.getEnhetId().isEmpty() || !HsaWsResponderMock.shouldExistInHsa(intyg.getEnhetId())) {
            return;
        }
        String name = intyg.getEnhetName() != null && !intyg.getEnhetName().isEmpty() ? intyg.getEnhetName() : intyg.getEnhetId();
        final Query query = manager.createNativeQuery("UPDATE enhet SET namn=:enhetName where enhetId=:enhetId");
        query.setParameter("enhetName", name);
        query.setParameter("enhetId", intyg.getEnhetId());
        int executeUpdate = query.executeUpdate();
        if (executeUpdate < 1) {
            Enhet enhet = new Enhet(new HsaIdVardgivare(intyg.getVardgivareId()), new HsaIdEnhet(intyg.getEnhetId()), name, "", "", "");
            manager.persist(enhet);
        }
    }

    @PUT
    @Path("meddelande")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response insertIntyg(Meddelande meddelande) {
        LOG.info("Insert meddelande. id: " + meddelande.getMessageId() + ", data: " + meddelande.getData());
        insertMeddelandeWithoutLogging(meddelande);
        return Response.ok().build();
    }

    @Transactional
    public void insertMeddelandeWithoutLogging(Meddelande meddelande) {
        processMessageLog.store(meddelande.getType(), meddelande.getData(), meddelande.getMessageId(), meddelande.getTimestamp());
    }

    @POST
    @Path("processIntyg")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response processIntyg() {
        LOG.debug("Log Job");
        int count;
        do {
            count = consumer.processBatch();
            LOG.info("Processed batch with {} entries", count);
        } while (count > 0);
        sjukfallUtil.clearSjukfallGroupCache();
        warehouse.clearAisleCache();
        nationalChartDataService.buildCache();
        return Response.ok().build();
    }

    @POST
    @Path("processMeddelande")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response processMeddelande() {
        LOG.debug("Log Job");

        long firstId;
        long lastId = 0;
        do {
            firstId = lastId;
            lastId = messageLogConsumer.processBatch(firstId);
        } while (firstId != lastId);
        LOG.debug("All messages processed");

        return Response.ok().build();
    }

    @POST
    @Path("denyCalc")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response denyCalc() {
        LOG.info("Deny calc");
        CalcCoordinator.setDenyAll(true);
        return Response.ok().build();
    }

    @POST
    @Path("allowCalc")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response allowCalc() {
        LOG.info("Allow calc");
        CalcCoordinator.setDenyAll(false);
        return Response.ok().build();
    }

    @PUT
    @Path("personal")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response insertPersonal(Personal personal) {
        if (hsaDataInjectable != null) {
            LOG.info("Insert personal: " + personal);
            hsaDataInjectable.addPersonal(new HsaIdLakare(personal.getId()), personal.getFirstName(), personal.getLastName(),
                    personal.getKon(), personal.getAge(),
                    personal.getBefattning(), personal.isSkyddad());
        }
        return Response.ok().build();
    }

    @PUT
    @Path("landsting/vgid/{vgid}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response insertLandsting(@PathParam("vgid") String vgId) {
        LOG.info("Insert landsting with vgid {}", vgId);
        if (!landstingManager.getForVg(new HsaIdVardgivare(vgId)).isPresent()) {
            landstingManager.add(vgId, new HsaIdVardgivare(vgId));
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("clearLandstingFileUploads")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response clearLandstingFileUploads() {
        LOG.info("Clearing all uploaded landsting files");
        final List<LandstingEnhet> allLandstingEnhets = landstingEnhetManager.getAll();
        allLandstingEnhets.forEach(landstingEnhet -> {
            final long landstingId = landstingEnhet.getLandstingId();
            landstingEnhetManager.removeByLandstingId(landstingId);
            landstingEnhetUpdateManager.update(landstingId, this.getClass().getSimpleName(), new HsaIdUser(""), "-",
                    LandstingEnhetUpdateOperation.REMOVE);
        });
        return Response.ok().build();
    }

    @POST
    @Path("clearCountyPopulation")
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response clearCountyPopulation() {
        countyPopulationInjector.clearCountyPopulations();
        countyPopulationManager.clearCountyPopulation();
        return Response.ok().build();
    }

    @PUT
    @Path("countyPopulation/{date}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response insertCountyPopulation(Map<String, KonField> countyPopulation, @PathParam("date") String date) {
        LOG.info("For date: {}, insert county population: {}", date, countyPopulation);
        countyPopulationInjector.addCountyPopulation(countyPopulation, java.time.LocalDate.parse(date).getYear());
        final LocalDate nextYear = LocalDate.parse(date).plusYears(1);
        countyPopulationManager.getCountyPopulation(new Range(nextYear, nextYear)); // Populate the cache in db
        return Response.ok().build();
    }

    /**
     * Get sjukfall information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenReport")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getSosStatistics(@QueryParam(SOC_PARAM_DX) List<String> dx, @QueryParam(SOC_PARAM_FROMYEAR) String fromYearParam,
            @QueryParam(SOC_PARAM_TOYEAR) String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx, changableClock, fromYear, toYear);
        final List<SosRow> sosReport = sosReportCreator.getSosReport();
        return Response.ok(sosReport).build();
    }

    private int getYear(String yearParam) {
        return yearParam == null ? LocalDate.now(changableClock).minusYears(1).getYear() : Integer.parseInt(yearParam);
    }

    /**
     * Get sjukfall mean value information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenMedianReport")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getSosMedianStatistics(@QueryParam("dx") List<String> dx, @QueryParam("fromyear") String fromYearParam,
            @QueryParam("toyear") String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx, changableClock, fromYear, toYear);
        final List<SosCalculatedRow> medianValuesSosReport = sosReportCreator.getMedianValuesSosReport();
        return Response.ok(medianValuesSosReport).build();
    }

    /**
     * Get sjukfall standard deviation information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenStdDevReport")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getSosStdDevStatistics(@QueryParam("dx") List<String> dx, @QueryParam("fromyear") String fromYearParam,
            @QueryParam("toyear") String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx, changableClock, fromYear, toYear);
        final List<SosCalculatedRow> medianValuesSosReport = sosReportCreator.getStdDevValuesSosReport();
        return Response.ok(medianValuesSosReport).build();
    }

    /**
     * Get sjukfall year report requested by FK (INTYG-3165).
     */
    @GET
    @Path("getFkYearReport")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getFkYearReport(@QueryParam("dx") final List<String> customDiagnoses) {
        List<String> dxList = customDiagnoses;
        if (dxList == null || dxList.size() == 0) {
            dxList = Arrays.asList("F32", "F43", "M54", "M17");
        }
        final Iterator<Aisle> aisles = warehouse.iterator();

        final FkReportCreator fkReportCreator = new FkReportCreator(aisles, icd10, dxList, changableClock);
        final List<FkReportDataRow> reportData = fkReportCreator.getReportData();
        return Response.ok(reportData).build();
    }

}
