/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;

import se.inera.statistics.service.helper.DocumentHelper;
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
            isCacheFull = isCacheFull | accept(token, firstMonth, endMonth, prevEnd, length);
        }
        return isCacheFull;
    }

    private boolean accept(GenericHolder token, LocalDate firstMonth, LocalDate endMonth, LocalDate prevEnd, RollingLength length) {
        boolean isCacheFull = false;
        LocalDate startMonth = calcStart(firstMonth, prevEnd, length);
        LocalDate lastMonth = endMonth.plusMonths(length.getPeriods() - 1);
        for (LocalDate currentMonth = startMonth; !currentMonth.isAfter(lastMonth); currentMonth = currentMonth.plusMonths(1)) {
            String period = ReportUtil.toPeriod(currentMonth);
            isCacheFull = isCacheFull | accept(token, period, length);
        }
        if (prevEnd != null) {
            LocalDate regroupStart = intygFirstDay(token).getMonthOfYear() == prevEnd.getMonthOfYear() ? firstMonth.minusMonths(1) : firstMonth;
            LocalDate regroupEnd = prevEnd.withDayOfMonth(1).plusMonths(length.getPeriods() - 1);
            for (LocalDate currentMonth = regroupStart; !currentMonth.isAfter(regroupEnd); currentMonth = currentMonth.plusMonths(1)) {
                String period = ReportUtil.toPeriod(currentMonth);
                regroup(token, currentMonth, period, length);
            }
        }
        return isCacheFull;
    }

    private LocalDate intygFirstDay(GenericHolder token) {
        return LocalDate.parse(DocumentHelper.getForstaNedsattningsdag(token.getUtlatande()));
    }

    private LocalDate calcStart(LocalDate firstMonth, LocalDate prevEnd, RollingLength length) {
        return prevEnd == null ? firstMonth : prevEnd.plusMonths(length.getPeriods());
    }

    protected void regroup(GenericHolder token, LocalDate currentMonth, String period, RollingLength length) {
    }

    protected abstract boolean accept(GenericHolder token, String period, RollingLength length);

}
