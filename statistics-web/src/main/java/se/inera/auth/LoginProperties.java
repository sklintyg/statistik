package se.inera.auth;

public class LoginProperties {
    private final String url;

    public LoginProperties() {
        if (ApplicationContextProvider.getApplicationContext().getEnvironment().acceptsProfiles("dev")) {
            url = "/login.jsp?secure=false";
        } else {
            url = "/saml/login";
        }
    }

    public String getUrl() {
        return url;
    }
}
