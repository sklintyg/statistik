/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Aisle implements Iterable<Fact> {
    private final List<Fact> lines = new ArrayList<>();

    public void addLine(Fact line) {
        lines.add(line);
    }

    @Override
    public Iterator<Fact> iterator() {
        return lines.iterator();
    }

    public int getSize() {
        return lines.size();
    }

    public void sort() {
        Collections.sort(lines, Fact.TIME_ORDER);
    }

    List<Fact> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
