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

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;

import java.util.concurrent.TimeUnit;

/**
 * This class is meant to be used when a lot of calls to HSA is expected during a short period of time,
 * e.g. when re-processing all intygs where one or more calls to HSA is expected for each intyg.
 */
public class HsaWebServiceCached implements HsaWebService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaWebServiceCached.class);
    private static final int MAX_SIZE = 10000;

    @Autowired
    private HSAWebServiceCalls service;

    @Autowired
    private AuthorizationManagementService authorizationManagementService;

    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        service.setHsaLogicalAddress(hsaLogicalAddress);
    }

    public void callPing() {
        service.callPing();
    }

    private LoadingCache<String, Optional<GetStatisticsHsaUnitResponseType>> units = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsHsaUnitResponseType>>() {
                        public Optional<GetStatisticsHsaUnitResponseType> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsHsaUnit for: " + key);
                            return Optional.fromNullable(service.getStatisticsHsaUnit(key));
                        }
                    });

    @Override
    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String unitId) {
        try {
            return units.get(unitId).orNull();
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA unit call", e);
            return service.getStatisticsHsaUnit(unitId);
        }
    }

    private LoadingCache<String, Optional<GetStatisticsNamesResponseType>> names = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsNamesResponseType>>() {
                        public Optional<GetStatisticsNamesResponseType> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsNames for: " + key);
                            return Optional.fromNullable(service.getStatisticsNames(key));
                        }
                    });

    @Override
    public GetStatisticsNamesResponseType getStatisticsNames(String personId) {
        try {
            return names.get(personId).orNull();
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA statisticsNames call", e);
            return service.getStatisticsNames(personId);
        }
    }

    private LoadingCache<String, Optional<GetStatisticsPersonResponseType>> persons = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsPersonResponseType>>() {
                        public Optional<GetStatisticsPersonResponseType> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsPerson for: " + key);
                            return Optional.fromNullable(service.getStatisticsPerson(key));
                        }
                    });

    @Override
    public GetStatisticsPersonResponseType getStatisticsPerson(String personId) {
        try {
            return persons.get(personId).orNull();
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA statisticsPerson call", e);
            return service.getStatisticsPerson(personId);
        }
    }

    private LoadingCache<String, Optional<GetStatisticsCareGiverResponseType>> careGivers = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsCareGiverResponseType>>() {
                        public Optional<GetStatisticsCareGiverResponseType> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsCareGiver for: " + key);
                            return Optional.fromNullable(service.getStatisticsCareGiver(key));
                        }
                    });

    @Override
    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId) {
        try {
            return careGivers.get(careGiverId).orNull();
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA caregiver call", e);
            return service.getStatisticsCareGiver(careGiverId);
        }
    }

}
