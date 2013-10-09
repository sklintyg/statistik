package se.inera.statistics.web.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.security.Principal;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    @Context
    private HttpServletRequest context;

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public String getLoginInfo() {
        Principal user = context.getUserPrincipal();
        String name = "";
        String loggedIn = "false";
        if (user != null) {
            loggedIn = "true";
            name = user.getName();
        }

        return  "{\"name\":\"" + name + "\",\"loggedIn\":" + loggedIn + "}";
    }

}
