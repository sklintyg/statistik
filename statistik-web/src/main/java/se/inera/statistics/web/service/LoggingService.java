/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.web.model.LogData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service("loggingService")
@Path("/logging")
public class LoggingService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    public LoggingService() { }

    @POST
    @Path("log")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response frontendLogging(@Context HttpServletRequest request, LogData logData) {
        String user = loginServiceUtil.isLoggedIn() ? getHsaIdForLoggedInUser(request).getId() : "Anonymous";
        LOG.info(user + " : " + logData.getMessage() + " [" + logData.getUrl() + "]");
        return Response.ok().build();
    }

    private HsaIdUser getHsaIdForLoggedInUser(@Context HttpServletRequest request) {
        return loginServiceUtil.getLoginInfo().getHsaId();
    }

}
