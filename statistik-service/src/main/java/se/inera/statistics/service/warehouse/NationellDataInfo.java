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
package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;

import java.util.Map;

/**
 * Contains the final results for calculated national statistics.
 */
public class NationellDataInfo {

    private Range antalIntygRange;
    private Range diagnosgrupperRange;
    private Range diagnosavsnittRange;
    private Range aldersgrupperRange;
    private Range sjukskrivningsgradRange;
    private Range sjukfallslangdRange;
    private Range lanRange;
    private Range langaSjukfallRange;
    private Range overviewRange;

    private SimpleKonResponse<SimpleKonDataRow> antalIntygResult;
    private DiagnosgruppResponse diagnosgrupperResult;
    private Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnittResult;
    private SimpleKonResponse<SimpleKonDataRow> aldersgrupperResult;
    private KonDataResponse sjukskrivningsgradResult;
    private SimpleKonResponse<SimpleKonDataRow> sjukfallslangdResult;
    private SimpleKonResponse<SimpleKonDataRow> lanResult;
    private SimpleKonResponse<SimpleKonDataRow> langaSjukfallPreviousResult;
    private SimpleKonResponse<SimpleKonDataRow> langaSjukfallCurrentResult;

    private SimpleKonResponse<SimpleKonDataRow> overviewGenderResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewForandringResult;
    private DiagnosgruppResponse overviewDiagnosgrupperResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewPreviousAldersgruppResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewCurrentAldersgruppResult;
    private KonDataResponse overviewSjukskrivningsgrader;
    private SimpleKonResponse<SimpleKonDataRow> overviewSjukskrivningslangdPreviousResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewSjukskrivningslangdCurrentResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallDiffResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLanPreviousResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLanCurrentResult;

    public SimpleKonResponse<SimpleKonDataRow> getAntalIntygResult() {
        return antalIntygResult;
    }

    void setAntalIntygResult(SimpleKonResponse<SimpleKonDataRow> antalIntygResult) {
        this.antalIntygResult = antalIntygResult;
    }

    public DiagnosgruppResponse getDiagnosgrupperResult() {
        return diagnosgrupperResult;
    }

    void setDiagnosgrupperResult(DiagnosgruppResponse diagnosgrupperResult) {
        this.diagnosgrupperResult = diagnosgrupperResult;
    }

    public Map<Icd10.Kapitel, DiagnosgruppResponse> getDiagnosavsnittResult() {
        return diagnosavsnittResult;
    }

