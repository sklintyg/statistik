/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
    private TsBasValidator tsBasValidator;

    @Autowired
    private TsDiabetesValidator tsDiabetesValidator;

    public ValidateXmlResponse validate(@Nonnull final IntygType typ, @Nonnull final String data) {
        switch (typ) {
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
            case TSTRK1007:
                return tsBasValidator.validateSchematron(data);
            case TSTRK1031:
                return tsDiabetesValidator.validateSchematron(data);
            default:
                return new ValidateXmlResponse("Unknown certificate type: " + typ);
        }
    }

}
