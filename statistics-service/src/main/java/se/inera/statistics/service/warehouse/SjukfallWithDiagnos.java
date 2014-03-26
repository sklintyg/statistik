package se.inera.statistics.service.warehouse;

public class SjukfallWithDiagnos extends Sjukfall {
    private int diagnoskapitel;

    public SjukfallWithDiagnos(Fact line) {
        super(line);
        this.diagnoskapitel = line.getDiagnoskapitel();
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    @Override
    public SjukfallWithDiagnos join(Fact line) {
        SjukfallWithDiagnos sjukfall = (SjukfallWithDiagnos) super.join(line);
        sjukfall.diagnoskapitel = line.getDiagnoskapitel();
        return sjukfall;
    }

    @Override
    public Sjukfall newSjukfall(Fact line) {
        return new SjukfallWithDiagnos(line);
    }

    @Override
    public String toString() {
        return "SjukfallWithDiagnos{"
                + "diagnoskapitel=" + diagnoskapitel
                + '}' + super.toString();
    }
}
