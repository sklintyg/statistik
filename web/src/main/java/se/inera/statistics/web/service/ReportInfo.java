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
package se.inera.statistics.web.service;

public class ReportInfo {

    private Report report;
    private ReportType reportType;

    public ReportInfo(Report report, ReportType reportType) {
        this.report = report;
        this.reportType = reportType;
    }

    public Report getReport() {
        return report;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public boolean isDualChartReport() {
        if (ReportType.TIDSSERIE.equals(reportType)) {
            switch (report) {
                case V_SJUKFALLTOTALT: //Fall through
                case L_SJUKFALLTOTALT: //Fall through
                case N_SJUKFALLTOTALT: //Fall through
                case V_SJUKSKRIVNINGSLANGDMERAN90DAGAR: //Fall through
                case V_VARDENHET: //Fall through
                case L_VARDENHET: //Fall through
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

}
