package se.inera.statistics.web.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.web.model.LoginInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class LoginInfoServiceTest {
    HttpServletRequest request;
    HttpSession session;

    @Before
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);

        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.doNothing().when(session).setAttribute(anyString(), anyObject());
    }

    @Test
    public void getNotLoggedInLoginInfoTest() {
        LoginInfoService loginInfoService = new LoginInfoService();

        LoginInfo info = loginInfoService.getLoginInfo(request);

        Assert.assertEquals(false, info.isLoggedIn());
    }
    @Test

    public void getLoginInfoTest() {
        LoginInfoService loginInfoService = new LoginInfoService();
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);
        Mockito.when(principal.getName()).thenReturn("Kalle");

        LoginInfo info = loginInfoService.getLoginInfo(request);

        Assert.assertEquals(true, info.isLoggedIn());
        Assert.assertEquals("Kalle", info.getName());
    }
}
