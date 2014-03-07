package se.inera.statistics.service.warehouse;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    int start;
    int end;
    int realDays;
    int intygCount;

    public Sjukfall(Fact line) {
        start = line.kalenderperiod;
        end = line.kalenderperiod + line.sjukskrivningslangd;
        realDays = line.sjukskrivningslangd;
        intygCount++;
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
        int lineEnd = line.kalenderperiod + line.sjukskrivningslangd;
        if (end + MAX_GAP + 1 < line.kalenderperiod) {
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
