/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.integration.hsa.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;
import java.io.IOException;
import java.io.Serializable;

public class HsaIdAny implements Serializable {

    private String id;

    HsaIdAny(String id) {
        this.id = id == null ? "" : toUpperCaseAndRemoveBlanks(id);
    }

    private static String toUpperCaseAndRemoveBlanks(String s) {
        final char[] chars = new char[s.length()];
        int j = 0;
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (!Character.isWhitespace(ch)) {
                chars[j++] = Character.toUpperCase(ch);
            }
        }
        return new String(chars, 0, j);
    }

    /**
     * Instances of this class should only be created in the rare cases when one of the subclasses can not be used.
     */
    public static HsaIdAny createEvenThoughSubclassesArePreferred(String id) {
        return new HsaIdAny(id);
    }

    public static HsaIdAny empty() {
        return new HsaIdAny("");
    }

    @JsonValue
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HsaIdAny hsaId = (HsaIdAny) o;
        return Objects.equal(id, hsaId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return id;
    }

    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        stream.writeObject(id);
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        id = (String) stream.readObject();
    }

    public boolean isEmpty() {
        return id.isEmpty();
    }

}
