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

package se.inera.statistics.service.hsa;

import static se.inera.statistics.service.helper.DocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.DocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.helper.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class HSADecorator {
    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private HSAService service;

    @Transactional
    public JsonNode decorate(JsonNode doc, String documentId) {
        JsonNode info = getHSAInfo(documentId);
        if (info == null) {
            HSAKey key = extractHSAKey(doc);
            LOG.debug(key.toString());
            info = service.getHSAInfo(key);
            try {
                storeHSAInfo(documentId, info);
            } catch (javax.persistence.PersistenceException e) {
                // Expected error if multiple HSA is fetched for same key. Ignore.
                LOG.debug("Ignoring expected error", e);
            }
        }
        return info;
    }

    protected void storeHSAInfo(String documentId, JsonNode info) {
        if (info != null) {
            HSAStore entity = new HSAStore(documentId, info.toString());
            manager.persist(entity);
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
        String vardgivareId = getVardgivareId(document);
        String enhetId = getEnhetId(document);
        String lakareId = getLakarId(document);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

}
