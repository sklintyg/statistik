package se.inera.statistics.service.warehouse;

class PersonifiedSjukfall {

    private int patient;
    private Sjukfall sjukfall;

    PersonifiedSjukfall(Fact line) {
        sjukfall = new Sjukfall(line);
        this.patient = line.getPatient();
    }

    PersonifiedSjukfall(Sjukfall previous, int patient) {
        sjukfall = previous;
        this.patient = patient;
    }

    int getPatient() {
        return patient;
    }

    Sjukfall getSjukfall() {
        return sjukfall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonifiedSjukfall that = (PersonifiedSjukfall) o;

        if (patient != that.patient) {
            return false;
        }
        if (sjukfall != null ? !sjukfall.equals(that.sjukfall) : that.sjukfall != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = patient;
        final int magicNumberGeneratedByIdea = 31;
        result = magicNumberGeneratedByIdea * result + (sjukfall != null ? sjukfall.hashCode() : 0);
        return result;
    }

}
