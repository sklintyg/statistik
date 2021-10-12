/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.schemavalidation;

import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.warehouse.IntygType;

@Component
public class SchemaValidator {

    @Autowired
    private LuseValidator luseValidator;

    @Autowired
    private LisjpValidator lisjpValidator;

    @Autowired
    private LuaeNaValidator luaeNaValidator;

    @Autowired
    private LuaeFsValidator luaeFsValidator;

    @Autowired
    private Fk7263sitValidator fk7263sitValidator;

    @Autowired
    private DbValidator dbValidator;

    @Autowired
    private DoiValidator doiValidator;

    @Autowired
    private Af00213Validator af00213Validator;

    @Autowired
    private Af00251Validator af00251Validator;

    @Autowired
    private TsBasV6Validator tsBasV6Validator;

    @Autowired
    private TsBasV7Validator tsBasV7Validator;

    @Autowired
    private TsDiabetesV3Validator tsDiabetesV3Validator;

    @Autowired
    private TsDiabetesV4Validator tsDiabetesV4Validator;

    @Autowired
    private Tstrk1009Validator tstrk1009Validator;

    @Autowired
    private Tstrk1062Validator tstrk1062Validator;

    @Autowired
    private Ag114Validator ag114Validator;

    @Autowired
    private Ag7804Validator ag7804Validator;

    public ValidateXmlResponse validate(@Nonnull final IntygType certificateType, final String certificateVersion,
        @Nonnull final String data) {
        switch (certificateType) {
            case FK7263:
                return fk7263sitValidator.validateSchematron(data);
            case LISJP:
                return lisjpValidator.validateSchematron(data);
            case LUAE_FS:
                return luaeFsValidator.validateSchematron(data);
            case LUAE_NA:
                return luaeNaValidator.validateSchematron(data);
            case LUSE:
                return luseValidator.validateSchematron(data);
            case DB:
                return dbValidator.validateSchematron(data);
            case DOI:
                return doiValidator.validateSchematron(data);
            case AF00213:
                return af00213Validator.validateSchematron(data);
            case AF00251:
                return af00251Validator.validateSchematron(data);
            case TSTRK1007:
                if (certificateVersion.startsWith(TsBasV7Validator.MAJOR_VERSION)) {
                    return tsBasV7Validator.validateSchematron(data);
                } else if (certificateVersion.startsWith(TsBasV6Validator.MAJOR_VERSION)) {
                    return tsBasV6Validator.validateSchematron(data);
                } else {
                    return new ValidateXmlResponse("Unknown version: " + certificateVersion
                        + "for certificate type: " + certificateType);
                }
            case TSTRK1009:
                return tstrk1009Validator.validateSchematron(data);
            case TSTRK1031:
                if (certificateVersion.startsWith(TsDiabetesV4Validator.MAJOR_VERSION)) {
                    return tsDiabetesV4Validator.validateSchematron(data);
                } else if (certificateVersion.startsWith(TsDiabetesV3Validator.MAJOR_VERSION)) {
                    return tsDiabetesV3Validator.validateSchematron(data);
                } else {
                    return new ValidateXmlResponse("Unknown version: " + certificateVersion
                        + "for certificate type: " + certificateType);
                }
            case TSTRK1062:
                return tstrk1062Validator.validateSchematron(data);
            case AG114:
                return ag114Validator.validateSchematron(data);
            case AG7804:
                return ag7804Validator.validateSchematron(data);
            default:
                return new ValidateXmlResponse("Unknown certificate type: " + certificateType);
        }
    }

}
