/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.statistics.service.warehouse.IntygType;

@ExtendWith(MockitoExtension.class)
class SchemaValidatorTest {

    @InjectMocks
    SchemaValidator schemaValidator;

    @Mock
    private LuseValidator luseValidator;

    @Mock
    private LisjpValidator lisjpValidator;

    @Mock
    private LuaeNaValidator luaeNaValidator;

    @Mock
    private LuaeFsValidator luaeFsValidator;

    @Mock
    private Fk7263sitValidator fk7263sitValidator;

    @Mock
    private DbValidator dbValidator;

    @Mock
    private DoiValidator doiValidator;

    @Mock
    private Af00213Validator af00213Validator;

    @Mock
    private Af00251Validator af00251Validator;

    @Mock
    private TsBasV6Validator tsBasV6Validator;

    @Mock
    private TsBasV7Validator tsBasV7Validator;

    @Mock
    private TsDiabetesV3Validator tsDiabetesV3Validator;

    @Mock
    private TsDiabetesV4Validator tsDiabetesV4Validator;

    @Mock
    private Tstrk1009Validator tstrk1009Validator;

    @Mock
    private Tstrk1062Validator tstrk1062Validator;

    @Mock
    private Ag114Validator ag114Validator;

    @Mock
    private Ag7804Validator ag7804Validator;


    @Test
    void shouldUseFk7263sitValidatorIfCertificateTypeIsFK7263() {
        schemaValidator.validate(IntygType.FK7263, "1", "data");
        verify(fk7263sitValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseLisjpValidatorIfCertificateTypeIsLISJP() {
        schemaValidator.validate(IntygType.LISJP, "1", "data");
        verify(lisjpValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseLuaeFsValidatorIfCertificateTypeIsLUAEFS() {
        schemaValidator.validate(IntygType.LUAE_FS, "1", "data");
        verify(luaeFsValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseLuaeNaValidatorIfCertificateTypeIsLUAEFS() {
        schemaValidator.validate(IntygType.LUAE_NA, "1", "data");
        verify(luaeNaValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseLuseValidatorValidatorIfCertificateTypeIsLUSE() {
        schemaValidator.validate(IntygType.LUSE, "1", "data");
        verify(luseValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseDbValidatorIfCertificateTypeIsDB() {
        schemaValidator.validate(IntygType.DB, "1", "data");
        verify(dbValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseDoiValidatorIfCertificateTypeIsDOI() {
        schemaValidator.validate(IntygType.DOI, "1", "data");
        verify(doiValidator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseAf00213ValidatorIfCertificateTypeIsAF00213() {
        schemaValidator.validate(IntygType.AF00213, "1", "data");
        verify(af00213Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseAf00251ValidatorIfCertificateTypeIsAF00213() {
        schemaValidator.validate(IntygType.AF00251, "1", "data");
        verify(af00251Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseTsBasV6ValidatorIfCertificateTypeIsTSTRK1007AndCertificateVersionStartsWith6() {
        schemaValidator.validate(IntygType.TSTRK1007, "6", "data");
        verify(tsBasV6Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseTsBasV7ValidatorIfCertificateTypeIsTSTRK1007AndCertificateVersionStartsWith7() {
        schemaValidator.validate(IntygType.TSTRK1007, "7", "data");
        verify(tsBasV7Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldReturnValidationErrorIfCertificateTypeIsTSTRK1007AndHasUnknownCertificateVersion() {
        final var result = schemaValidator.validate(IntygType.TSTRK1007, "11", "data");
        assertEquals(1, result.getValidationErrors().size());
    }

    @Test
    void shouldUseTstrk1009ValidatorIfCertificateTypeIsTSTRK1009() {
        schemaValidator.validate(IntygType.TSTRK1009, "1", "data");
        verify(tstrk1009Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseTsDiabetesV3ValidatorIfCertificateTypeIsTSTRK1031AndCertificateVersionStartsWith3() {
        schemaValidator.validate(IntygType.TSTRK1031, "3", "data");
        verify(tsDiabetesV3Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseTsDiabetesV4ValidatorIfCertificateTypeIsTSTRK1031AndCertificateVersionStartsWith4() {
        schemaValidator.validate(IntygType.TSTRK1031, "4", "data");
        verify(tsDiabetesV4Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldReturnValidationErrorIfCertificateTypeIsTSTRK1031AndHasUnknownCertificateVersion() {
        final var result = schemaValidator.validate(IntygType.TSTRK1031, "11", "data");
        assertEquals(1, result.getValidationErrors().size());
    }

    @Test
    void shouldUseTstrk1062ValidatorIfCertificateTypeIsTSTRK1062() {
        schemaValidator.validate(IntygType.TSTRK1062, "1", "data");
        verify(tstrk1062Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseAg114ValidatorValidatorIfCertificateTypeIsAG114() {
        schemaValidator.validate(IntygType.AG114, "1", "data");
        verify(ag114Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldUseAg7804ValidatorValidatorIfCertificateTypeIsAG7804() {
        schemaValidator.validate(IntygType.AG7804, "1", "data");
        verify(ag7804Validator, Mockito.times(1)).validateSchematron("data");
    }

    @Test
    void shouldReturnValidateXmlResponseAsDefault() {
        final var result = schemaValidator.validate(IntygType.FK7210, "1", "data");
        assertEquals(ValidateXmlResponse.class, result.getClass());
    }
}

