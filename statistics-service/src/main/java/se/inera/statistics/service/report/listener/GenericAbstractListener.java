package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public abstract class GenericAbstractListener extends AbstractListener<GenericHolder> {

    @Override
    GenericHolder setup(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, LocalDate start, LocalDate end) {
        return new GenericHolder(utlatande);
    }
}
