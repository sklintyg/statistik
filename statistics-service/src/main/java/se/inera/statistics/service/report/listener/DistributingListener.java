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
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DistributingListener implements ProcessorListener {
    @Autowired
    private SjukfallPerKonListener sjukfallPerKonListener;

    @Autowired
    private SjukfallPerDiagnosgruppListener sjukfallPerDiagnosgruppListenerListener;

    @Autowired
    private SjukfallPerDiagnosundergruppListener sjukfallPerDiagnosundergruppListenerListener;

    @Autowired
    private AldersGruppListener aldersgruppListener;

    @Autowired
    private SjukfallslangdListener sjukfallsLangdListener;

    @Autowired
    private SjukskrivningsgradListener sjukskrivningsgradListener;

    @Autowired
    private SjukfallPerLanListener sjukfallPerLanListener;

    @Override
    @Transactional
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, long logId) {
        aldersgruppListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerKonListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerDiagnosgruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerDiagnosundergruppListenerListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallsLangdListener.accept(sjukfallInfo, utlatande, hsa);
        sjukskrivningsgradListener.accept(sjukfallInfo, utlatande, hsa);
        sjukfallPerLanListener.accept(sjukfallInfo, utlatande, hsa);
    }

}
