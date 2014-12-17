package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.Range;

import java.util.Collection;

public class SjukfallGroup {
    private final Range range;
    private final Collection<Sjukfall> sjukfall;

    public SjukfallGroup(Range range, Collection<Sjukfall> sjukfall) {
        this.range = range;
        this.sjukfall = sjukfall;
    }

    public Range getRange() {
        return range;
    }

    public Collection<Sjukfall> getSjukfall() {
        return sjukfall;
    }
}
