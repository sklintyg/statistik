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
package se.inera.statistics.service.warehouse;

import java.io.Serializable;

public final class Diagnos implements Serializable {

    private final int diagnoskapitel;
    private final int diagnosavsnitt;
    private final int diagnoskategori;
    private final int diagnoskod;

    Diagnos(int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int diagnoskod) {
        this.diagnoskapitel = diagnoskapitel;
        this.diagnosavsnitt = diagnosavsnitt;
        this.diagnoskategori = diagnoskategori;
        this.diagnoskod = diagnoskod;
    }

    Diagnos(Fact fact) {
        this(fact.getDiagnoskapitel(), fact.getDiagnosavsnitt(), fact.getDiagnoskategori(), fact.getDiagnoskod());
    }

    public int getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public int getDiagnosavsnitt() {
        return diagnosavsnitt;
    }

    public int getDiagnoskategori() {
        return diagnoskategori;
    }

    public int getDiagnoskod() {
        return diagnoskod;
    }

}
