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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.intyg.statistik.logging.PerformanceLogging;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.StaticData;
import se.inera.statistics.web.model.UserAccessInfo;
import se.inera.statistics.web.model.UserSettingsDTO;
import se.inera.statistics.web.service.LoginServiceUtil;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    public LoginInfoService() {
    }

    @GET
    @Path("getLoginInfo")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till login info")
    @PerformanceLogging(eventAction = "get-login-info", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public LoginInfo getLoginInfo() {
        return loginServiceUtil.getLoginInfo();
    }

    @GET
    @Path("getAppSettings")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till app-inställningar")
    @PerformanceLogging(eventAction = "get-app-settings", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public AppSettings getAppSettings() {
        return loginServiceUtil.getSettings();
    }

    @GET
    @Path("getUserAccessInfo/{vgId}")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till användarens rättigheter till information från vårdgivare")
    @PerformanceLogging(eventAction = "get-user-access-info", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public UserAccessInfo getUserAccessInfo(@PathParam("vgId") String vgId) {
        return loginServiceUtil.getUserAccessInfoForVg(new HsaIdVardgivare(vgId));
    }

    @GET
    @Path("getStaticData")
    @Produces({MediaType.APPLICATION_JSON})
    @PrometheusTimeMethod(
        help = "API-tjänst för åtkomst till statisk referensdata")
    @PerformanceLogging(eventAction = "get-static-data", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public StaticData getStaticData() {
        return loginServiceUtil.getStaticData();
    }

    @POST
    @Path("saveUserSettings")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @PerformanceLogging(eventAction = "save-user-settings", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
    public UserSettingsDTO saveUserSettings(UserSettingsDTO userSettingsDTO) {
        return loginServiceUtil.saveUserSettings(userSettingsDTO);
    }

}