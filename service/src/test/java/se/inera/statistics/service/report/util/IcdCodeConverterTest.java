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

package se.inera.statistics.service.report.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kategori;

@ExtendWith(MockitoExtension.class)
class IcdCodeConverterTest {


    private Kategori category = mock(Kategori.class);
    private Avsnitt episode = mock(Avsnitt.class);
    @InjectMocks
    private IcdCodeConverter icdCodeConverter;

    @BeforeEach
    void setUp() {
        icdCodeConverter = new IcdCodeConverter();
    }

    @Nested
    class ToCode {

        @Test
        void shouldReturnNullIfNoMatchingCategories() {
            final var tsvContent = "\"A02.2\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";

            doReturn(false).when(category).contains(anyString());

            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));

            assertNull(result);
        }

        @Test
        void shouldNotConvertLineWithColumnDescriptions() {
            final var tsvContent = "\"Kod\"\t\"Giltig från\"\t\"Överordnad kod\"\t\"Titel\"\t\"Latin\"\t\"Beskrivning\"\t\"Exempel\"\t\""
                + "Innefattar\"\t\"Utesluter\"\t\"Anmärkning\"\t\"Kodningsinformation\"\t\"Innehåll\"\t\"Manifestation(*)/Etiologi(†)"
                + "\"\t\"Koppling Manifestation(*)/Etiologi(†)\"\t\"Ej huvuddiagnos\"\t\"Kodnivå - kodspecifikation\"";
            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));

            assertNull(result);
        }

        @Test
        void shouldNotConvertLineIfDiagnosisChapter() {
            final var tsvContent = "\"15\"\t\"1997-01-01\"\t\"\"\t\"Graviditet, förlossning och barnsängstid (O00-O99)"
                + "\"\t\"Graviditas, partus et puerperium\"\t\"\"\t\"\"\t\"\"\t\"Obstetrisk tetanus (A34)\"\t\"\"\t\"\"\t\""
                + "Detta kapitel innehåller följande avsnitt:<div>O00-O08 Graviditet som avslutas med abort</div><div>O10-O16 "
                + "Ödem, proteinuri och hypertoni under graviditet, förlossning och barnsängstid</div><div>O20-O29 Andra sjukdomar"
                + " hos den blivande modern i huvudsak sammanhängande med graviditeten</div><div>O30-O49 Vård under graviditet på "
                + "grund av problem relaterade till fostret och amnionhålan samt befarade förlossningsproblem"
                + "</div><div>O60-O75 Komplikationer"
                + " vid värkarbete och förlossning</div><div>O80-O84 Förlossning</div><div>O85-O92 Komplikationer "
                + "huvudsakligen sammanhängande"
                + " med barnsängstiden</div><div>O94-O99 Andra obstetriska tillstånd som ej klassificeras på annan plats</div>"
                + "\"\t\"\"\t\"\"\t\"\"\t\"Kapitelkod\"\n";

            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));
            assertNull(result);
        }

        @Test
        void shouldNotConvertLineIfDiagnosisGroup() {
            final var tsvContent = "\"A00-A09\"\t\"1997-01-01\"\t\"01\"\t\"Infektionssjukdomar utgående från mag-tarmkanalen\"\t\""
                + "Morbi infectiosi origine gastrointestinali\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"Avsnittskod,"
                + " kodintervall\"\n";

            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));
            assertNull(result);
        }

        @Test
        void shouldNotConvertInactiveDiagnosis() {
            final var tsvContent = "\"A02.2\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"Salmonellaartrit (M01.3*)"
                + "\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"";

            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));
            assertNull(result);
        }

        @Test
        void shouldReturnNullIfCodeHasLength3() {
            final var tsvContent = "\"A0.2\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";

            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));
            assertNull(result);
        }

        @Test
        void shouldConvertValidDiagnosis() {
            final var tsvContent = "\"A02.2\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";
            doReturn(true).when(category).contains(anyString());
            doReturn(new ArrayList<>()).when(category).getKods();
            final var result = icdCodeConverter.convertToCode(tsvContent, List.of(category));
            assertNotNull(result);
        }
    }

    @Nested
    class ToCategory {

        @Test
        void shouldReturnNullIfNoMatchingCategories() {
            final var tsvContent = "\"A0.2\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";

            doReturn(false).when(category).contains(anyString());

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));

            assertNull(result);
        }

        @Test
        void shouldNotConvertLineWithColumnDescriptions() {
            final var tsvContent = "\"Kod\"\t\"Giltig från\"\t\"Överordnad kod\"\t\"Titel\"\t\"Latin\"\t\"Beskrivning\"\t\"Exempel\"\t\""
                + "Innefattar\"\t\"Utesluter\"\t\"Anmärkning\"\t\"Kodningsinformation\"\t\"Innehåll\"\t\"Manifestation(*)/Etiologi(†)"
                + "\"\t\"Koppling Manifestation(*)/Etiologi(†)\"\t\"Ej huvuddiagnos\"\t\"Kodnivå - kodspecifikation\"";
            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));

            assertNull(result);
        }

        @Test
        void shouldNotConvertLineIfDiagnosisChapter() {
            final var tsvContent = "\"15\"\t\"1997-01-01\"\t\"\"\t\"Graviditet, förlossning och barnsängstid (O00-O99)"
                + "\"\t\"Graviditas, partus et puerperium\"\t\"\"\t\"\"\t\"\"\t\"Obstetrisk tetanus (A34)\"\t\"\"\t\"\"\t\""
                + "Detta kapitel innehåller följande avsnitt:<div>O00-O08 Graviditet som avslutas med abort</div><div>O10-O16 "
                + "Ödem, proteinuri och hypertoni under graviditet, förlossning och barnsängstid</div><div>O20-O29 Andra sjukdomar"
                + " hos den blivande modern i huvudsak sammanhängande med graviditeten</div><div>O30-O49 Vård under graviditet på "
                + "grund av problem relaterade till fostret och amnionhålan samt befarade förlossningsproblem"
                + "</div><div>O60-O75 Komplikationer"
                + " vid värkarbete och förlossning</div><div>O80-O84 Förlossning</div><div>O85-O92 Komplikationer "
                + "huvudsakligen sammanhängande"
                + " med barnsängstiden</div><div>O94-O99 Andra obstetriska tillstånd som ej klassificeras på annan plats</div>"
                + "\"\t\"\"\t\"\"\t\"\"\t\"Kapitelkod\"\n";

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));
            assertNull(result);
        }

        @Test
        void shouldNotConvertLineIfDiagnosisGroup() {
            final var tsvContent = "\"A00-A09\"\t\"1997-01-01\"\t\"01\"\t\"Infektionssjukdomar utgående från mag-tarmkanalen\"\t\""
                + "Morbi infectiosi origine gastrointestinali\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"Avsnittskod,"
                + " kodintervall\"\n";

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));
            assertNull(result);
        }

        @Test
        void shouldNotConvertInactiveCategory() {
            final var tsvContent = "\"A0.2\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"Salmonellaartrit (M01.3*)"
                + "\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"";

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));
            assertNull(result);
        }

        @Test
        void shouldReturnNullIfCodeDontHaveLength3() {
            final var tsvContent = "\"A0.24\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));
            assertNull(result);
        }

        @Test
        void shouldConvertValidCategory() {
            final var tsvContent = "\"A0.2\"\t\"1997-01-01\"\t\"A02\"\t\"Lokaliserade salmonellainfektioner"
                + "\"\t\"\"\t\"\"\t\"Renal tubulo-interstitiell sjukdom orsakad av salmonella (N16.0*)\"\t\"\""
                + "\t\"\"\t\"\"\t\"\"\t\"\"\t\"Etiologisk kod (†)\"\t\"\"\t\"\"\t\"Subkategorikod, fyrställig (fem tecken med punkt)\"";

            doReturn(true).when(episode).contains(anyString());
            doReturn(new ArrayList<>()).when(episode).getKategori();

            final var result = icdCodeConverter.convertToCategory(tsvContent, List.of(episode));
            assertNotNull(result);
        }
    }
}
