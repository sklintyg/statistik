package se.inera.statistics.hsa.stub;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.hsa.model.Vardenhet;

/**
 * @author rlindsjo
 */
public class HsaStubRestApi {

    @Autowired
    HsaServiceStub hsaServiceStub;

    @POST
    @Path("/vardenhet")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUnit(List<Vardenhet> vardenhet) {
        hsaServiceStub.getVardenhets().addAll(vardenhet);
        return Response.ok().build();
    }

    @DELETE
    @Path("/vardenhet/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVardenhet(@PathParam("id") String id) {
        hsaServiceStub.deleteEnhet(id);
        return Response.ok().build();
    }

    @GET
    @Path("/vardgivare")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vardenhet> getVardgivare() {
        return hsaServiceStub.getVardenhets();
    }
}
