package se.inera.statistics.service.hsa;

import java.util.List;

public interface HsaDataInjectable {

    void addPersonal(String id, HsaKon kon, int age, List<Integer> befattning);

}
