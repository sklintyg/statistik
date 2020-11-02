package se.inera.statistics.service.schemavalidation;

import org.springframework.stereotype.Component;

@Component
class TsBasV6Validator extends RegisterCertificateValidator {

    public static final String MAJOR_VERSION = "6";

    TsBasV6Validator() {
        super("tstrk1007.v6.sch");
    }
}