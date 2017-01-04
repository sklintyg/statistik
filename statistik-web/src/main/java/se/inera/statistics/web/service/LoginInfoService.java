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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.web.model.AppSettings;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.UserAccessInfo;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

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

}
