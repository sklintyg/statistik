package se.inera.statistics.web.service;

import java.security.Principal;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import se.inera.statistics.web.model.Verksamhet;
import se.inera.statistics.web.model.LoginInfo;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    @Context
    private HttpServletRequest context;

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginInfo getLoginInfo() {
        Principal user = context.getUserPrincipal();
        String name = "";
        boolean loggedIn = false;
        if (user != null) {
            loggedIn = true;
            name = user.getName();
        }

        return new LoginInfo(name, loggedIn, Arrays.asList(new Verksamhet("verksamhet1", "Närhälsan i Småmåla"), new Verksamhet("verksamhet2", "Småmålas akutmottagning")));
    }

}
