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

import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.DiagnosisSubGroupStatisticsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.overview.OverviewData;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the final reports for national statistics.
 */
public class NationellDataResult {

    private SimpleDetailsData numberOfCasesPerMonth;
    // private SimpleDetailsData numberOfMeddelandenPerMonth;
    private DualSexStatisticsData diagnosgrupper;
    private Map<String, DiagnosisSubGroupStatisticsData> diagnoskapitel = new HashMap<>();
    private OverviewData overview;
    private SimpleDetailsData aldersgrupper;
    private DualSexStatisticsData sjukskrivningsgrad;
    private SimpleDetailsData sjukfallslangd;
    private CasesPerCountyData sjukfallPerLan;
    private SimpleDetailsData konsfordelningPerLan;
    private DualSexStatisticsData meddelandenPerAmne;
    private TableDataReport intygPerTyp;
    private TableDataReport andelKompletteringar;

    SimpleDetailsData getNumberOfCasesPerMonth() {
        return numberOfCasesPerMonth;
    }

    void setNumberOfCasesPerMonth(SimpleDetailsData numberOfCasesPerMonth) {
        this.numberOfCasesPerMonth = numberOfCasesPerMonth;
    }

    public DualSexStatisticsData getDiagnosgrupper() {
        return diagnosgrupper;
    }

    public void setDiagnosgrupper(DualSexStatisticsData diagnosgrupper) {
        this.diagnosgrupper = diagnosgrupper;
    }

    public Map<String, DiagnosisSubGroupStatisticsData> getDiagnoskapitel() {
        return diagnoskapitel;
    }

    public void setDiagnoskapitel(Map<String, DiagnosisSubGroupStatisticsData> diagnoskapitel) {
        this.diagnoskapitel = diagnoskapitel;
    }

    public OverviewData getOverview() {
        return overview;
    }

    public void setOverview(OverviewData overview) {
        this.overview = overview;
    }

    public SimpleDetailsData getAldersgrupper() {
        return aldersgrupper;
    }

    public void setAldersgrupper(SimpleDetailsData aldersgrupper) {
        this.aldersgrupper = aldersgrupper;
    }

    public DualSexStatisticsData getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public void setSjukskrivningsgrad(DualSexStatisticsData sjukskrivningsgrad) {
        this.sjukskrivningsgrad = sjukskrivningsgrad;
    }

    SimpleDetailsData getSjukfallslangd() {
        return sjukfallslangd;
    }

    void setSjukfallslangd(SimpleDetailsData sjukfallslangd) {
        this.sjukfallslangd = sjukfallslangd;
    }

    CasesPerCountyData getSjukfallPerLan() {
        return sjukfallPerLan;
    }

    void setSjukfallPerLan(CasesPerCountyData sjukfallPerLan) {
        this.sjukfallPerLan = sjukfallPerLan;
    }

    SimpleDetailsData getKonsfordelningPerLan() {
        return konsfordelningPerLan;
    }

    void setKonsfordelningPerLan(SimpleDetailsData konsfordelningPerLan) {
        this.konsfordelningPerLan = konsfordelningPerLan;
    }

    void setMeddelandenPerAmne(DualSexStatisticsData meddelandenPerAmne) {
        this.meddelandenPerAmne = meddelandenPerAmne;
    }

    DualSexStatisticsData getMeddelandenPerAmne() {
        return meddelandenPerAmne;
    }

    public TableDataReport getIntygPerTyp() {
        return intygPerTyp;
    }

    public void setIntygPerTyp(TableDataReport intygPerTyp) {
        this.intygPerTyp = intygPerTyp;
    }

    public TableDataReport getAndelKompletteringar() {
        return andelKompletteringar;
    }

    public void setAndelKompletteringar(TableDataReport andelKompletteringar) {
        this.andelKompletteringar = andelKompletteringar;
    }

}
