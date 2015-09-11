package se.inera.statistics.service.hsa;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonResponseType;
import se.inera.ifv.hsawsresponder.v3.GetMiuForPersonType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsCareGiverResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsHsaUnitResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsNamesResponseType;
import se.inera.ifv.hsawsresponder.v3.GetStatisticsPersonResponseType;
import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class is meant to be used when a lot of calls to HSA is expected during a short period of time,
 * e.g. when re-processing all intygs where one or more calls to HSA is expected for each intyg.
 */
public class HsaWebServiceCached implements HsaWebService {

    private static final Logger LOG = LoggerFactory.getLogger(HSAServiceImpl.class);
    private static final int MAX_SIZE = 10000;

    @Autowired
    private HSAWebServiceCalls service;

    public void setHsaLogicalAddress(String hsaLogicalAddress) {
        service.setHsaLogicalAddress(hsaLogicalAddress);
    }

    public void callPing() {
        service.callPing();
    }

    private LoadingCache<String, GetStatisticsHsaUnitResponseType> units = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, GetStatisticsHsaUnitResponseType>() {
                        public GetStatisticsHsaUnitResponseType load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsHsaUnit for: " + key);
                            return service.getStatisticsHsaUnit(key);
                        }
                    });

    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(String unitId) {
        try {
            return units.get(unitId);
        } catch (ExecutionException e) {
            LOG.error("Failed to use cache for HSA unit call", e);
            return service.getStatisticsHsaUnit(unitId);
        }
    }

    private LoadingCache<String, GetStatisticsNamesResponseType> names = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, GetStatisticsNamesResponseType>() {
                        public GetStatisticsNamesResponseType load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsNames for: " + key);
                            return service.getStatisticsNames(key);
                        }
                    });

    public GetStatisticsNamesResponseType getStatisticsNames(String personId) {
        try {
            return names.get(personId);
        } catch (ExecutionException e) {
            LOG.error("Failed to use cache for HSA statisticsNames call", e);
            return service.getStatisticsNames(personId);
        }
    }

    private LoadingCache<String, GetStatisticsPersonResponseType> persons = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, GetStatisticsPersonResponseType>() {
                        public GetStatisticsPersonResponseType load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsPerson for: " + key);
                            return service.getStatisticsPerson(key);
                        }
                    });

    public GetStatisticsPersonResponseType getStatisticsPerson(String personId) {
        try {
            return persons.get(personId);
        } catch (ExecutionException e) {
            LOG.error("Failed to use cache for HSA statisticsPerson call", e);
            return service.getStatisticsPerson(personId);
        }
    }

    private LoadingCache<GetMiuForPersonType, GetMiuForPersonResponseType> muiRights = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<GetMiuForPersonType, GetMiuForPersonResponseType>() {
                        public GetMiuForPersonResponseType load(GetMiuForPersonType key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call callMiuRights for: " + key);
                            return service.callMiuRights(key);
                        }
                    });

    public GetMiuForPersonResponseType callMiuRights(GetMiuForPersonType parameters) {
        try {
            return muiRights.get(parameters);
        } catch (ExecutionException e) {
            LOG.error("Failed to use cache for HSA muiRights call", e);
            return service.callMiuRights(parameters);
        }
    }

    private LoadingCache<String, GetStatisticsCareGiverResponseType> careGivers = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<String, GetStatisticsCareGiverResponseType>() {
                        public GetStatisticsCareGiverResponseType load(String key) throws Exception {
                            LOG.info("HSA call was not cached. Making remote call getStatisticsCareGiver for: " + key);
                            return service.getStatisticsCareGiver(key);
                        }
                    });

    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(String careGiverId) {
        try {
            return careGivers.get(careGiverId);
        } catch (ExecutionException e) {
            LOG.error("Failed to use cache for HSA caregiver call", e);
            return service.getStatisticsCareGiver(careGiverId);
        }
    }

}
