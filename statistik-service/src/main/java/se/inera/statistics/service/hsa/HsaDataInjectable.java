package se.inera.statistics.service.hsa;

/**
 * Created by fredrik on 04/12/14.
 */
public interface HsaDataInjectable {

    void addPersonal(String id, HsaKon kon, int age, int befattning);

}
