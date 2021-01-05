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

import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.schematron.xslt.SchematronResourceSCH;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public abstract class RegisterCertificateValidator implements SchematronValidator {

    private SchematronResourceSCH schematronResource;

    RegisterCertificateValidator(@Nonnull final String location) {
        schematronResource = SchematronResourceSCH.fromClassPath(location);
        if (!schematronResource.isValidSchematron()) {
            throw new IllegalArgumentException("Invalid Schematron: " + location);
        }
    }

    @Override
    public ValidateXmlResponse validateSchematron(@Nonnull final String xmlContent) {
        try {
            final Source source = new StreamSource(new StringReader(xmlContent));
            SchematronOutputType valResult = schematronResource.applySchematronValidationToSVRL(source);
            if (valResult == null) {
                return new ValidateXmlResponse(Collections.singletonList("Failed to validate xml. Result is null."));
            }
            final List<SVRLFailedAssert> allFailedAssertions = SVRLHelper.getAllFailedAssertions(valResult);
            if (allFailedAssertions.size() > 0) {
                final List<String> errorMsgs = allFailedAssertions.stream()
                    .map(fa -> String.format("TEST: %s, MSG: %s, ROLE: %s, LOCATION: %s, ALL: %s", fa.getTest(), fa.getText(),
                        fa.getRole(), fa.getLocation(), fa.toString()))
                    .collect(Collectors.toList());
                return new ValidateXmlResponse(errorMsgs);
            } else {
                return ValidateXmlResponse.newValidResponse();
            }
        } catch (Exception e) {
            return new ValidateXmlResponse(Collections.singletonList("Failed to validate xml"));
        }
    }

}
