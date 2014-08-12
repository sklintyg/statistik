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

package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.StatisticsMalformedDocument;
import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;
import se.inera.statistics.service.warehouse.WidelineManager;

import static se.inera.statistics.service.helper.DocumentHelper.getForstaNedsattningsdag;
import static se.inera.statistics.service.helper.DocumentHelper.getPersonId;
import static se.inera.statistics.service.helper.DocumentHelper.getSistaNedsattningsdag;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

public class Processor {
    private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.date();

    @Autowired
    private ProcessorListener listener;

    @Autowired
    private SjukfallService sjukfallService;

    @Autowired
    private WidelineManager widelineManager;

    @Value("${skip.discrete.counter:false}")
    private boolean skipDiscreteCounter;

    private LocalDate cleanedup;
    private int processedCounter;

    public void accept(JsonNode utlatande, JsonNode hsa, long logId, String correlationId, EventType type) {
        SjukfallKey sjukfallKey = extractSjukfallKey(utlatande);

        SjukfallInfo sjukfallInfo = sjukfallService.register(sjukfallKey);

        ObjectNode anonymous = DocumentHelper.anonymize(utlatande);

        if (!skipDiscreteCounter) {
            listener.accept(sjukfallInfo, anonymous, hsa, logId);
        }

        ObjectNode preparedDoc = DocumentHelper.prepare(utlatande);
        widelineManager.accept(preparedDoc, hsa, logId, correlationId, type);

        processedCounter++;
    }

    public void setSkipDiscreteCounter(boolean skipDiscreteCounter) {
        this.skipDiscreteCounter = skipDiscreteCounter;
    }

    public int getProcessedCounter() {
        return processedCounter;
    }

    protected SjukfallKey extractSjukfallKey(JsonNode utlatande) {
        try {
            String personId = getPersonId(utlatande);
            String vardgivareId = getVardgivareId(utlatande);
            String startString = getForstaNedsattningsdag(utlatande);
            String endString = getSistaNedsattningsdag(utlatande);
            LocalDate start = FORMATTER.parseLocalDate(startString);
            LocalDate end = FORMATTER.parseLocalDate(endString);
            if (cleanedup == null) {
                cleanedup = start;
            } else if (start.isAfter(cleanedup)) {
                sjukfallService.expire(cleanedup);
                cleanedup = start;
            }
            return new SjukfallKey(personId, vardgivareId, start, end);
        } catch (RuntimeException e) {
            throw new StatisticsMalformedDocument("Could not parse dates from intyg.", e);
        }
    }

}
