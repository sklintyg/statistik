package se.inera.statistics.web.service;

import org.springframework.stereotype.Service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service("protectedChartService")
@Path("/update")
public class ProtectedUpdateService {

    @POST
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response register() {

        return Response.accepted().build();
    }

}
