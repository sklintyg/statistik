package se.inera.statistics.service.warehouse;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Range;

import java.util.Collection;

public class IntygCommonFilter {

    private final Range range;
    private final Collection<HsaIdEnhet> enheter;
    private final Collection<String> diagnoser;
    private final Collection<String> aldersgrupp;
    private final Collection<String> intygstyper;

    public IntygCommonFilter(Range range, Collection<HsaIdEnhet> enheter, Collection<String> diagnoser, Collection<String> aldersgrupp, Collection<String> intygstyper) {
        this.range = range;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
        this.aldersgrupp = aldersgrupp;
        this.intygstyper = intygstyper;
    }

    public Range getRange() {
        return range;
    }

    public Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    public Collection<String> getDiagnoser() {
        return diagnoser;
    }

    public Collection<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public Collection<String> getIntygstyper() {
        return intygstyper;
    }
}
