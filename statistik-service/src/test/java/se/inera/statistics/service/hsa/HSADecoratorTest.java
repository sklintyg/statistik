/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.hsa;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.JSONSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.inera.statistics.service.helper.DocumentHelper;

@RunWith(MockitoJUnitRunner.class)
public class HSADecoratorTest {

    @Mock
    private HSAService hsaService;// = Mockito.mock(HSAService.class);

    @Mock
    private EntityManager manager;

    @InjectMocks
    private HSADecorator hsaDecorator = new HSADecorator();

    @Test
    public void decorate_document() throws IOException {
        String docId = "aaa";
        JsonNode doc = new ObjectMapper().readTree(JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1));

        hsaDecorator.decorate(doc, docId);

        verify(hsaService).getHSAInfo(any(HSAKey.class));
    }
}
