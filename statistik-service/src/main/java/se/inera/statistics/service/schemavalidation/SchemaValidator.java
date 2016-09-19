/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.schemavalidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class SchemaValidator {

    @Autowired
    private LisuValidator lisuValidator;

    @Autowired
    private Fk7263sitValidator fk7263sitValidator;

    public ValidateXmlResponse validate(@Nonnull final String xmlContent) {
        if (xmlContent.toUpperCase().contains(">FK7263<")) {
            return fk7263sitValidator.validateSchematron(xmlContent);
        } else if (xmlContent.toUpperCase().contains(">LISU<")) {
            return lisuValidator.validateSchematron(xmlContent);
        } else {
            return new ValidateXmlResponse("Unknown certificate type");
        }
    }

}
