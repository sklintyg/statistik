/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
