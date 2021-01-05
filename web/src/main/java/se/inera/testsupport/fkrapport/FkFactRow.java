/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.fkrapport;

import se.inera.statistics.service.report.model.Kon;

/**
 * Represents a singular fact about a sjukfall for the FK report.
 */
public class FkFactRow {

    private String diagnos;
    private Kon kon;
    private String lanId;
    private int length;

    public FkFactRow(String diagnos, Kon kon, String lanId, int length) {
        this.diagnos = diagnos;
        this.kon = kon;
        this.lanId = lanId;
        this.length = length;
    }

    public String getDiagnos() {
        return diagnos;
    }

    public Kon getKon() {
        return kon;
    }

    public String getLanId() {
        return lanId;
    }

    public int getLength() {
        return length;
    }
}
