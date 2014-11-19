package se.inera.testsupport;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service("restSupportService")
public class RestSupportService {

    @GET
    @Path("now")
    @Produces({MediaType.APPLICATION_JSON })
    public Response getCurrentDateTime() {
        return Response.ok(System.currentTimeMillis()).build();
    }


}
