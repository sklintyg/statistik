/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    private HsaServiceStub hsaServiceStub;

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
