package se.inera.statistics.service.hsa;

import java.util.List;

public interface HsaDataInjectable {

    void addPersonal(String id, String firstName, String lastName, HsaKon kon, int age, List<Integer> befattning);

    void setCountyForNextIntyg(String countyCode);

}
