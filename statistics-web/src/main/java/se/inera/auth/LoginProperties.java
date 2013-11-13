package se.inera.auth;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class LoginProperties {
    private final String url;

    public LoginProperties() {
        if (ApplicationContextProvider.getApplicationContext().getEnvironment().acceptsProfiles("dev")) {
            //Resource resource = new ClassPathResource("/default-dev.properties");
            url = "/login.jsp?secure=false";
        } else {
            url = "/saml/login";
        }

//        String u = "";
//        try {
//            Properties props = PropertiesLoaderUtils.loadProperties(resource);
//            u = props.getProperty("login.url");
//        } catch (IOException e) {
//            u = "/login/login.html";
//        }
//        url = u;
    }

    public String getUrl() {
        return url;
    }
}
