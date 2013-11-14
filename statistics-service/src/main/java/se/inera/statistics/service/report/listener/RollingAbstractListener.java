package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.ReportUtil;

public abstract class RollingAbstractListener extends GenericAbstractListener {

    @Override
    boolean accept(GenericHolder token, String period) {
        throw new NoSuchMethodError("Not implemented");
    }

    @Override
    boolean accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth) {
        boolean isCacheFull = false;
        LocalDate prevEnd = token.getSjukfallInfo().getPrevEnd();
        for (RollingLength length: RollingLength.values()) {
            isCacheFull = isCacheFull || accept(token, firstMonth, endMonth, prevEnd, length);
        }
        return isCacheFull;
    }

    private boolean accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth, LocalDate prevEnd, RollingLength length) {
        boolean isCacheFull = false;
        LocalDate startMonth = prevEnd == null ? firstMonth : prevEnd.plusMonths(length.getPeriods());
        LocalDate lastMonth = endMonth.plusMonths(length.getPeriods() - 1);
        for (LocalDate currentMonth = startMonth; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            String period = ReportUtil.toPeriod(currentMonth);
            isCacheFull = isCacheFull || accept(token, period, length);
        }
        return isCacheFull;
    }

    protected abstract boolean accept(GenericHolder token, String period, RollingLength length);

}
