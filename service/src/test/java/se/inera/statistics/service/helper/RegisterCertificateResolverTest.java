/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.statistics.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.statistics.service.helper.certificate.Ag114RegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.Ag7804RegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.FkRegisterCertificateHelper;
import se.inera.statistics.service.helper.certificate.RegisterCertificateHelper;
import se.inera.statistics.service.warehouse.IntygType;

@ExtendWith(MockitoExtension.class)
class RegisterCertificateResolverTest {

    @InjectMocks
    RegisterCertificateResolver registerCertificateResolver;

    @Mock
    RegisterCertificateHelper registerCertificateHelper;

    @Mock
    FkRegisterCertificateHelper fkRegisterCertificateHelper;

    @Mock
    Ag7804RegisterCertificateHelper ag7804RegisterCertificateHelper;

    @Mock
    Ag114RegisterCertificateHelper ag114RegisterCertificateHelper;

    @Nested
    class FkHelper {

        @Test
        void shouldUseFkRegisterCertificateHelperIfCertificateTypeIsLisjp() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.LISJP);
            assertEquals(FkRegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseFkRegisterCertificateHelperIfCertificateTypeIsLuse() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.LUSE);
            assertEquals(FkRegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseFkRegisterCertificateHelperIfCertificateTypeIsFk7263() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.FK7263);
            assertEquals(FkRegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseFkRegisterCertificateHelperIfCertificateTypeIsLuaeNa() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.LUAE_NA);
            assertEquals(FkRegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseFkRegisterCertificateHelperIfCertificateTypeIsLuaeFs() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.LUAE_FS);
            assertEquals(FkRegisterCertificateHelper.class, result.getClass());
        }
    }

    @Nested
    class AG114Helper {

        @Test
        void shouldUseAg114RegisterCertificateHelperIfCertificateTypeIsAg114() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.AG114);
            assertEquals(Ag114RegisterCertificateHelper.class, result.getClass());
        }
    }

    @Nested
    class AG7804Helper {

        @Test
        void shouldUseAg7804RegisterCertificateHelperIfCertificateTypeIsAg7804() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.AG7804);
            assertEquals(Ag7804RegisterCertificateHelper.class, result.getClass());
        }
    }

    @Nested
    class CertificateHelper {

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsDb() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.DB);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsDoi() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.DOI);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsAf00213() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.AF00213);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsAf00251() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.AF00251);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsTstrk1007() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.TSTRK1007);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsTstrk1031() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.TSTRK1031);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsTstrk1009() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.TSTRK1009);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperIfCertificateTypeIsTstrk1062() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.TSTRK1062);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }

        @Test
        void shouldUseRegisterCertificateHelperAsDefault() {
            final var result = registerCertificateResolver.resolveIntygHelper(IntygType.FK7210);
            assertEquals(RegisterCertificateHelper.class, result.getClass());
        }
    }
}
