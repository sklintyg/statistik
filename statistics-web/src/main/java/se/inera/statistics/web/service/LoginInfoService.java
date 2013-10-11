package se.inera.statistics.web.service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.Business;
import se.inera.statistics.web.model.LoginInfo;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    public LoginInfoService() {}

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginInfo getLoginInfo(@Context HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        String name = "";
        boolean loggedIn = false;
        if (user != null) {
            loggedIn = true;
            name = user.getName();
        }
        List<Business> verksamhets = Arrays.asList(new Business("verksamhet1", "Närhälsan i Småmåla"), new Business("verksamhet2", "Småmålas akutmottagning"));
        request.getSession(true).setAttribute("verksamhets", verksamhets);
        return new LoginInfo(name, loggedIn, verksamhets);
    }

}