    void setDiagnosavsnittResult(Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnittResult) {
        this.diagnosavsnittResult = diagnosavsnittResult;
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupperResult() {
        return aldersgrupperResult;
    }

    void setAldersgrupperResult(SimpleKonResponse<SimpleKonDataRow> aldersgrupperResult) {
        this.aldersgrupperResult = aldersgrupperResult;
    }

    public KonDataResponse getSjukskrivningsgradResult() {
        return sjukskrivningsgradResult;
    }

    void setSjukskrivningsgradResult(KonDataResponse sjukskrivningsgradResult) {
        this.sjukskrivningsgradResult = sjukskrivningsgradResult;
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallslangdResult() {
        return sjukfallslangdResult;
    }

    void setSjukfallslangdResult(SimpleKonResponse<SimpleKonDataRow> sjukfallslangdResult) {
        this.sjukfallslangdResult = sjukfallslangdResult;
    }

    public SimpleKonResponse<SimpleKonDataRow> getLanResult() {
        return lanResult;
    }

    void setLanResult(SimpleKonResponse<SimpleKonDataRow> lanResult) {
        this.lanResult = lanResult;
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukfallPreviousResult() {
        return langaSjukfallPreviousResult;
    }

    void setLangaSjukfallPreviousResult(SimpleKonResponse<SimpleKonDataRow> langaSjukfallPreviousResult) {
        this.langaSjukfallPreviousResult = langaSjukfallPreviousResult;
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukfallCurrentResult() {
        return langaSjukfallCurrentResult;
    }

    void setLangaSjukfallCurrentResult(SimpleKonResponse<SimpleKonDataRow> langaSjukfallCurrentResult) {
        this.langaSjukfallCurrentResult = langaSjukfallCurrentResult;
    }

    public Range getAntalIntygRange() {
        return antalIntygRange;
    }

    void setAntalIntygRange(Range antalIntygRange) {
        this.antalIntygRange = antalIntygRange;
    }

    public Range getDiagnosgrupperRange() {
        return diagnosgrupperRange;
    }

    void setDiagnosgrupperRange(Range diagnosgrupperRange) {
        this.diagnosgrupperRange = diagnosgrupperRange;
    }

    public Range getDiagnosavsnittRange() {
        return diagnosavsnittRange;
    }

    void setDiagnosavsnittRange(Range diagnosavsnittRange) {
        this.diagnosavsnittRange = diagnosavsnittRange;
    }

    public Range getAldersgrupperRange() {
        return aldersgrupperRange;
    }

    void setAldersgrupperRange(Range aldersgrupperRange) {
        this.aldersgrupperRange = aldersgrupperRange;
    }

    public Range getSjukskrivningsgradRange() {
        return sjukskrivningsgradRange;
    }

    void setSjukskrivningsgradRange(Range sjukskrivningsgradRange) {
        this.sjukskrivningsgradRange = sjukskrivningsgradRange;
    }

    public Range getSjukfallslangdRange() {
        return sjukfallslangdRange;
    }

    void setSjukfallslangdRange(Range sjukfallslangdRange) {
        this.sjukfallslangdRange = sjukfallslangdRange;
    }

    public Range getLanRange() {
        return lanRange;
    }

    void setLanRange(Range lanRange) {
        this.lanRange = lanRange;
    }

    Range getLangaSjukfallRange() {
        return langaSjukfallRange;
    }

    void setLangaSjukfallRange(Range langaSjukfallRange) {
        this.langaSjukfallRange = langaSjukfallRange;
    }

    void setOverviewRange(Range overviewRange) {
        this.overviewRange = overviewRange;
    }

    Range getOverviewRange() {
        return overviewRange;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewGenderResult() {
        return overviewGenderResult;
    }

    void setOverviewGenderResult(SimpleKonResponse<SimpleKonDataRow> overviewGenderResult) {
        this.overviewGenderResult = overviewGenderResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewForandringResult() {
        return overviewForandringResult;
    }

    void setOverviewForandringResult(SimpleKonResponse<SimpleKonDataRow> overviewForandringResult) {
        this.overviewForandringResult = overviewForandringResult;
    }

    DiagnosgruppResponse getOverviewDiagnosgrupperResult() {
        return overviewDiagnosgrupperResult;
    }

    void setOverviewDiagnosgrupperResult(DiagnosgruppResponse overviewDiagnosgrupperResult) {
        this.overviewDiagnosgrupperResult = overviewDiagnosgrupperResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewPreviousAldersgruppResult() {
        return overviewPreviousAldersgruppResult;
    }

    void setOverviewPreviousAldersgruppResult(SimpleKonResponse<SimpleKonDataRow> overviewPreviousAldersgruppResult) {
        this.overviewPreviousAldersgruppResult = overviewPreviousAldersgruppResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewCurrentAldersgruppResult() {
        return overviewCurrentAldersgruppResult;
    }

    void setOverviewCurrentAldersgruppResult(SimpleKonResponse<SimpleKonDataRow> overviewCurrentAldersgruppResult) {
        this.overviewCurrentAldersgruppResult = overviewCurrentAldersgruppResult;
    }

    KonDataResponse getOverviewSjukskrivningsgrader() {
        return overviewSjukskrivningsgrader;
    }

    void setOverviewSjukskrivningsgrader(KonDataResponse overviewSjukskrivningsgrader) {
        this.overviewSjukskrivningsgrader = overviewSjukskrivningsgrader;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewSjukskrivningslangdPreviousResult() {
        return overviewSjukskrivningslangdPreviousResult;
    }

    void setOverviewSjukskrivningslangdPreviousResult(SimpleKonResponse<SimpleKonDataRow> overviewSjukskrivningslangdPreviousResult) {
        this.overviewSjukskrivningslangdPreviousResult = overviewSjukskrivningslangdPreviousResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewSjukskrivningslangdCurrentResult() {
        return overviewSjukskrivningslangdCurrentResult;
    }

    void setOverviewSjukskrivningslangdCurrentResult(SimpleKonResponse<SimpleKonDataRow> overviewSjukskrivningslangdCurrentResult) {
        this.overviewSjukskrivningslangdCurrentResult = overviewSjukskrivningslangdCurrentResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewLangaSjukfallResult() {
        return overviewLangaSjukfallResult;
    }

    void setOverviewLangaSjukfallResult(SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallResult) {
        this.overviewLangaSjukfallResult = overviewLangaSjukfallResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewLangaSjukfallDiffResult() {
        return overviewLangaSjukfallDiffResult;
    }

    void setOverviewLangaSjukfallDiffResult(SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallDiffResult) {
        this.overviewLangaSjukfallDiffResult = overviewLangaSjukfallDiffResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewLanPreviousResult() {
        return overviewLanPreviousResult;
    }

    void setOverviewLanPreviousResult(SimpleKonResponse<SimpleKonDataRow> overviewLanPreviousResult) {
        this.overviewLanPreviousResult = overviewLanPreviousResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewLanCurrentResult() {
        return overviewLanCurrentResult;
    }

    void setOverviewLanCurrentResult(SimpleKonResponse<SimpleKonDataRow> overviewLanCurrentResult) {
        this.overviewLanCurrentResult = overviewLanCurrentResult;
    }

}
