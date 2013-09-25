package se.inera.statistics.service.processlog;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;

import com.fasterxml.jackson.databind.JsonNode;

public class Processor {

    @Autowired
    private ProcessorListener listener;

    @Autowired
    private SjukfallService sjukfallService;

    public void accept(JsonNode utlatande, JsonNode hsa) {
        SjukfallKey sjukfallKey = extractSjukfallKey(utlatande);
        String sjukfallId = sjukfallService.register(sjukfallKey);
    }

    protected SjukfallKey extractSjukfallKey(JsonNode utlatande) {
        return null;
    }

}
