package se.inera.testability;


import static se.inera.config.WebSecurityConfig.TESTABILITY_PROFILE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import se.inera.auth.FakeCredentials;

@Service
@RequiredArgsConstructor
@Profile(TESTABILITY_PROFILE)
@Path("/testability")
public class TestabilityController {

    private final FakeLoginService fakeAuthService;
    @Context
    private HttpServletRequest request;

    @POST
    @Path("/fake")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void login(@RequestBody FakeCredentials fakeCredentials) {
        fakeAuthService.login(fakeCredentials, request);
    }

    @POST
    @Path("/logout")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void logout() {
        fakeAuthService.logout(request.getSession(false));
    }

}