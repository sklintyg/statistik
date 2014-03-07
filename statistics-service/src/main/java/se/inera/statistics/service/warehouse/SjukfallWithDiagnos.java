package se.inera.statistics.service.warehouse;

public class SjukfallWithDiagnos extends Sjukfall {
    private int diagnoskapitel;

    public SjukfallWithDiagnos(Fact line) {
        super(line);
        this.diagnoskapitel = line.diagnoskapitel;
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
        int lineEnd = line.kalenderperiod + line.sjukskrivningslangd;
        if (end + MAX_GAP + 1 < line.kalenderperiod) {
            return new SjukfallWithDiagnos(line);
        } else {
            end = lineEnd;
            realDays += line.sjukskrivningslangd;
            diagnoskapitel = line.diagnoskapitel;
            intygCount++;
            return this;
        }
    }

    @Override
    public String toString() {
        return "SjukfallWithDiagnos{" +
                "diagnoskapitel=" + diagnoskapitel +
                '}' + super.toString();
    }
}
