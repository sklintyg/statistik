package se.inera.statistics.service.warehouse;

public class SjukfallWithSjukskrivningsgrad extends Sjukfall {
    private int sjukskrivningsgrad;

    public SjukfallWithSjukskrivningsgrad(Fact line) {
        super(line);
        sjukskrivningsgrad = line.getSjukskrivningsgrad();
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public SjukfallWithSjukskrivningsgrad join(Fact line) {
        return (SjukfallWithSjukskrivningsgrad) super.join(line);
    }

    @Override
    public Sjukfall newSjukfall(Fact line) {
        return new SjukfallWithSjukskrivningsgrad(line);
    }

    @Override
    public String toString() {
        return "SjukfallWithSjukskrivningsgrad{"
                + "sjukskrivningsgrad=" + sjukskrivningsgrad
                + '}' + super.toString();
    }
}
