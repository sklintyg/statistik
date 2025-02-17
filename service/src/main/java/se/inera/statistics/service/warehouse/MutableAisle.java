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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

public class MutableAisle {

    private final List<Fact> lines = new ArrayList<>();
    private final HsaIdVardgivare vardgivareId;

    public MutableAisle(HsaIdVardgivare vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public void addLine(Fact line) {
        lines.add(line);
    }

    public Aisle createAisle() {
        return new Aisle(vardgivareId, lines);
    }

    public int getSize() {
        return lines.size();
    }

    List<Fact> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public HsaIdVardgivare getVardgivareId() {
        return vardgivareId;
    }

}
