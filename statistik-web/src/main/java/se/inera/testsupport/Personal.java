package se.inera.testsupport;

import se.inera.statistics.service.hsa.HsaKon;

public class Personal {

    private String id;
    private HsaKon kon;
    private int age;
    private int befattning;

    public Personal(String id, HsaKon kon, int age, int befattning) {
        this.id = id;
        this.kon = kon;
        this.age = age;
        this.befattning = befattning;
    }

    /**
     * For json mapper.
     */
    Personal() { }

    public String getId() {
        return id;
    }

    public HsaKon getKon() {
        return kon;
    }

    public int getAge() {
        return age;
    }

    public int getBefattning() {
        return befattning;
    }

}
