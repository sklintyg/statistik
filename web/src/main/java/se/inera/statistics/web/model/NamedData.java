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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class NamedData implements Serializable {

    private final String name;
    private final List<Object> data;
    private final boolean marked;

    public NamedData(String name, List<? extends Object> data, boolean marked) {
        this.name = name;
        this.data = Collections.unmodifiableList(data);
        this.marked = marked;
    }

    public NamedData(String name, List<? extends Object> data) {
        this(name, data, false);
    }

    public String getName() {
        return name;
    }

    public List<Object> getData() {
        return data;
    }

    public boolean isMarked() {
        return marked;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NamedData namedData = (NamedData) o;

        if (marked != namedData.marked) {
            return false;
        }
        if (name != null ? !name.equals(namedData.name) : namedData.name != null) {
            return false;
        }
        return data != null ? data.equals(namedData.data) : namedData.data == null;

    }

    // CHECKSTYLE:OFF MagicNumber
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (marked ? 1 : 0);
        return result;
    }
    // CHECKSTYLE:ON MagicNumber
}
