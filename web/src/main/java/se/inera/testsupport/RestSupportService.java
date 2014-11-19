package se.inera.testsupport;

import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Service("restSupportService")
public class RestSupportService {

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


}
