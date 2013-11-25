package se.inera.auth;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class LoginVisibility {

    @Value("${hide.login.request.param.name}")
    private String hideParamName;

    @Value("${hide.login.request.param.value}")
    private Pattern hideParamValue;

    @Autowired(required = true)
    private HttpServletRequest httpServletRequest;

    public LoginVisibility() {
    }

    public boolean isLoginVisible() {
        @SuppressWarnings("unchecked")
        Enumeration<String> headers = httpServletRequest.getHeaders(hideParamName);
        if (headers == null) {
            return true;
        } else {
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();
                if (hideParamValue.matcher(header).matches()) {
                    return false;
                }
            }
        }
        return true;
    }

}
