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
package se.inera.statistics.web.service;

import java.util.List;

public class ReportRequestFilter {
    private List<String> kapitels;
    private List<String> avsnitts;
    private List<String> kategoris;
    private List<String> enhets;
    private List<String> verksamhetstyper;

    public ReportRequestFilter() {
    }

    public ReportRequestFilter(List<String> kapitels, List<String> avsnitts, List<String> kategoris, List<String> enhets, List<String> verksamhetstyper) {
        this.kapitels = kapitels;
        this.avsnitts = avsnitts;
        this.kategoris = kategoris;
        this.enhets = enhets;
        this.verksamhetstyper = verksamhetstyper;
    }

    public List<String> getKategoris() {
        return kategoris;
    }

    public List<String> getKapitels() {
        return kapitels;
    }

    public List<String> getAvsnitts() {
        return avsnitts;
    }

    public List<String> getEnhets() {
        return enhets;
    }

    public List<String> getVerksamhetstyper() {
        return verksamhetstyper;
    }

}
