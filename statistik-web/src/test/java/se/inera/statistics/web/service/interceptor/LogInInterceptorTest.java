/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

package se.inera.statistics.web.service.interceptor;

import org.apache.cxf.interceptor.LoggingMessage;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertTrue;


/**
 * @author Magnus Ekstrand on 2017-11-17.
 */
public class LogInInterceptorTest {

    @InjectMocks
    LogInInterceptor testee = new LogInInterceptor();

    @Test
    public void testLoggingOfFileUploading() throws Exception {
        String payload = "------WebKitFormBoundary6UVVf08Ori6LNKyB\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"VG3_landsting (5).xlsx\"\n" +
                "Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\n" +
                "\n" +
                "QqK\n" +
                "^Ô¥_rels/.relsMK1ïýaîÝl+HÓ^ÐHýc2ûÁn2";

        LoggingMessage loggingMessage = new LoggingMessage("", "83");
        loggingMessage.getAddress().append("http://localhost:8080/api/landsting/fileupload?vgid=VG3");
        loggingMessage.getEncoding().append("ISO-8859-1");
        loggingMessage.getHttpMethod().append("POST");
        loggingMessage.getContentType().append("multipart/form-data; boundary=----WebKitFormBoundary6UVVf08Ori6LNKyB");
        loggingMessage.getHeader().append("{Accept=[application/json], accept-encoding=[gzip, deflate, br], Accept-Language=[sv,en-US;q=0.9,en;q=0.8,nb;q=0.7], Cache-Control=[no-cache], connection=[keep-alive], Content-Length=[9666], content-type=[multipart/form-data; boundary=----WebKitFormBoundary6UVVf08Ori6LNKyB], Cookie=[textwrapon=false; textautoformat=false; wysiwyg=textarea; __ngDebug=true; JSESSIONID=e65v9i48vm82gqxif3nepjis], DNT=[1], Host=[localhost:8080], Origin=[http://localhost:8080], Referer=[http://localhost:8080/], User-Agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36], X-Requested-With=[XMLHttpRequest]}");
        loggingMessage.getPayload().append(payload);

        String formattedLogMsg = testee.formatLoggingMessage(loggingMessage);

        // Verify
        int index1 = formattedLogMsg.indexOf("<rest of content skipped>");
        int index2 = formattedLogMsg.indexOf("-------------------------");

        assertTrue(index2 > index1);
    }

    @Test
    public void testLoggingOfCreatePdf() throws Exception {
        String payload = "pdf=JVBERi0xLjMKJf%2F%2F%2F%2F8KMTAgMCBvYmoKPDwKL1R5cGUgL0V4dEdTdGF0ZQovY2" +
                "EgMQo%2BPgplbmRvYmoKMTEgMCBvYmoKPDwKL1R5cGUgL0V4dEdTdGF0ZQovQ0EgMQo%2BPgplbmRvYmoKNyAwI" +
                "G9iago8PAovVHlwZSAvUGFnZQovUGFyZW50IDEgMCBSCi9NZWRpYUJveCBbMCAwIDU5NS4yOCA4NDEuODldCi9D" +
                "b250ZW50cyA1IDAgUgovUmVzb3VyY2VzIDY";

        LoggingMessage loggingMessage = new LoggingMessage("", "37");
        loggingMessage.getAddress().append("http://localhost:8080/api/pdf/create");
        loggingMessage.getEncoding().append("ISO-8859-1");
        loggingMessage.getHttpMethod().append("POST");
        loggingMessage.getContentType().append("application/x-www-form-urlencoded");
        loggingMessage.getPayload().append(payload);

        String formattedLogMsg = testee.formatLoggingMessage(loggingMessage);

        // Verify
        int index1 = formattedLogMsg.indexOf("<rest of content skipped>");
        int index2 = formattedLogMsg.indexOf("-------------------------");

        assertTrue(index2 > index1);
    }

}