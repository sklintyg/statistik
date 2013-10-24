package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.ReportUtil;

public abstract class RollingAbstractListener extends GenericAbstractListener {

    @Override
    void accept(GenericHolder token, String period) {
        throw new NoSuchMethodError("Not implemented");
    }

    @Override
    void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth) {
        LocalDate prevEnd = token.getSjukfallInfo().getPrevEnd();
        for (RollingLength length: RollingLength.values()) {
            accept(token, firstMonth, endMonth, prevEnd, length);
        }
    }

    private void accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth, LocalDate prevEnd, RollingLength length) {
        LocalDate startMonth = prevEnd == null ? firstMonth : prevEnd.plusMonths(length.getPeriods());
        LocalDate lastMonth = endMonth.plusMonths(length.getPeriods() - 1);
        for (LocalDate currentMonth = startMonth; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            String period = ReportUtil.toPeriod(currentMonth);
            accept(token, period, length);
        }
    }

    protected abstract void accept(GenericHolder token, String period, RollingLength length);

}
