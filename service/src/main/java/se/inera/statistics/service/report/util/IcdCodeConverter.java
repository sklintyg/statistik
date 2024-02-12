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

import java.util.Collection;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.util.Icd10.Avsnitt;
import se.inera.statistics.service.report.util.Icd10.Kategori;
import se.inera.statistics.service.report.util.Icd10.Kod;
import se.inera.statistics.service.report.util.Icd10.Range;

@Component
public class IcdCodeConverter {

    private static final String CODE_HEADING = "Kod";
    private static final int DIAGNOSIS_CODE_INDEX = 0;
    private static final int DIAGNOSIS_TITLE_INDEX = 3;

    public Kod convertToCode(String line, Collection<Kategori> categories) {
        return toCode(line, categories);
    }

    public Kategori convertToCategory(String line, Collection<Avsnitt> episode) {
        return toCategory(line, episode);
    }

    private Kod toCode(String line, Collection<Kategori> categories) {
        final var text = line.replace("\"", "").split("\t");
        if (isDiagnosisChapter(text) || isDiagnosisGroup(text) || isNotActive(text) || isHeading(text)) {
            return null;
        }

        final var code = getCode(text);

        if (code.length() == 3) {
            return null;
        }

        final var description = getDescription(text);
        final var kategori = find(code, categories);

        if (kategori == null) {
            return null;
        }

        return new Kod(code, description, kategori);
    }

    private Kategori toCategory(String line, Collection<Avsnitt> episodes) {
        final var text = line.replace("\"", "").split("\t");
        if (isDiagnosisChapter(text) || isDiagnosisGroup(text) || isNotActive(text) || isHeading(text)) {
            return null;
        }
        final var code = getCode(text);

        if (code.length() != 3) {
            return null;
        }
        
        final var description = getDescription(text);
        final var episode = find(code, episodes);

        if (episode == null) {
            return null;
        }

        return new Kategori(code, description, episode);
    }

    private static <T extends Range> T find(String id, Collection<T> ranges) {
        for (T range : ranges) {
            if (range.contains(id)) {
                return range;
            }
        }
        return null;
    }

    private static String getDescription(String[] text) {
        return text[DIAGNOSIS_TITLE_INDEX];
    }

    private static String getCode(String[] text) {
        return text[DIAGNOSIS_CODE_INDEX].replace(".", "");
    }

    private boolean isHeading(String[] text) {
        return text[0].equals(CODE_HEADING);
    }

    private boolean isNotActive(String[] line) {
        return line[1].isEmpty();
    }

    private boolean isDiagnosisGroup(String[] line) {
        return line[0].contains("-");
    }

    private boolean isDiagnosisChapter(String[] line) {
        return Character.isDigit(line[0].charAt(0));
    }
}
