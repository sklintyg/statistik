package se.inera.statistics.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.LoginInfo;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    public LoginInfoService() { }

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginInfo getLoginInfo(@Context HttpServletRequest request) {
        return ServiceUtil.getLoginInfo(request);
    }
}
