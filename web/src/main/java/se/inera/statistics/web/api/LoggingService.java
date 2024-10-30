/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.api;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.ok;
import static jakarta.ws.rs.core.Response.status;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.monitoring.logging.UserAgentInfo;
import se.inera.intyg.infra.monitoring.logging.UserAgentParser;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.intyg.statistik.logging.PerformanceLogging;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.web.api.dto.MonitoringRequest;
import se.inera.statistics.web.model.LogData;
import se.inera.statistics.web.service.LoginServiceUtil;
import se.inera.statistics.web.service.monitoring.MonitoringLogService;

@Service("loggingService")
@Path("/logging")
public class LoggingService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    @Autowired
    private MonitoringLogService monitoringService;

    @Autowired
    private UserAgentParser userAgentParser;

    public LoggingService() {
    }

    @POST
    @Path("log")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(help = "API-tjänst för att logga från frontend app")
    @PerformanceLogging(eventAction = "frontend-logging", eventType = MdcLogConstants.EVENT_TYPE_CREATION)
    public Response frontendLogging(LogData logData) {
        String user = loginServiceUtil.isLoggedIn() ? getHsaIdForLoggedInUser().getId() : "Anonymous";
        LOG.info(user + " : " + logData.getMessage() + " [" + logData.getUrl() + "]");
        return Response.ok().build();
    }

    private HsaIdUser getHsaIdForLoggedInUser() {
        return loginServiceUtil.getLoginInfo().getHsaId();
    }

    @POST
    @Path("monitorlog")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(help = "API-tjänst för att monitoreringslogga från frontend app")
    @PerformanceLogging(eventAction = "frontend-monitor-logging", eventType = MdcLogConstants.EVENT_TYPE_CREATION)
    public Response monitoring(MonitoringRequest request, @HeaderParam(HttpHeaders.USER_AGENT) String userAgent) {
        if (request == null || !request.isValid()) {
            return status(BAD_REQUEST).build();
        }

        switch (request.getEvent()) {
            case SCREEN_RESOLUTION:
                final UserAgentInfo userAgentInfo = userAgentParser.parse(userAgent);
                monitoringService
                    .logBrowserInfo(userAgentInfo.getBrowserName(),
                        userAgentInfo.getBrowserVersion(),
                        userAgentInfo.getOsFamily(),
                        userAgentInfo.getOsVersion(),
                        request.getInfo().get(MonitoringRequest.WIDTH),
                        request.getInfo().get(MonitoringRequest.HEIGHT));
                break;
        }
        return ok().build();
    }
}