package se.inera.statistics.service.warehouse;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    final int start;
    int end;
    int realDays;
    int intygCount;
    final int kon;

    public Sjukfall(Fact line) {
        start = line.startdatum;
        end = line.startdatum + line.sjukskrivningslangd - 1;
        realDays = line.sjukskrivningslangd;
        intygCount++;
        kon = line.getKon();
    }

    public int getKon() {
        return kon;
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public Sjukfall join(Fact line) {
        int lineEnd = line.startdatum + line.sjukskrivningslangd - 1;
        if (end + MAX_GAP + 1 < line.startdatum) {
            return new Sjukfall(line);
        } else {
            end = lineEnd;
            realDays += line.sjukskrivningslangd;
            intygCount++;
            return this;
        }
    }

    @Override
    public String toString() {
        return "Sjukfall{" +
                "start=" + start +
                ", end=" + end +
                ", realDays=" + realDays +
                ", intygCount=" + intygCount +
                '}';
    }
}
