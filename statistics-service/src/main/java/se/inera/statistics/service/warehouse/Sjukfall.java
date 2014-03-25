package se.inera.statistics.service.warehouse;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    private final int start;
    private int end;
    private int realDays;
    private int intygCount;
    private final int kon;
    private int alder;
    private int diagnoskapitel;
    private int sjukskrivningsgrad;

    public Sjukfall(Fact line) {
        start = line.startdatum;
        end = line.startdatum + line.sjukskrivningslangd - 1;
        realDays = line.sjukskrivningslangd;
        intygCount++;
        kon = line.getKon();
        alder = line.getAlder();
        diagnoskapitel = line.getDiagnoskapitel();
        sjukskrivningsgrad = line.getSjukskrivningsgrad();
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
        if (isExpired(line.startdatum)) {
            return newSjukfall(line);
        } else {
            end = lineEnd;
            realDays += line.sjukskrivningslangd;
            intygCount++;
            if (alder != line.getAlder()) {
                alder = line.getAlder();
            }
            if (diagnoskapitel != line.getDiagnoskapitel()) {
                diagnoskapitel = line.getDiagnoskapitel();
            }
            sjukskrivningsgrad = line.getSjukskrivningsgrad();
            return this;
        }
    }

    public Sjukfall newSjukfall(Fact line) {
        return new Sjukfall(line);
    }

    private boolean isExpired(int datum) {
        return end + MAX_GAP + 1 < datum;
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

    public boolean in(int start, int end) {
        return !(this.end < start || this.start > end);
    }

    public int getAlder() {
        return alder;
    }

    public int getRealDays() {
        return realDays;
    }

    public int getIntygCount() {
        return intygCount;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }
}
