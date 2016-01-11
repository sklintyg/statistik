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
package se.inera.statistics.hsa.stub;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import se.inera.statistics.hsa.model.Vardenhet;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BootstrapBean {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapBean.class);

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void bootstrapVardgivare() throws IOException {

        LOG.debug("Bootstrapping vardgivare for HSA stub ...");
        List<Resource> files = getResourceListing("bootstrap-vardgivare/*.json");
        for (Resource res : files) {
            addVardgivare(res);
        }

        LOG.debug("Bootstrapping medarbetare for HSA stub ...");
        files = getResourceListing("bootstrap-medarbetaruppdrag/*.json");
        for (Resource res : files) {
            addMedarbetaruppdrag(res);
        }
    }

    private List<Resource> getResourceListing(String classpathResourcePath) {
        try {
            PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
            return Arrays.asList(r.getResources(classpathResourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMedarbetaruppdrag(Resource res) throws IOException {
        Medarbetaruppdrag medarbetaruppdrag = objectMapper.readValue(res.getFile(), Medarbetaruppdrag.class);
        hsaServiceStub.getMedarbetaruppdrag().add(medarbetaruppdrag);
    }

    private void addVardgivare(Resource res) throws IOException {
        Vardenhet vardenhet = objectMapper.readValue(res.getFile(), Vardenhet.class);
        hsaServiceStub.getVardenhets().add(vardenhet);
    }
}
