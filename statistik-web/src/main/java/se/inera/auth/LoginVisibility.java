/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
