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
package se.inera.testsupport;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.scheduler.active.LogJob;
import se.inera.statistics.service.caching.Cache;
import se.inera.statistics.service.countypopulation.CountyPopulationManagerForTest;
import se.inera.statistics.service.hsa.HSAStore;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.processlog.IntygEvent;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.processlog.intygsent.IntygSentEvent;
import se.inera.statistics.service.processlog.intygsent.IntygsentLogConsumer;
import se.inera.statistics.service.processlog.intygsent.ProcessIntygsentLog;
import se.inera.statistics.service.processlog.message.MessageEvent;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;
import se.inera.statistics.service.processlog.message.ProcessMessageLog;
import se.inera.statistics.service.region.persistance.region.RegionManager;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhet;
import se.inera.statistics.service.region.persistance.regionenhet.RegionEnhetManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateManager;
import se.inera.statistics.service.region.persistance.regionenhetupdate.RegionEnhetUpdateOperation;
import se.inera.statistics.service.report.model.Kon;
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
import se.inera.statistics.service.warehouse.query.RegionCutoff;
import se.inera.statistics.time.ChangableClock;
import se.inera.statistics.web.api.ChartDataService;
import se.inera.testsupport.fkrapport.FkReportCreator;
import se.inera.testsupport.fkrapport.FkReportDataRow;
import se.inera.testsupport.socialstyrelsenspecial.IntygCommonSosManager;
import se.inera.testsupport.socialstyrelsenspecial.SjukfallsLangdGroupSos;
import se.inera.testsupport.socialstyrelsenspecial.SosCalculatedRow;
import se.inera.testsupport.socialstyrelsenspecial.SosCountRow;
import se.inera.testsupport.socialstyrelsenspecial.SosMeCfs1ReportCreator;
import se.inera.testsupport.socialstyrelsenspecial.SosMeCfs1Row;
import se.inera.testsupport.socialstyrelsenspecial.SosMeCfs2ReportCreator;
import se.inera.testsupport.socialstyrelsenspecial.SosMeCfs2Row;
import se.inera.testsupport.socialstyrelsenspecial.SosReportCreator;
import se.inera.testsupport.socialstyrelsenspecial.SosRow;
import se.inera.testsupport.specialrapport.SjukfallLengthSpecialReportCreator;
import se.inera.testsupport.specialrapport.SjukfallLengthSpecialRow;
import se.inera.testsupport.specialrapport.regionskane.SpecialReportSkaneAgeCreator;
import se.inera.testsupport.specialrapport.regionskane.SpecialReportSkaneCreator;
import se.inera.testsupport.specialrapport.regionskane.SpecialSkaneRow;

@Service("restSupportService")
public class RestSupportService {

    private static final Logger LOG = LoggerFactory.getLogger(RestSupportService.class);
    public static final String SOC_PARAM_FROMYEAR = "fromyear";
    public static final String SOC_PARAM_TOYEAR = "toyear";
    public static final String SOC_PARAM_DX = "dx";
    public static final String SOC_PARAM_STARTDX = "sjukfallstartingwithdx";

    @Autowired
    @Qualifier("restTemplateStat")
    private RestTemplate restTemplate;

    @Value("${baseUrl}")
    private String baseUrl;

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

    @Autowired
    private ProcessIntygsentLog processIntygsentLog;

    @Autowired
    private IntygsentLogConsumer intygsentLogConsumer;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private NationellDataInvoker nationellData;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private RegionManager regionManager;

    @Autowired
    private RegionEnhetManager regionEnhetManager;

    @Autowired
    private RegionEnhetUpdateManager regionEnhetUpdateManager;

    @Autowired
    private RegionCutoff regionCutoff;

    @Autowired
    private CountyPopulationManagerForTest countyPopulationManager;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private CountyPopulationInjector countyPopulationInjector;

    @Autowired
    private ChangableClock changableClock;

    @Autowired(required = false)
    @Qualifier("intygCommonDxPopulator")
    private ApplicationListener<ApplicationEvent> intygCommonDxPopulator;

    @Autowired
    private Cache cache;

    @Autowired
    private CalcCoordinator calcCoordinator;

    @Autowired
    private LogJob logJob;

//    @Autowired(required = false)
//    private HsaServiceStub hsaServiceStub;

    private IntygCommonSosManager intygCommonSosManager;

    @PostConstruct
    public void setup() {
        intygCommonSosManager = new IntygCommonSosManager(icd10, manager, warehouse);
    }

