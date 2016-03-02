/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.service;

import org.springframework.stereotype.Service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

@Service("pdfService")
@Path("/pdf")
public class PdfService {

    public PdfService() { }

    @POST
    @Path("create")
    public Response pdf(@FormParam("pdf") String pdf, @FormParam("name") String name) {

        byte[] array = DatatypeConverter.parseBase64Binary(pdf);

        Response.ResponseBuilder response = Response.ok().entity(array).type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        response.header("Content-Disposition", "attachment; filename=" + name);

        return response.build();
    }
}

