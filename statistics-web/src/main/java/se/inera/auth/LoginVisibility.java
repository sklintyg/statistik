package se.inera.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class LoginVisibility {

    @Value("${hide.login.request.param.name}")
    private String hideParamName;

    @Value("${hide.login.request.param.value}")
    private String hideParamValue;

    @Autowired(required = true)
    private HttpServletRequest httpServletRequest;

    public LoginVisibility() {
    }

    public boolean isLoginVisible() {
        String header = httpServletRequest.getHeader(hideParamName);
        System.out.println(hideParamName + ":" + hideParamValue + ":" + header);
        return header == null || !header.equalsIgnoreCase(hideParamValue);
    }

}