    /**
     * Can only be invoked when app is started with spring profile "populate-intygcommon-dx".
     */
    @GET
    @Path("invokeIntygCommonDxPopulator")
    @Produces({MediaType.APPLICATION_JSON})
    public Response populateIntygCommonDx() {
        intygCommonDxPopulator.onApplicationEvent(new ApplicationEvent(this) {
            @Override
            public Object getSource() {
                return super.getSource();
            }
        });
        return Response.ok().build();
    }

    @GET
    @Path("converteddate/{internalDate}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConvertedDate(@PathParam("internalDate") int internalDate) {
        return Response.ok(WidelineConverter.toDate(internalDate).toString()).build();
    }

    @POST
    @Path("cutoff")
    @Produces({MediaType.APPLICATION_JSON})
    public Response setCutoff(int cutoff) {
        nationellData.setCutoff(cutoff);
        regionCutoff.setCutoff(cutoff);
        return Response.ok().build();
    }

    @GET
    @Path("now")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCurrentDateTime() {
        return Response.ok(changableClock.millis()).build();
    }

    @POST
    @Path("now")
    @Produces({MediaType.APPLICATION_JSON})
    public Response setCurrentDateTime(long timeMillis) {
        final Clock baseClock = Clock.system(ZoneId.systemDefault());
        changableClock.setCurrentClock(Clock.offset(baseClock, Duration.ofMillis(timeMillis - System.currentTimeMillis())));
        return Response.ok().build();
    }

    @POST
    @Path("clearDatabase")
    @Produces({MediaType.APPLICATION_JSON})
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
        manager.createNativeQuery("TRUNCATE TABLE " + IntygSentEvent.TABLE).executeUpdate();
        return clearCaches();
    }

    @POST
    @Path("clearCaches")
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response clearCaches() {
        cache.clearCaches();
        return Response.ok().build();
    }

//    @PUT
//    @Path("intyg")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_JSON})
//    @Transactional
//    public Response insertIntyg(Intyg intyg) {
//        LOG.info("Insert intyg. id: " + intyg.getDocumentId() + ", data: " + intyg.getData());
//        insertIntygWithoutLogging(intyg);
//        return Response.ok().build();
//    }
//
//    @Transactional
//    public void insertIntygWithoutLogging(Intyg intyg) {
//        if (hsaServiceStub != null) {
//            hsaServiceStub.deleteCareUnit(intyg.getEnhetId());
//            hsaServiceStub.deleteSubUnit(intyg.getEnhetId());
//
//            AbstractUnitStub unit;
//            if (intyg.getHuvudenhetId() == null || intyg.getEnhetId().equals(intyg.getHuvudenhetId())) {
//                unit = new CareUnitStub();
//                ((CareUnitStub) unit).setSubUnits(Collections.emptyList());
//            } else {
//                unit = new SubUnitStub();
//                ((SubUnitStub) unit).setParentHsaId(intyg.getHuvudenhetId());
//            }
//            unit.setId(cleanString(intyg.getEnhetId()));
//            unit.setCareProviderHsaId(cleanString(intyg.getVardgivareId()));
//            unit.setName(getEnhetsNamn(cleanString(intyg.getEnhetId()), intyg.getEnhetName()));
//            unit.setCountyCode(intyg.getCounty());
//            unit.setMunicipalityCode(intyg.getKommun());
//
//            if (unit instanceof CareUnitStub) {
//                hsaServiceStub.addCareUnit((CareUnitStub) unit);
//            } else {
//                hsaServiceStub.addSubUnit((SubUnitStub) unit);
//            }
//        }
//
//        receiver.accept(intyg.getType(), intyg.getData(), intyg.getDocumentId(), intyg.getTimestamp());
//        setEnhetName(intyg);
//    }

    private String cleanString(String string) {
        return string == null ? null : string.replace(" ", "").toLowerCase();
    }

    private String getEnhetsNamn(String enhetId, String enhetName) {
        if (enhetName != null) {
            return enhetName;
        }
        if (enhetId != null && enhetId.startsWith("vg1-")) {
            String suffix = enhetId.substring(enhetId.lastIndexOf('-') + 1);
            return "Verksamhet " + suffix;
        }
        return "Enhet " + enhetId;
    }

    private void setEnhetName(Intyg intyg) {
        if (intyg.getEnhetId() == null || intyg.getEnhetId().isEmpty() || !shouldExistInHsa(intyg.getEnhetId())) {
            return;
        }
        String name = intyg.getEnhetName() != null && !intyg.getEnhetName().isEmpty() ? intyg.getEnhetName() : intyg.getEnhetId();
        final Query query = manager.createNativeQuery("UPDATE enhet SET namn=:enhetName where enhetId=:enhetId");
        query.setParameter("enhetName", name);
        query.setParameter("enhetId", intyg.getEnhetId());
        int executeUpdate = query.executeUpdate();
        if (executeUpdate < 1) {
            Enhet enhet = new Enhet(new HsaIdVardgivare(intyg.getVardgivareId()), new HsaIdEnhet(intyg.getEnhetId()),
                name, "", "", "", intyg.getHuvudenhetId());
            manager.persist(enhet);
        }
    }

    public static boolean shouldExistInHsa(String hsaId) {
        return hsaId != null && !hsaId.startsWith("EJHSA") && !"UTANENHETSID".equals(hsaId);
    }

    @PUT
    @Path("meddelande")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
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
    @Produces({MediaType.APPLICATION_JSON})
    public Response processIntyg() {
        logJob.process();

        clearCaches();

        nationalChartDataService.buildNationalDataCache();

        return Response.ok().build();
    }

    @POST
    @Path("processMeddelande")
    @Produces({MediaType.APPLICATION_JSON})
    public Response processMeddelande() {
        LOG.debug("Log Job");

        long firstId;
        long lastId = 0;
        do {
            firstId = lastId;
            lastId = messageLogConsumer.processBatch(firstId);
        } while (firstId != lastId);
        LOG.debug("All messages processed");
        return clearCaches();
    }

    @POST
    @Path("denyCalc")
    @Produces({MediaType.APPLICATION_JSON})
    public Response denyCalc() {
        LOG.info("Deny calc");
        calcCoordinator.setDenyAll(true);
        return Response.ok().build();
    }

    @POST
    @Path("allowCalc")
    @Produces({MediaType.APPLICATION_JSON})
    public Response allowCalc() {
        LOG.info("Allow calc");
        calcCoordinator.setDenyAll(false);
        return Response.ok().build();
    }

