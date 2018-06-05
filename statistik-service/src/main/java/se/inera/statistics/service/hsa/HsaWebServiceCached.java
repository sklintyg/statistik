/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;
import se.inera.statistics.hsa.model.GetStatisticsCareGiverResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsHsaUnitResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsNamesResponseDto;
import se.inera.statistics.hsa.model.GetStatisticsPersonResponseDto;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * This class is meant to be used when a lot of calls to HSA is expected during a short period of time,
 * e.g. when re-processing all intygs where one or more calls to HSA is expected for each intyg.
 */
public class HsaWebServiceCached implements HsaWebService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaWebServiceCached.class);
    private static final int MAX_SIZE = 10000;

    private LoadingCache<String, Optional<GetStatisticsHsaUnitResponseDto>> units = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsHsaUnitResponseDto>>() {
                        @Override
                        public Optional<GetStatisticsHsaUnitResponseDto> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsHsaUnit for: " + key);
                            return Optional.ofNullable(service.getStatisticsHsaUnit(key));
                        }
                    });

    private LoadingCache<String, Optional<GetStatisticsNamesResponseDto>> names = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsNamesResponseDto>>() {
                        @Override
                        public Optional<GetStatisticsNamesResponseDto> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsNames for: " + key);
                            return Optional.ofNullable(service.getStatisticsNames(key));
                        }
                    });

    private LoadingCache<String, Optional<GetStatisticsPersonResponseDto>> persons = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsPersonResponseDto>>() {
                        @Override
                        public Optional<GetStatisticsPersonResponseDto> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsPerson for: " + key);
                            return Optional.ofNullable(service.getStatisticsPerson(key));
                        }
                    });

    private LoadingCache<String, Optional<GetStatisticsCareGiverResponseDto>> careGivers = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, Optional<GetStatisticsCareGiverResponseDto>>() {
                        @Override
                        public Optional<GetStatisticsCareGiverResponseDto> load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsCareGiver for: " + key);
                            return Optional.ofNullable(service.getStatisticsCareGiver(key));
                        }
                    });

    @Autowired
    private HSAWebServiceCalls service;

    @Override
    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        service.setHsaLogicalAddress(hsaLogicalAddress);
    }

    @Override
    public void callPing() {
        service.callPing();
    }

    @Override
    public GetStatisticsHsaUnitResponseDto getStatisticsHsaUnit(String unitId) {
        try {
            return units.get(unitId).orElse(null);
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA unit call", e);
            return service.getStatisticsHsaUnit(unitId);
        }
    }

    @Override
    public GetStatisticsNamesResponseDto getStatisticsNames(String personId) {
        try {
            return names.get(personId).orElse(null);
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA statisticsNames call", e);
            return service.getStatisticsNames(personId);
        }
    }

    @Override
    public GetStatisticsPersonResponseDto getStatisticsPerson(String personId) {
        try {
            return persons.get(personId).orElse(null);
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA statisticsPerson call", e);
            return service.getStatisticsPerson(personId);
        }
    }

    @Override
    public GetStatisticsCareGiverResponseDto getStatisticsCareGiver(String careGiverId) {
        try {
            return careGivers.get(careGiverId).orElse(null);
        } catch (Exception e) {
            LOG.error("Failed to use cache for HSA caregiver call", e);
            return service.getStatisticsCareGiver(careGiverId);
        }
    }

}
