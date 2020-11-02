package se.inera.statistics.service.schemavalidation;

import org.springframework.stereotype.Component;

@Component
class TsBasV7Validator extends RegisterCertificateValidator {

    public static final String MAJOR_VERSION = "7";

    TsBasV7Validator() {
        super("tstrk1007.v7.sch");
    }
}