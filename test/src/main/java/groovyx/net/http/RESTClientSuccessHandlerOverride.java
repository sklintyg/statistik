/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package groovyx.net.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class RESTClientSuccessHandlerOverride extends RESTClient {

    public RESTClientSuccessHandlerOverride(Object defaultURI, Object defaultContentType) throws URISyntaxException {
        super(defaultURI, defaultContentType);
    }

    @Override
    protected HttpResponseDecorator defaultSuccessHandler(HttpResponseDecorator resp, Object data)
        throws ResponseParseException {
        try {
            //If response is streaming, buffer it in a byte array:
            if (data instanceof InputStream) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                IOGroovyMethods.leftShift(buffer, (InputStream) data);
                data = new ByteArrayInputStream(buffer.toByteArray());

            } else if (data instanceof Reader) {
                StringWriter buffer = new StringWriter();
                IOGroovyMethods.leftShift(buffer, data);
                data = new StringReader(buffer.toString());

            } else if (data instanceof Closeable) {
                log.warn("Parsed data is streaming, but will be accessible after "
                    + "the network connection is closed.  Use at your own risk!");
            }

            resp.setData(data);
            return resp;

        } catch (IOException ex) {
            throw new ResponseParseException(resp, ex);
        }
    }
}
