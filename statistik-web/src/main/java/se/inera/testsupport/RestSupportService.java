package se.inera.testsupport;

import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.hsa.HsaDataInjectable;
import se.inera.statistics.service.landsting.persistance.landsting.LandstingManager;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.processlog.Receiver;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.WarehouseManager;
import se.inera.statistics.service.warehouse.query.CalcCoordinator;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.web.service.ChartDataService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service("restSupportService")
public class RestSupportService {

    private static final Logger LOG = LoggerFactory.getLogger(RestSupportService.class);

    @Autowired
    private ChartDataService nationalChartDataService;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Receiver receiver;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private NationellData nationellData;

    @Autowired
    private HsaDataInjectable hsaDataInjectable;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Autowired
    private LandstingManager landstingManager;

    @Autowired
    private SjukfallQuery sjukfallQuery;

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
        return Response.ok(DateTimeUtils.currentTimeMillis()).build();
    }

    @POST
    @Path("now")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response setCurrentDateTime(long timeMillis) {
        DateTimeUtils.setCurrentMillisOffset(timeMillis - System.currentTimeMillis());
        return Response.ok().build();
    }

    @POST
    @Path("clearDatabase")
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public Response clearDatabase() {
        manager.createQuery("DELETE FROM IntygEvent").executeUpdate();
        manager.createQuery("DELETE FROM WideLine").executeUpdate();
        manager.createQuery("DELETE FROM EventPointer").executeUpdate();
        manager.createQuery("DELETE FROM Enhet").executeUpdate();
        manager.createQuery("DELETE FROM Lakare").executeUpdate();
        manager.createQuery("DELETE FROM HSAStore").executeUpdate();
        warehouse.clear();
        sjukfallUtil.clearSjukfallGroupCache();
        nationalChartDataService.buildCache();
        return Response.ok().build();
    }

    @PUT
    @Path("intyg")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response insertIntyg(Intyg intyg) {
        LOG.info("Insert intyg. id: " + intyg.getDocumentId() + ", data: " + intyg.getData());
        hsaDataInjectable.setCountyForNextIntyg(intyg.getCounty());
        hsaDataInjectable.setHuvudenhetIdForNextIntyg(intyg.getHuvudenhetId());
        receiver.accept(intyg.getType(), intyg.getData(), intyg.getDocumentId(), intyg.getTimestamp());
        return Response.ok().build();
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
        warehouseManager.loadEnhetAndWideLines();
        nationalChartDataService.buildCache();
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
        LOG.info("Insert personal: " + personal);
        hsaDataInjectable.addPersonal(personal.getId(), personal.getFirstName(), personal.getLastName(), personal.getKon(), personal.getAge(), personal.getBefattning());
        return Response.ok().build();
    }

    @PUT
    @Path("landsting/name/{name}/vgid/{vgid}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response insertLandsting(@PathParam("name") String name, @PathParam("vgid") String vgId) {
        LOG.info("Insert landsting with name {} and vgid {}", name, vgId);
        if (!landstingManager.getForVg(vgId.toUpperCase()).isPresent()) {
            landstingManager.add(name, vgId.toUpperCase());
        }
        return Response.ok().build();
    }

}
