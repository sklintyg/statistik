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
package se.inera.statistics.web.service.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.StaticData;
import se.inera.statistics.web.model.UserAccessInfo;
import se.inera.statistics.web.service.LoginServiceUtil;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInfoService.class);

    @Autowired
    private LoginServiceUtil loginServiceUtil;

    public LoginInfoService() { }

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginInfo getLoginInfo() {
        return loginServiceUtil.getLoginInfo();
    }

    @GET
    @Path("getAppSettings")
    @Produces({ MediaType.APPLICATION_JSON })
    public AppSettings getAppSettings(@Context HttpServletRequest request) {
        return loginServiceUtil.getSettings();
    }

    @GET
    @Path("getUserAccessInfo/{vgId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public UserAccessInfo getUserAccessInfo(@Context HttpServletRequest request, @PathParam("vgId") String vgId) {
        return loginServiceUtil.getUserAccessInfoForVg(request, new HsaIdVardgivare(vgId));
    }

    @GET
    @Path("getStaticData")
    @Produces({ MediaType.APPLICATION_JSON })
    public StaticData getStaticData() {
        LOG.info("Calling getStaticData");
        return loginServiceUtil.getStaticData();
    }

}
