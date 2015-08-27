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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

@Component
public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private HSAService service;

    @Transactional
    public JsonNode decorate(JsonNode doc, String documentId) {
        final JsonNode info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = extractHSAKey(doc);
            LOG.debug(key.toString());
            LOG.info("Fetching HSA data for " + documentId);
            final ObjectNode updatedHsaInfo = service.getHSAInfo(key, info);
            try {
                storeHSAInfo(documentId, updatedHsaInfo);
            } catch (javax.persistence.PersistenceException e) {
                // Expected error if multiple HSA is fetched for same key. Ignore.
                LOG.debug("Ignoring expected error", e);
            }
            return updatedHsaInfo;
        }
        return info;
    }

    private boolean missingData(JsonNode info) {
        return info == null || !(info.has(HSAService.HSA_INFO_ENHET)
                && info.has(HSAService.HSA_INFO_HUVUDENHET)
                && info.has(HSAService.HSA_INFO_PERSONAL)
                && info.has(HSAService.HSA_INFO_VARDGIVARE));
    }

    protected void storeHSAInfo(String documentId, JsonNode info) {
        if (info != null) {
            HSAStore entity = new HSAStore(documentId, info.toString());
            manager.merge(entity);
        }
    }

    public JsonNode getHSAInfo(String documentId) {
        HSAStore hsaStore = manager.find(HSAStore.class, documentId);
        if (hsaStore != null) {
            return JSONParser.parse(hsaStore.getData());
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

}
