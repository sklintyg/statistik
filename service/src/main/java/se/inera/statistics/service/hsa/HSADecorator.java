/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.hsa;

import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getEnhetId;
import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getLakarId;
import static se.inera.statistics.service.helper.certificate.JsonDocumentHelper.getVardgivareId;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.statistics.service.helper.RegisterCertificateResolver;
import se.inera.statistics.service.helper.certificate.TsBasHelper;
import se.inera.statistics.service.helper.certificate.TsDiabetesHelper;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Component
public class HSADecorator {

    private static final Logger LOG = LoggerFactory.getLogger(HSADecorator.class);

    private static ObjectMapper hsaInfoMapper = getHsaInfoMapper();

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private HSAService service;

    @Autowired
    private RegisterCertificateResolver registerCertificateResolver;

    @Autowired
    private TsBasHelper tsBasHelper;

    @Autowired
    private TsDiabetesHelper tsDiabetesHelper;

    @Transactional
    public HsaInfo decorate(JsonNode doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = extractHSAKey(doc);
            return getAndUpdateHsaJson(documentId, info, key);
        }
        return info;
    }

    @Transactional
    public HsaInfo populateHsaData(RegisterCertificateType doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = registerCertificateResolver.extractHSAKey(doc);
            return getAndUpdateHsaJson(documentId, info, key);
        }
        return info;
    }

    @Transactional
    public HsaInfo populateHsaData(RegisterTSBasType doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = tsBasHelper.extractHSAKey(doc);
            return getAndUpdateHsaJson(documentId, info, key);
        }
        return info;
    }

    @Transactional
    public HsaInfo populateHsaData(RegisterTSDiabetesType doc, String documentId) {
        final HsaInfo info = getHSAInfo(documentId);
        if (missingData(info)) {
            HSAKey key = tsDiabetesHelper.extractHSAKey(doc);
            return getAndUpdateHsaJson(documentId, info, key);
        }
        return info;
    }

    private static ObjectMapper getHsaInfoMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        mapper.registerModule(new JavaTimeModule().addSerializer(LocalDateTime.class, new OurLocalDateTimeSerializer())
            .addDeserializer(LocalDateTime.class, new OurLocalDateTimeDeserializer()));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    @java.lang.SuppressWarnings("squid:S1067") // Expression complexity check ignored in Sonar
    private boolean missingData(HsaInfo info) {
        return info == null || !info.hasEnhet() || !info.hasHuvudenhet() || !info.hasPersonal() || !info.hasVardgivare();
    }

    protected void storeHSAInfo(String documentId, HsaInfo info) {
        if (info != null) {
            String infoJson = hsaInfoToJson(info);
            if (infoJson != null) {
                HSAStore entity = manager.find(HSAStore.class, documentId);

                if (entity == null) {
                    entity = new HSAStore(documentId, infoJson);
                } else {
                    entity.setData(infoJson);
                }

                manager.merge(entity);
            }
        }
    }

    public static String hsaInfoToJson(HsaInfo info) {
        try {
            return hsaInfoMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to convert HSA object to json. HSA info has not been stored");
            LOG.debug("Failed to convert HSA object to json. HSA info has not been stored", e);
            return null;
        }
    }

    public static HsaInfo jsonToHsaInfo(String data) {
        try {
            return hsaInfoMapper.readValue(data, HsaInfo.class);
        } catch (IOException e) {
            LOG.error("Failed to parse HSA info json");
            LOG.debug("Failed to parse HSA info json", e);
            return null;
        }
    }

    public HsaInfo getHSAInfo(String documentId) {
        HSAStore hsaStore = manager.find(HSAStore.class, documentId);
        if (hsaStore != null) {
            final String data = hsaStore.getData();
            return jsonToHsaInfo(data);
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

    /**
     * This dateformat is not recommended (ISO-8601 is default and recommended), but is used for backward compatibility.
     */
    private static class OurLocalDateTimeSerializer extends com.fasterxml.jackson.databind.JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
            jsonGenerator.writeString(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

    }

    private static class OurLocalDateTimeDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final boolean emptyField = p == null || p.getText() == null || p.getText().isEmpty();
            try {
                return emptyField ? null : LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(p.getText())).atStartOfDay();
            } catch (NumberFormatException | DateTimeParseException e) {
                throw new JsonProcessingException("Failed to parse: " + p.getText(), e) {
                };
            }
        }
    }

}
