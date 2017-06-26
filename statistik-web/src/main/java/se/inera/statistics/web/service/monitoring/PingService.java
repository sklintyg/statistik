package se.inera.statistics.web.service.monitoring;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by eriklupander on 2017-06-26.
 */
@Service("pingService")
@Path("/ping")
public class PingService {

    @GET
    @Path("/")
    @Produces("text/plain")
    public Response ping() {
        return Response.ok("OK")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET")
                .header("Access-Control-Max-Age", "1209600")
                .build();
    }
}
