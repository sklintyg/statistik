package se.inera.statistics.service.report.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.db.AldersgruppKey;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.HashMap;

@Component
public class AldersGruppListener extends RollingAbstractListener {

    private static final int MAX_CACHE_SIZE = 1000;
    private final HashMap<AldersgruppKey, AldersgruppValue> cache = new HashMap<>();

    @Autowired
    private AgeGroups ageGroups;

    protected void accept(GenericHolder token, String period, RollingLength length) {
        String group = AldersgroupUtil.RANGES.rangeFor(token.getAge()).getName();
        ageGroups.count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
        ageGroups.count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
//        count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
//        count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
    }

    private void count(String period, String enhetId, String group, RollingLength length, Verksamhet verksamhet, Sex kon) {
        AldersgruppKey key = new AldersgruppKey(period, enhetId, group, length.getPeriods());
        synchronized (cache) {
            if (cache.containsKey(key)) {
                cache.get(key).add(kon);

            } else {
                AldersgruppValue value = new AldersgruppValue(verksamhet, kon);
                cache.put(key, value);
            }
            if (cache.size() >= MAX_CACHE_SIZE) {
                ageGroups.countAll(cache);
            }
        }
    }

    public class AldersgruppValue {
        private final Verksamhet verksamhet;
        private int female;
        private int male;

        public AldersgruppValue(Verksamhet verksamhet, Sex kon) {
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
