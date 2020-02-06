package se.inera.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatistikLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private String timeoutTargetUrlParameterValue = "timeout";
    private String timeoutLoggedOutUrl;


    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        String targetUrlParameter = getTargetUrlParameter();
        if (targetUrlParameter != null) {
            String targetUrlParameterValue = request.getParameter(targetUrlParameter);

            if (StringUtils.hasText(targetUrlParameterValue) && timeoutTargetUrlParameterValue.equals(targetUrlParameterValue)) {
                // Timeout logout performed. Create url for correct logout page
                if (response.isCommitted()) {
                    logger.debug("Response has already been committed. Unable to redirect to "
                            + targetUrlParameterValue);
                    return;
                }

                getRedirectStrategy().sendRedirect(request, response, timeoutLoggedOutUrl);
            } else {
                super.handle(request, response, authentication);
            }
        } else {
            super.handle(request, response, authentication);
        }


    }

    public void setTimeoutTargetUrlParameterValue(String timeoutTargetUrlParameterValue) {
        this.timeoutTargetUrlParameterValue = timeoutTargetUrlParameterValue;
    }

    public void setTimeoutLoggedOutUrl(String timeoutLoggedOutUrl) {
        this.timeoutLoggedOutUrl = timeoutLoggedOutUrl;
    }
}
