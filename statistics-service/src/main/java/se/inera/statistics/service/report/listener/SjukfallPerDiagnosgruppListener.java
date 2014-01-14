package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.Diagnosgrupp;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.repository.DiagnosisGroupKey;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.HashMap;
import java.util.Map;

@Component
public class SjukfallPerDiagnosgruppListener extends GenericAbstractListener {
    private static final int DEFAULT_MAX_CACHE_SIZE = 1000;
    private static int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
    private final Map<DiagnosisGroupKey, DiagnosgruppValue> cache = new HashMap<>();

    @Autowired
    private Diagnosgrupp diagnosisgroupPersistenceHandler;

    @Override
    boolean accept(GenericHolder token, String period) {
        diagnosisgroupPersistenceHandler.count(token.getEnhetId(), period, token.getDiagnosgrupp(), Verksamhet.ENHET, token.getKon());
        diagnosisgroupPersistenceHandler.count(token.getVardgivareId(),
        period, token.getDiagnosgrupp(), Verksamhet.VARDGIVARE,
        token.getKon());
        return false;
//        return count(token.getEnhetId(), period, token.getDiagnosgrupp(), Verksamhet.ENHET, token.getKon())
//                || count(token.getEnhetId(), period, token.getDiagnosgrupp(), Verksamhet.VARDGIVARE, token.getKon());
    }

    private boolean count(String hsaId, String period, String diagnosgrupp, Verksamhet verksamhet, Sex kon) {
        boolean isCacheFull;
        DiagnosisGroupKey key = new DiagnosisGroupKey(period, hsaId, diagnosgrupp);
        synchronized (cache) {
            if (cache.containsKey(key)) {
                cache.get(key).add(kon);

            } else {
                DiagnosgruppValue value = new DiagnosgruppValue(verksamhet, kon);
                cache.put(key, value);
            }
            isCacheFull = cache.size() >= maxCacheSize;
        }
        return isCacheFull;
    }

    public void persistCache() {
        synchronized (cache) {
            if (cache.size() > 0) {
                diagnosisgroupPersistenceHandler.countAll(cache);
            }
        }
    }

    public static void setMaxCacheSize(int newSize) {
        maxCacheSize = newSize;
    }

    public class DiagnosgruppValue {
        private final Verksamhet verksamhet;
        private int female;
        private int male;

        public DiagnosgruppValue(Verksamhet verksamhet, Sex kon) {
            this.verksamhet = verksamhet;
            this.female = kon.equals(Sex.Female) ? 1 : 0;
            this.male = kon.equals(Sex.Male) ? 1 : 0;
        }

        public Verksamhet getVerksamhet() {
            return verksamhet;
        }

        public int getFemale() {
            return female;
        }

        public int getMale() {
            return male;
        }

        public void add(Sex kon) {
            if (kon.equals(Sex.Female)) {
                female++;
            } else {
                male++;
            }
        }
    }
}
