/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.util.StringUtils;

/**
 * Success Logout handler that can use specific keyword from the targetUrlParameter and redirect the user to a specified
 * "timeoutLoggedOutUrl".
 *
 * This is useful when the logout redirect contains a fragment "#" which is normal in an SPA. The fragment part of the URL is never sent to
 * the server from the browser so this class can be used to mitigate that problem.
 *
 * @author Daniel Petersson
 */

public class StatistikLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    /**
     * The keyword used in the targetUrlParameter and should trigger the special handling.
     */
    private String timeoutTargetUrlParameterValue = "timeout";

    /**
     * The redirect URL to be used when the specified value from targetUrlParameter matches timeoutTargetUrlParameterValue.
     */
    private String timeoutLoggedOutUrl;


    @Override
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
