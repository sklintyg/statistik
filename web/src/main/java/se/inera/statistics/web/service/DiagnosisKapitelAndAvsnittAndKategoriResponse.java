/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import se.inera.statistics.service.report.model.Icd;

import java.util.List;
import java.util.Map;

class DiagnosisKapitelAndAvsnittAndKategoriResponse {
    private final List<Icd> kapitels;
    private final Map<String, List<Icd>> avsnitts;
    private final Map<String, List<Icd>> kategoris;

    DiagnosisKapitelAndAvsnittAndKategoriResponse(Map<String, List<Icd>> kategoris, Map<String, List<Icd>> avsnitts, List<Icd> kapitels) {
        this.kapitels = kapitels;
        this.avsnitts = avsnitts;
        this.kategoris = kategoris;
    }

    public List<Icd> getKapitels() {
        return kapitels;
    }

    public Map<String, List<Icd>> getAvsnitts() {
        return avsnitts;
    }

    public Map<String, List<Icd>> getKategoris() {
        return kategoris;
    }

}