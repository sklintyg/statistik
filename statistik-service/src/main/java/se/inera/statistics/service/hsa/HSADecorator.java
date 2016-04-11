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
package se.inera.statistics.service.hsa;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private HSAService service;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    @Transactional
    public JsonNode decorate(JsonNode doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = extractHSAKey(doc);
            return toJsonNode(getAndUpdateHsaJson(documentId, info, key));
        }
        return toJsonNode(info);
    }

    @Transactional
    public JsonNode populateHsaData(RegisterCertificateType doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = extractHSAKey(doc);
            return toJsonNode(getAndUpdateHsaJson(documentId, info, key));
        }
        return toJsonNode(info);
    }

    /**
     * Temporary method to be used in the first step of removing HSA JsonNodes from the java code.
     */
    public static JsonNode toJsonNode(Object data) {
        ObjectMapper mapper = getHsaInfoMapper();
        final String infoJson;
        try {
            infoJson = mapper.writeValueAsString(data);
            return JSONParser.parse(infoJson);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to convert Object to json");
            return null;
        }
    }

    private static ObjectMapper getHsaInfoMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return mapper;
    }

    private HsaInfo getAndUpdateHsaJson(String documentId, HsaInfo info, HSAKey key) {
        LOG.debug(key.toString());
        LOG.info("Fetching HSA data for " + documentId);
        final HsaInfo updatedHsaInfo = service.getHSAInfo(key, info);
        try {
            storeHSAInfo(documentId, updatedHsaInfo);
        } catch (javax.persistence.PersistenceException e) {
            // Expected error if multiple HSA is fetched for same key. Ignore.
            LOG.debug("Ignoring expected error", e);
        }
        return updatedHsaInfo;
    }

    private boolean missingData(HsaInfo info) {
        return info == null || !info.hasEnhet() || !info.hasHuvudenhet() || !info.hasPersonal() || !info.hasVardgivare();
    }

    protected void storeHSAInfo(String documentId, HsaInfo info) {
        if (info != null) {
            ObjectMapper mapper = getHsaInfoMapper();
            try {
                final String infoJson = mapper.writeValueAsString(info);
                HSAStore entity = new HSAStore(documentId, infoJson);
                manager.merge(entity);
            } catch (JsonProcessingException e) {
                LOG.error("Failed to convert HSA object to json. HSA info has not been stored");
            }
        }
    }

    public HsaInfo getHSAInfo(String documentId) {
        HSAStore hsaStore = manager.find(HSAStore.class, documentId);
        if (hsaStore != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(hsaStore.getData(), HsaInfo.class);
            } catch (IOException e) {
                LOG.error("Failed to parse HSA info json");
                return null;
            }
        } else {
            return null;
        }
    }

    protected HSAKey extractHSAKey(JsonNode document) {
        final DocumentHelper.IntygVersion version = DocumentHelper.getIntygVersion(document);
        String vardgivareId = getVardgivareId(document, version);
        String enhetId = getEnhetId(document, version);
        String lakareId = getLakarId(document, version);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

    protected HSAKey extractHSAKey(RegisterCertificateType document) {
        String vardgivareId = registerCertificateHelper.getVardgivareId(document);
        String enhetId = registerCertificateHelper.getEnhetId(document);
        String lakareId = registerCertificateHelper.getLakareId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
