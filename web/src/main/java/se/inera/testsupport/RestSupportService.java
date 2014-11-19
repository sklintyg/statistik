package se.inera.testsupport;

import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistics.service.warehouse.NationellData;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.web.service.ChartDataService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service("restSupportService")
public class RestSupportService {

    @Autowired
    private ChartDataService nationalChartDataService;

    @Autowired
    private Warehouse warehouse;

    @GET
    @Path("now")
    @Produces({MediaType.APPLICATION_JSON })
    public Response getCurrentDateTime() {
        return Response.ok(DateTimeUtils.currentTimeMillis()).build();
    }

    @POST
    @Path("now")
    @Produces({MediaType.APPLICATION_JSON })
    public Response setCurrentDateTime(long timeMillis) {
        DateTimeUtils.setCurrentMillisFixed(timeMillis);
        return Response.ok().build();
    }

    @POST
    @Path("recalculateNationalData")
    @Produces({MediaType.APPLICATION_JSON })
    public Response recalculateNationalData() {
        warehouse.complete(warehouse.getLastUpdate().plusMillis(1));
        nationalChartDataService.buildCache();
        return Response.ok().build();
    }

}
