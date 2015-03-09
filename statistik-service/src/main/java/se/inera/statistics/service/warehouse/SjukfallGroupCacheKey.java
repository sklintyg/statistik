package se.inera.statistics.service.warehouse;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;

class SjukfallGroupCacheKey {

    private final LocalDate from;
    private final int periods;
    private final int periodSize;
    private final Aisle aisle;
    private final Predicate<Fact> filter;
    private final boolean useOriginalSjukfallStart;
    private final String key;

    public SjukfallGroupCacheKey(LocalDate from, int periods, int periodSize, Aisle aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        this.aisle = aisle;
        this.filter = filter;
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        this.key = from.toDate().getTime() + "_" + periods + "_" + periodSize + "_" + aisle.getVardgivareId() + "_" + useOriginalSjukfallStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SjukfallGroupCacheKey that = (SjukfallGroupCacheKey) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public LocalDate getFrom() {
        return from;
    }

    public int getPeriods() {
        return periods;
    }

    public int getPeriodSize() {
        return periodSize;
    }

    public Aisle getAisle() {
        return aisle;
    }

    public Predicate<Fact> getFilter() {
        return filter;
    }

    public boolean isUseOriginalSjukfallStart() {
        return useOriginalSjukfallStart;
    }

    public String getKey() {
        return key;
    }
}
