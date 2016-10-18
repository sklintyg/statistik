/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.socialstyrelsenspecial;

import se.inera.statistics.service.report.model.Kon;

public class SosRow {

    private String diagnos;
    private Kon kon;
    private String lanId;
    private int length;

    public SosRow(String diagnos, Kon kon, String lanId, int length) {
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
