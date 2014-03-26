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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.List;

@Component
public class SjukskrivningsgradListener extends GenericAbstractListener {

    public static final int FORMOGA_INGEN = 0;
    public static final int FORMOGA_25 = 25;
    public static final int FORMOGA_HALV = 50;
    public static final int FORMOGA_75 = 75;
    @Autowired
    private Sjukskrivningsgrad api;


    @Override
    boolean accept(GenericHolder token, String period) {
        List<Integer> arbetsformagor = DocumentHelper.getArbetsformaga(token.getUtlatande());
        for (Integer formaga: arbetsformagor) {
            String grad = arbetsformagaTillSjukskrivning(formaga);
            api.count(token.getEnhetId(), period, grad, Verksamhet.ENHET, token.getKon());
            api.count(token.getVardgivareId(), period, grad, Verksamhet.VARDGIVARE, token.getKon());
        }
        return false;
    }

    private String arbetsformagaTillSjukskrivning(Integer formaga) {
        switch(formaga) {
        case FORMOGA_INGEN: return "100";
        case FORMOGA_25: return "75";
        case FORMOGA_HALV: return "50";
        case FORMOGA_75: return "25";
        default:
            return "100";
        }
    }
}
