package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10RangeType;

import java.util.HashSet;
import java.util.Set;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    private int start;
    private final int lan;
    private int end;
    private int realDays;
    private int intygCount;
    private final int kon;
    private int alder;
    private int diagnoskapitel;
    private int diagnosavsnitt;
    private int diagnoskategori;
    private int sjukskrivningsgrad;
    private Set<Lakare> lakare = new HashSet<>();
    private Sjukfall extending;

    public Sjukfall(Fact line) {
        start = line.getStartdatum();
        end = line.getStartdatum() + line.getSjukskrivningslangd() - 1;
        realDays = line.getSjukskrivningslangd();
        intygCount++;
        kon = line.getKon();
        alder = line.getAlder();
        diagnoskapitel = line.getDiagnoskapitel();
        diagnosavsnitt = line.getDiagnosavsnitt();
        diagnoskategori = line.getDiagnoskategori();
        sjukskrivningsgrad = line.getSjukskrivningsgrad();
        lan = line.getLan();
        final int lakarid = line.getLakarid();
        final Kon lakarKon = Kon.byNumberRepresentation(line.getLakarkon());
        final int lakaralder = line.getLakaralder();
        final int[] lakarbefattnings = line.getLakarbefattnings();
        this.lakare.add(new Lakare(lakarid, lakarKon, lakaralder, lakarbefattnings));
    }

    public Sjukfall(Sjukfall previous, Fact line) {
        this(line);
        start = previous.getStart();
        realDays = previous.getRealDays() + line.getSjukskrivningslangd();
        intygCount = previous.getIntygCount() + 1;
        extending = previous;
        lakare.addAll(previous.getLakare());
    }

    Sjukfall(Sjukfall previous, Sjukfall sjukfall) {
        start = previous.getStart();
        end = sjukfall.getEnd();
        realDays = previous.getRealDays() + sjukfall.getRealDays();
        intygCount = previous.getIntygCount() + sjukfall.getIntygCount();
        kon = sjukfall.kon;
        alder = sjukfall.getAlder();
        diagnoskapitel = sjukfall.getDiagnoskapitel();
        diagnosavsnitt = sjukfall.getDiagnosavsnitt();
        diagnoskategori = sjukfall.getDiagnoskategori();
        sjukskrivningsgrad = sjukfall.getSjukskrivningsgrad();
        lan = sjukfall.lan;
        lakare.addAll(previous.getLakare());
        lakare.addAll(sjukfall.getLakare());
    }

    public Kon getKon() {
        return Kon.byNumberRepresentation(kon);
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
        if (isExpired(line.getStartdatum())) {
            return newSjukfall(line);
        } else {
            return extendSjukfall(line);
        }
    }

    public Sjukfall newSjukfall(Fact line) {
        return new Sjukfall(line);
    }

    public Sjukfall extendSjukfall(Fact line) {
        return new Sjukfall(this, line);
    }

    public Sjukfall extendSjukfall(Sjukfall sjukfall) {
        return new Sjukfall(this, sjukfall);
    }

    private boolean isExpired(int datum) {
        return end + MAX_GAP + 1 < datum;
    }

    @Override
    public String toString() {
        return "Sjukfall{"
                + "start=" + start
                + ", end=" + end
                + ", realDays=" + realDays
                + ", intygCount=" + intygCount
                + '}';
    }

    public boolean in(int start, int end) {
        return !(this.end < start || this.start > end);
    }

    public int getDiagnoskategori() {
        return diagnoskategori;
    }

    public int getIcd10CodeForType(Icd10RangeType rangeType) {
        switch (rangeType) {
            case KAPITEL: return getDiagnoskapitel();
            case AVSNITT: return getDiagnosavsnitt();
            case KATEGORI: return getDiagnoskategori();
            default: throw new RuntimeException("Unknown range type: " + rangeType);
        }
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

    public int getDiagnosavsnitt() {
        return diagnosavsnitt;
    }

    public boolean isExtended() {
        return extending != null;
    }

    public String getLanskod() {
        return String.format("%1$02d", lan);
    }

    public Set<Lakare> getLakare() {
        return lakare;
    }

}
