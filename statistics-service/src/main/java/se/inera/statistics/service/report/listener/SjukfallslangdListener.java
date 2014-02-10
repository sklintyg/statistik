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

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.SjukfallslangdGrupp;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.report.util.Verksamhet;

@Component
public class SjukfallslangdListener extends RollingAbstractListener {

    @Autowired
    private SjukfallslangdGrupp langdGrupp;

    protected boolean accept(GenericHolder token, String period, RollingLength length) {
        String group = SjukfallslangdUtil.RANGES.rangeFor(token.getSjukfallInfo().getLangd()).getName();
        langdGrupp.count(period, token.getEnhetId(), group, length, Verksamhet.ENHET, token.getKon());
        langdGrupp.count(period, token.getVardgivareId(), group, length, Verksamhet.VARDGIVARE, token.getKon());
        return false;
    }

    @Override
    protected void regroup(GenericHolder token, LocalDate currentMonth, String period, RollingLength length) {
        int days = Days.daysBetween(token.getSjukfallInfo().getStart(), token.getSjukfallInfo().getPrevEnd()).getDays() + 1;
        String group = SjukfallslangdUtil.RANGES.rangeFor(days).getName();
        String newGroup = SjukfallslangdUtil.RANGES.rangeFor(token.getSjukfallInfo().getLangd()).getName();
        if (!newGroup.equals(group)) {
            langdGrupp.recount(period, token.getEnhetId(), group, newGroup, length, Verksamhet.ENHET, token.getKon());
            langdGrupp.recount(period, token.getVardgivareId(), group, newGroup, length, Verksamhet.VARDGIVARE, token.getKon());
        }
    }

}
