package se.inera.statistics.web.service;

import com.google.common.base.Predicate;
import se.inera.statistics.service.warehouse.Fact;

import java.util.Collection;

class Filter {

    private Predicate<Fact> predicate;
    private Collection<String> enheter;
    private Collection<String> diagnoser;

    Filter(Predicate<Fact> predicate, Collection<String> enheter, Collection<String> diagnoser) {
        this.predicate = predicate;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
    }

    static Filter empty() {
        return new Filter(null, null, null);
    }

    Predicate<Fact> getPredicate() {
        return predicate;
    }

    Collection<String> getEnheter() {
        return enheter;
    }

    Collection<String> getDiagnoser() {
        return diagnoser;
    }

}
