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
package se.inera.testsupport.fkrapport;

import static se.inera.testsupport.fkrapport.FkReportCreator.roundToTwoDecimals;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.service.report.model.Kon;
import se.inera.testsupport.socialstyrelsenspecial.MathStatistics;

/**
 * Placeholder for the given combination of diagnos, kon and lan.
 * It will only accept FkFactRow data that fits it's combination.
 * The actual measurements / average, medians etc can be queried at any time, but in reality it's only done when all
 * facts have
 * been distributed.
 */
public class FkReportDataRow {
    private static final String KON_BOTH = "BOTH";
    private String diagnos;
    private String regexpMatcher;
    private Kon kon;
    private String lanId;
    private List<Double> lengths = new ArrayList<>();

    public FkReportDataRow(String diagnos, String regexpMatcher, Kon kon, String lanId) {
        this.diagnos = diagnos;
        this.kon = kon;
        this.lanId = lanId;
        this.regexpMatcher = regexpMatcher;
    }

    public void ingest(FkFactRow factRow) {
        // If this factrow contains data that is relevant to this resultRow - add the length data
        if (matchesDiagnose(factRow.getDiagnos()) && matchesKon(factRow.getKon()) && matchesLan(factRow.getLanId())) {
            lengths.add((double) factRow.getLength());
        }
    }

    private boolean matchesLan(String candidateLanId) {
        return lanId.equals(candidateLanId);
    }

    private boolean matchesKon(Kon candidateKon) {
        return ((kon == null) || kon.equals(candidateKon));
    }

    private boolean matchesDiagnose(String diagnos) {
        return diagnos.matches(regexpMatcher);
    }

    public String getDiagnos() {
        return diagnos;
    }

    public String getKon() {
        return kon == null ? KON_BOTH : kon.name();
    }

    public String getLanId() {
        return lanId;
    }

    public int getAntal() {
        return this.lengths.size();
    }

    public Number getMedel() {
        // If no data - return 0
        if (lengths.size() == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (double a : lengths) {
            sum += a;
        }
        return roundToTwoDecimals(sum / (double) lengths.size());
    }

    public Number getMedian() {
        // If no data - return 0
        if (lengths.size() == 0) {
            return 0.0;
        }
        return new MathStatistics(lengths).median();
    }
}
