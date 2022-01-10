/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import se.inera.statistics.web.model.CasesPerCountyData;
import se.inera.statistics.web.model.DiagnosisSubGroupStatisticsData;
import se.inera.statistics.web.model.DualSexStatisticsData;
import se.inera.statistics.web.model.SimpleDetailsData;
import se.inera.statistics.web.model.TableDataReport;
import se.inera.statistics.web.model.overview.OverviewData;

/**
 * Contains the final reports for national statistics.
 */
public class NationellDataResult implements Serializable {

    private SimpleDetailsData numberOfCasesPerMonth;
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
    private SimpleDetailsData intygPerSjukfall;
    private TableDataReport andelKompletteringar;
    private TableDataReport kompletteringarPerFraga;

    public SimpleDetailsData getNumberOfCasesPerMonth() {
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

    public SimpleDetailsData getSjukfallslangd() {
        return sjukfallslangd;
    }

    void setSjukfallslangd(SimpleDetailsData sjukfallslangd) {
        this.sjukfallslangd = sjukfallslangd;
    }

    public CasesPerCountyData getSjukfallPerLan() {
        return sjukfallPerLan;
    }

    void setSjukfallPerLan(CasesPerCountyData sjukfallPerLan) {
        this.sjukfallPerLan = sjukfallPerLan;
    }

    public SimpleDetailsData getKonsfordelningPerLan() {
        return konsfordelningPerLan;
    }

    void setKonsfordelningPerLan(SimpleDetailsData konsfordelningPerLan) {
        this.konsfordelningPerLan = konsfordelningPerLan;
    }

    void setMeddelandenPerAmne(DualSexStatisticsData meddelandenPerAmne) {
        this.meddelandenPerAmne = meddelandenPerAmne;
    }

    public DualSexStatisticsData getMeddelandenPerAmne() {
        return meddelandenPerAmne;
    }

    public TableDataReport getIntygPerTyp() {
        return intygPerTyp;
    }

    public void setIntygPerTyp(TableDataReport intygPerTyp) {
        this.intygPerTyp = intygPerTyp;
    }

    public SimpleDetailsData getIntygPerSjukfall() {
        return intygPerSjukfall;
    }

    public void setIntygPerSjukfall(SimpleDetailsData intygPerSjukfall) {
        this.intygPerSjukfall = intygPerSjukfall;
    }

    public TableDataReport getAndelKompletteringar() {
        return andelKompletteringar;
    }

    public void setAndelKompletteringar(TableDataReport andelKompletteringar) {
        this.andelKompletteringar = andelKompletteringar;
    }

    public TableDataReport getKompletteringarPerFraga() {
        return kompletteringarPerFraga;
    }

    public void setKompletteringarPerFraga(TableDataReport kompletteringarPerFraga) {
        this.kompletteringarPerFraga = kompletteringarPerFraga;
    }
}
