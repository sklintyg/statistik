package se.inera.testsupport;

import se.inera.statistics.service.hsa.HsaKon;

import java.util.List;

public class Personal {

    private String id;
    private String firstName;
    private String lastName;
    private HsaKon kon;
    private int age;
    private List<Integer> befattning;

    public Personal(String id, String firstName, String lastName, HsaKon kon, int age, List<Integer> befattning) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public HsaKon getKon() {
        return kon;
    }

    public int getAge() {
        return age;
    }

    public List<Integer> getBefattning() {
        return befattning;
    }

}
