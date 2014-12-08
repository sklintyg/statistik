package se.inera.statistics.service.hsa;

/**
 * Created by fredrik on 05/12/14.
 */
public enum HsaKon {

    MALE(1), FEMALE(2), UNKNOWN(0);

    private int hsaRepresantation;

    HsaKon(int hsaRepresantation) {
        this.hsaRepresantation = hsaRepresantation;
    }

    public int getHsaRepresantation() {
        return hsaRepresantation;
    }

}
