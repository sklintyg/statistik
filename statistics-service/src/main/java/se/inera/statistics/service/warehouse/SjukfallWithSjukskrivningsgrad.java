package se.inera.statistics.service.warehouse;

public class SjukfallWithSjukskrivningsgrad extends Sjukfall {
    private int sjukskrivningsgrad;

    public SjukfallWithSjukskrivningsgrad(WideLine line) {
        super(line);
        sjukskrivningsgrad = line.sjukskrivningsgrad;
    }

    /**
     * Checks if the given WideLine is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public SjukfallWithSjukskrivningsgrad join(WideLine line) {
        int lineEnd = line.kalenderperiod + line.sjukskrivningslangd;
        if (end + MAX_GAP + 1 < line.kalenderperiod) {
            return new SjukfallWithSjukskrivningsgrad(line);
        } else {
            end = lineEnd;
            realDays += line.sjukskrivningslangd;
            intygCount++;
            return this;
        }
    }

    @Override
    public String toString() {
        return "SjukfallWithSjukskrivningsgrad{" +
                "sjukskrivningsgrad=" + sjukskrivningsgrad +
                '}' + super.toString();
    }
}
