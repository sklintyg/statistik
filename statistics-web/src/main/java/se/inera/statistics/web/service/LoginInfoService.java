package se.inera.statistics.web.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import se.inera.auth.model.User;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.web.model.LoginInfo;
import se.inera.statistics.web.model.Verksamhet;

@Service("loginService")
@Path("/login")
public class LoginInfoService {

    public LoginInfoService() { }

    @GET
    @Path("getLoginInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginInfo getLoginInfo(@Context HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        String name = "";
        boolean loggedIn = false;
        List<Verksamhet> verksamhets = new ArrayList<>();

        if (user instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;
            if (token.getDetails() instanceof User) {
                User realUser = (User) token.getDetails();
                loggedIn = true;
                name = realUser.getName();
                for (Vardenhet enhet: realUser.getVardenhetList()) {
                    verksamhets.add(new Verksamhet(enhet.getId(), enhet.getNamn()));
                }
            }
        }
        request.getSession(true).setAttribute("verksamhets", verksamhets);
        return new LoginInfo(name, loggedIn, verksamhets);
    }

}
