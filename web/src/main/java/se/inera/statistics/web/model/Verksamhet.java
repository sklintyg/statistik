/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

import static org.apache.commons.text.translate.UnicodeEscaper.above;
import static org.apache.commons.text.translate.UnicodeEscaper.between;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.io.Serializable;
import java.util.Set;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.UnicodeEscaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Verksamhet implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String vardgivarId;
    private final String vardgivarName;
    private final String lansId;
    private final String lansName;
    private final String kommunId;
    private final String kommunName;
    private final Set<VerksamhetsTyp> verksamhetsTyper;
    private final String vardenhetId;

    private static final CharSequenceTranslator ESCAPER = UnicodeEscaper.below('-').with(excludeBetween('-', '0'), excludeBetween('9', 'A'),
        excludeBetween('Z', 'a'), above('z'));

    // CHECKSTYLE:OFF ParameterNumber
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public Verksamhet(HsaIdEnhet id, String name, HsaIdVardgivare vardgivarId, String vardgivarName, String lansId, String lansName,
        String kommunId, String kommunName, Set<VerksamhetsTyp> verksamhetsTyper, String vardenhetId) {
        this.id = id.getId();
        this.name = name;
        this.vardgivarId = vardgivarId.getId();
        this.vardgivarName = vardgivarName;
        this.lansId = lansId;
        this.lansName = lansName;
        this.kommunId = kommunId;
        this.kommunName = kommunName;
        this.verksamhetsTyper = verksamhetsTyper;
        this.vardenhetId = vardenhetId;
    }
    // CHECKSTYLE:ON ParameterNumber

    private static UnicodeEscaper excludeBetween(int codepointLow, int codepointHigh) {
        return between(codepointLow + 1, codepointHigh - 1);
    }

    public HsaIdEnhet getId() {
        final String encodeId = encodeId(id);
        return new HsaIdEnhet(encodeId);
    }

    public HsaIdEnhet getIdUnencoded() {
        return new HsaIdEnhet(id);
    }

    public String getName() {
        return name;
    }

    public HsaIdVardgivare getVardgivarId() {
        return new HsaIdVardgivare(vardgivarId);
    }

    public String getVardgivarName() {
        return vardgivarName;
    }

    public String getLansId() {
        return lansId;
    }

    public String getLansName() {
        return lansName;
    }

    public String getKommunId() {
        return kommunId;
    }

    public String getKommunName() {
        return kommunName;
    }

    public String getVardenhetId() {
        return vardenhetId;
    }

    public Set<VerksamhetsTyp> getVerksamhetsTyper() {
        return verksamhetsTyper;
    }

    public static String encodeId(String id) {
        return ESCAPER.translate(id).replace('\\', '_');
    }

    public static String decodeId(String encodedId) {
        return new UnicodeUnescaper().translate(encodedId.replace('_', '\\'));
    }

    public boolean isVardenhet() {
        return id.equalsIgnoreCase(vardenhetId);
    }

    public static final class VerksamhetsTyp implements Serializable {

        private final String id;
        private final String name;

        public VerksamhetsTyp(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            VerksamhetsTyp that = (VerksamhetsTyp) o;

            if (!id.equals(that.id)) {
                return false;
            }
            if (!name.equals(that.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            final int number = 31;
            result = number * result + name.hashCode();
            return result;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return id;
        }

    }

}
