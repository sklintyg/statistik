/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistics.web.model.LogData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

@Service("pdfService")
@Path("/pdf")
public class PdfService {

    @Autowired
    private LoggingService loggingService;

    public PdfService() {
    }

    @POST
    @Path("create")
    public Response pdf(@Context HttpServletRequest request, @FormParam("pdf") String pdf, @FormParam("name") String name,
            @FormParam("url") String url) {

        byte[] array = DatatypeConverter.parseBase64Binary(pdf);

        String cleanName = name.replaceAll("Å", "A").replaceAll("Ä", "A").replaceAll("Ö", "O")
                .replaceAll("å", "a").replaceAll("ä", "a").replaceAll("ö", "o")
                .replaceAll("[^A-Za-z0-9._]", "");

        Response.ResponseBuilder response = Response.ok()
                .entity(array)
                .type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .header("Content-Length", array.length)
                .header("Content-Disposition", "attachment; filename=" + cleanName);

        LogData logData = new LogData("Printing pdf", url);
        loggingService.frontendLogging(request, logData);

        return response.build();
    }
}
