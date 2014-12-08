package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.Kon;

public class Lakare {

    private int id;
    private Kon kon;
    private int age;

    public Lakare(int lakareId, Kon kon, int age) {
        this.id = lakareId;
        this.kon = kon;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public Kon getKon() {
        return kon;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lakare lakare = (Lakare) o;

        if (id != lakare.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