//    @PUT
//    @Path("personal")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response insertPersonal(Personal personal) {
//        if (hsaServiceStub != null) {
//            HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(personal.getId());
//            if (hsaPerson == null) {
//                hsaPerson = new HsaPerson();
//                hsaPerson.setHsaId(personal.getId());
//            }
//            hsaPerson.setGivenName(personal.getFirstName());
//            hsaPerson.setMiddleAndSurname(personal.getLastName());
//            hsaPerson.setAge(String.valueOf(personal.getAge()));
//            hsaPerson.setGender(String.valueOf(personal.getKon().getHsaRepresantation()));
//            hsaPerson.setProtectedPerson(personal.isSkyddad());
//            List<PaTitle> paTitles = personal.getBefattning().stream().map(b -> {
//                var paTitle = new PaTitle();
//                paTitle.setTitleCode(b);
//                paTitle.setTitleName(b);
//                return paTitle;
//            }).collect(Collectors.toList());
//            hsaPerson.setPaTitle(paTitles);
//
//            hsaServiceStub.addHsaPerson(hsaPerson);
//        }
//
//        return Response.ok().build();
//    }

    @PUT
    @Path("region/vgid/{vgid}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response insertRegion(@PathParam("vgid") String vgId) {
        LOG.info("Insert region with vgid {}", vgId);
        if (!regionManager.getForVg(new HsaIdVardgivare(vgId)).isPresent()) {
            regionManager.add(vgId, new HsaIdVardgivare(vgId));
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("clearRegionFileUploads")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response clearRegionFileUploads() {
        LOG.info("Clearing all uploaded region files");
        final List<RegionEnhet> allRegionEnhets = regionEnhetManager.getAll();
        allRegionEnhets.forEach(regionEnhet -> {
            final long regionId = regionEnhet.getRegionId();
            regionEnhetManager.removeByRegionId(regionId);
            regionEnhetUpdateManager.update(regionId, this.getClass().getSimpleName(), new HsaIdUser(""), "-",
                RegionEnhetUpdateOperation.REMOVE);
        });
        return Response.ok().build();
    }

    @POST
    @Path("clearCountyPopulation")
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response clearCountyPopulation() {
        countyPopulationInjector.clearCountyPopulations();
        countyPopulationManager.clearCountyPopulation();
        return Response.ok().build();
    }

    @PUT
    @Path("countyPopulation/{date}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response insertCountyPopulation(Map<String, KonField> countyPopulation, @PathParam("date") String date) {
        LOG.info("For date: {}, insert county population: {}", date, countyPopulation);
        countyPopulationInjector.addCountyPopulation(countyPopulation, java.time.LocalDate.parse(date).getYear());
        final LocalDate nextYear = LocalDate.parse(date).plusYears(1);
        countyPopulationManager.getCountyPopulation(new Range(nextYear, nextYear)); // Populate the cache in db
        return Response.ok().build();
    }

    @POST
    @Path("sendIntygToMottagare/{intygId}/{mottagare}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response sendIntygToMottagare(@PathParam("intygId") String intygId, @PathParam("mottagare") String mottagare) {
        processIntygsentLog.store(intygId, mottagare, System.currentTimeMillis());
        intygsentLogConsumer.processBatch();
        return Response.ok().build();
    }

    /**
     * Get sjukfall information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSosStatistics(@QueryParam(SOC_PARAM_DX) List<String> dx,
        @QueryParam(SOC_PARAM_STARTDX) String startWithSpecifiedDx,
        @QueryParam(SOC_PARAM_FROMYEAR) String fromYearParam,
        @QueryParam(SOC_PARAM_TOYEAR) String toYearParam) {
        final List<SosRow> sosReport = getSosRows(dx, startWithSpecifiedDx, fromYearParam, toYearParam);
        return Response.ok(sosReport).build();
    }

    private List<SosRow> getSosRows(List<String> dx, String startWithSpecifiedDx, String fromYearParam, String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx,
            Boolean.parseBoolean(startWithSpecifiedDx), changableClock, fromYear, toYear);
        return sosReportCreator.getSosReport();
    }

    private int getYear(String yearParam) {
        return yearParam == null ? LocalDate.now(changableClock).minusYears(1).getYear() : Integer.parseInt(yearParam);
    }

    /**
     * Get sjukfall count information requested by socialstyrelsen (INTYG-6691).
     */
    @GET
    @Path("getSocialstyrelsenAntalReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSosCountStatistics(@QueryParam(SOC_PARAM_DX) List<String> dx,
        @QueryParam(SOC_PARAM_STARTDX) String startWithSpecifiedDx,
        @QueryParam(SOC_PARAM_FROMYEAR) String fromYearParam,
        @QueryParam(SOC_PARAM_TOYEAR) String toYearParam) {
        final List<SosRow> sosRows = getSosRows(dx, startWithSpecifiedDx, fromYearParam, toYearParam);
        final List<SosCountRow> sosCountRows = getSosCountRows(sosRows);
        final List<Map<String, Object>> response = createCountResponse(sosCountRows);
        return Response.ok(response).build();
    }

    private List<Map<String, Object>> createCountResponse(List<SosCountRow> sosCountRows) {
        return sosCountRows.stream().map((Function<SosCountRow, Map<String, Object>>) r -> {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("diagnos", r.getDiagnos());
            map.put("totalt", r.getTotalt());
            map.put("kvinnor", r.getKvinnor());
            map.put("man", r.getMan());
            for (SjukfallsLangdGroupSos group : SjukfallsLangdGroupSos.values()) {
                map.put(group.getGroupName() + " K", r.getFemaleByLength(group));
                map.put(group.getGroupName() + " M", r.getMaleByLength(group));
            }
            return map;
        }).collect(Collectors.toList());
    }

    private List<SosCountRow> getSosCountRows(List<SosRow> sosRows) {
        final Map<String, List<SosRow>> sosRowsByDx = sosRows.stream().collect(Collectors.groupingBy(SosRow::getDiagnos));
        return sosRowsByDx.entrySet().stream().map(stringListEntry -> {
            final Map<Kon, List<SosRow>> rowsByGender = stringListEntry.getValue().stream().collect(Collectors.groupingBy(SosRow::getKon));

            final List<SosRow> maleRows = getNullSafeList(rowsByGender.get(Kon.MALE));
            final Map<SjukfallsLangdGroupSos, Integer> malePerLength = getCountPerSjukfallsLangd(maleRows);

            final List<SosRow> femaleRows = getNullSafeList(rowsByGender.get(Kon.FEMALE));
            final Map<SjukfallsLangdGroupSos, Integer> femalePerLength = getCountPerSjukfallsLangd(femaleRows);

            final String diagnos = stringListEntry.getKey();
            final int femaleCount = femaleRows.size();
            final int maleCount = maleRows.size();
            final int total = femaleCount + maleCount;
            return new SosCountRow(diagnos, total, femaleCount, maleCount, malePerLength, femalePerLength);
        }).collect(Collectors.toList());
    }

    private <T> List<T> getNullSafeList(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    private Map<SjukfallsLangdGroupSos, Integer> getCountPerSjukfallsLangd(List<SosRow> sosRows) {
        final Map<SjukfallsLangdGroupSos, List<SosRow>> rowsByLength = sosRows.stream().collect(
            Collectors.groupingBy(sosRow -> SjukfallsLangdGroupSos.getByLength(sosRow.getLength())));
        return rowsByLength.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().size()));
    }

    /**
     * Get sjukfall mean value information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenMedianReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSosMedianStatistics(@QueryParam("dx") List<String> dx,
        @QueryParam(SOC_PARAM_STARTDX) String startWithSpecifiedDx,
        @QueryParam("fromyear") String fromYearParam,
        @QueryParam("toyear") String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx,
            Boolean.parseBoolean(startWithSpecifiedDx), changableClock, fromYear, toYear);
        final List<SosCalculatedRow> medianValuesSosReport = sosReportCreator.getMedianValuesSosReport();
        return Response.ok(medianValuesSosReport).build();
    }

    /**
     * Get sjukfall standard deviation information requested by socialstyrelsen (INTYG-2449).
     */
    @GET
    @Path("getSocialstyrelsenStdDevReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSosStdDevStatistics(@QueryParam("dx") List<String> dx,
        @QueryParam(SOC_PARAM_STARTDX) String startWithSpecifiedDx,
        @QueryParam("fromyear") String fromYearParam,
        @QueryParam("toyear") String toYearParam) {
        final Iterator<Aisle> aisles = warehouse.iterator();
        int fromYear = getYear(fromYearParam);
        int toYear = getYear(toYearParam);
        final SosReportCreator sosReportCreator = new SosReportCreator(aisles, sjukfallUtil, icd10, dx,
            Boolean.parseBoolean(startWithSpecifiedDx), changableClock, fromYear, toYear);
        final List<SosCalculatedRow> medianValuesSosReport = sosReportCreator.getStdDevValuesSosReport();
        return Response.ok(medianValuesSosReport).build();
    }

    /**
     * Special report for Socialstyrelsen regarding dx G933 (INTYG-6994).
     */
    @GET
    @Path("getSocialstyrelsenMeCfs1Report")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSocialstyrelsenMeCfs1Report() {
        final SosMeCfs1ReportCreator sosMeCfs1ReportCreator = new SosMeCfs1ReportCreator(intygCommonSosManager, warehouse);
        final List<SosMeCfs1Row> sosReport = sosMeCfs1ReportCreator.getSosReport(nationellData.getCutoff());
        return Response.ok(sosReport).build();
    }

    /**
     * Special report for Socialstyrelsen regarding dx G933 (INTYG-6994).
     */
    @GET
    @Path("getSocialstyrelsenMeCfs2Report")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSocialstyrelsenMeCfs2Report() {
        final Iterator<Aisle> aisles = warehouse.iterator();
        final List<SosMeCfs2Row> sosReport = new SosMeCfs2ReportCreator(aisles, sjukfallUtil,
            nationellData.getCutoff(), icd10).getSosReport();
        return Response.ok(sosReport).build();
    }

    /**
     * Get sjukfall year report requested by FK (INTYG-3165).
     */
    @GET
    @Path("getFkYearReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
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

    /**
     * Special report for regarding sjukfall lengths (INTYG-7983). Using the last full year if no specific year is requested.
     */
    @GET
    @Path("getSpecialLengthReport")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSpecialLengthReport(@QueryParam("year") String yearParam) {
        int year = getYear(yearParam);
        final Iterator<Aisle> aisles = warehouse.iterator();
        final List<SjukfallLengthSpecialRow> sosReport = new SjukfallLengthSpecialReportCreator(aisles, sjukfallUtil, icd10)
            .getReport(year);
        return Response.ok(sosReport).build();
    }

    /**
     * Special report for Region Skane requested in INTYGFV-12015.
     */
    @GET
    @Path("getSpecialReportSkane")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getSpecialReportSkane(@QueryParam("enhet") List<String> enhets) {
        final List<SpecialSkaneRow> report = new SpecialReportSkaneCreator(warehouse.iterator(), sjukfallUtil, icd10)
            .getReport(false, enhets);
        report.addAll(new SpecialReportSkaneAgeCreator(warehouse.iterator(), sjukfallUtil)
            .getReport(false, enhets));
        report.addAll(new SpecialReportSkaneCreator(warehouse.iterator(), sjukfallUtil, icd10)
            .getReport(true, enhets));
        report.addAll(new SpecialReportSkaneAgeCreator(warehouse.iterator(), sjukfallUtil)
            .getReport(true, enhets));
        return Response.ok(report).build();
    }

}