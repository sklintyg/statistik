/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import java.util.Map;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;

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
    private Range overviewRange;
    private Range meddelandenPerAmneRange;

    private SimpleKonResponse antalIntygResult;
    private DiagnosgruppResponse diagnosgrupperResult;
    private Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnittResult;
    private SimpleKonResponse aldersgrupperResult;
    private KonDataResponse sjukskrivningsgradResult;
    private SimpleKonResponse sjukfallslangdResult;
    private SimpleKonResponse lanResult;

    private SimpleKonResponse overviewGenderResult;
    private SimpleKonResponse overviewForandringResult;
    private DiagnosgruppResponse overviewDiagnosgrupperResult;
    private SimpleKonResponse overviewPreviousAldersgruppResult;
    private SimpleKonResponse overviewCurrentAldersgruppResult;
    private KonDataResponse overviewSjukskrivningsgraderPrevious;
    private KonDataResponse overviewSjukskrivningsgraderCurrent;
    private SimpleKonResponse overviewSjukskrivningslangdPreviousResult;
    private SimpleKonResponse overviewSjukskrivningslangdCurrentResult;
    private Integer overviewLangaSjukfallResult;
    private SimpleKonResponse overviewLangaSjukfallDiffPreviousResult;
    private SimpleKonResponse overviewLangaSjukfallDiffCurrentResult;
    private SimpleKonResponse overviewLanPreviousResult;
    private SimpleKonResponse overviewLanCurrentResult;

    private KonDataResponse meddelandenPerAmneResult;
    private KonDataResponse intygPerTypResult;
    private Range intygPerTypeRange;
    private SimpleKonResponse intygPerSjukfallResult;
    private Range intygPerSjukfallRange;
    private KonDataResponse andelKompletteringarResult;
    private Range andelKompletteringarRange;
    private SimpleKonResponse kompletteringarPerFragaResult;
    private Range kompletteringarPerFragaRange;

    public SimpleKonResponse getAntalIntygResult() {
        return antalIntygResult;
    }

    void setAntalIntygResult(SimpleKonResponse antalIntygResult) {
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

    public SimpleKonResponse getAldersgrupperResult() {
        return aldersgrupperResult;
    }

    void setAldersgrupperResult(SimpleKonResponse aldersgrupperResult) {
        this.aldersgrupperResult = aldersgrupperResult;
    }

    public KonDataResponse getSjukskrivningsgradResult() {
        return sjukskrivningsgradResult;
    }

    void setSjukskrivningsgradResult(KonDataResponse sjukskrivningsgradResult) {
        this.sjukskrivningsgradResult = sjukskrivningsgradResult;
    }

    public SimpleKonResponse getSjukfallslangdResult() {
        return sjukfallslangdResult;
    }

    void setSjukfallslangdResult(SimpleKonResponse sjukfallslangdResult) {
        this.sjukfallslangdResult = sjukfallslangdResult;
    }

    public SimpleKonResponse getLanResult() {
        return lanResult;
    }

    void setLanResult(SimpleKonResponse lanResult) {
        this.lanResult = lanResult;
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

    void setOverviewRange(Range overviewRange) {
        this.overviewRange = overviewRange;
    }

    Range getOverviewRange() {
        return overviewRange;
    }

    SimpleKonResponse getOverviewGenderResult() {
        return overviewGenderResult;
    }

    void setOverviewGenderResult(SimpleKonResponse overviewGenderResult) {
        this.overviewGenderResult = overviewGenderResult;
    }

    SimpleKonResponse getOverviewForandringResult() {
        return overviewForandringResult;
    }

    void setOverviewForandringResult(SimpleKonResponse overviewForandringResult) {
        this.overviewForandringResult = overviewForandringResult;
    }

    DiagnosgruppResponse getOverviewDiagnosgrupperResult() {
        return overviewDiagnosgrupperResult;
    }

    void setOverviewDiagnosgrupperResult(DiagnosgruppResponse overviewDiagnosgrupperResult) {
        this.overviewDiagnosgrupperResult = overviewDiagnosgrupperResult;
    }

    SimpleKonResponse getOverviewPreviousAldersgruppResult() {
        return overviewPreviousAldersgruppResult;
    }

    void setOverviewPreviousAldersgruppResult(SimpleKonResponse overviewPreviousAldersgruppResult) {
        this.overviewPreviousAldersgruppResult = overviewPreviousAldersgruppResult;
    }

    SimpleKonResponse getOverviewCurrentAldersgruppResult() {
        return overviewCurrentAldersgruppResult;
    }

    void setOverviewCurrentAldersgruppResult(SimpleKonResponse overviewCurrentAldersgruppResult) {
        this.overviewCurrentAldersgruppResult = overviewCurrentAldersgruppResult;
    }

    KonDataResponse getOverviewSjukskrivningsgraderPrevious() {
        return overviewSjukskrivningsgraderPrevious;
    }

    void setOverviewSjukskrivningsgraderPrevious(KonDataResponse overviewSjukskrivningsgrader) {
        this.overviewSjukskrivningsgraderPrevious = overviewSjukskrivningsgrader;
    }

    KonDataResponse getOverviewSjukskrivningsgraderCurrent() {
        return overviewSjukskrivningsgraderCurrent;
    }

    void setOverviewSjukskrivningsgraderCurrent(KonDataResponse overviewSjukskrivningsgrader) {
        this.overviewSjukskrivningsgraderCurrent = overviewSjukskrivningsgrader;
    }

    SimpleKonResponse getOverviewSjukskrivningslangdPreviousResult() {
        return overviewSjukskrivningslangdPreviousResult;
    }

    void setOverviewSjukskrivningslangdPreviousResult(SimpleKonResponse overviewSjukskrivningslangdPreviousResult) {
        this.overviewSjukskrivningslangdPreviousResult = overviewSjukskrivningslangdPreviousResult;
    }

    SimpleKonResponse getOverviewSjukskrivningslangdCurrentResult() {
        return overviewSjukskrivningslangdCurrentResult;
    }

    void setOverviewSjukskrivningslangdCurrentResult(SimpleKonResponse overviewSjukskrivningslangdCurrentResult) {
        this.overviewSjukskrivningslangdCurrentResult = overviewSjukskrivningslangdCurrentResult;
    }

    Integer getOverviewLangaSjukfallResult() {
        return overviewLangaSjukfallResult;
    }

    void setOverviewLangaSjukfallResult(Integer overviewLangaSjukfallResult) {
        this.overviewLangaSjukfallResult = overviewLangaSjukfallResult;
    }

    SimpleKonResponse getOverviewLangaSjukfallDiffPreviousResult() {
        return overviewLangaSjukfallDiffPreviousResult;
    }

    void setOverviewLangaSjukfallDiffPreviousResult(SimpleKonResponse overviewLangaSjukfallDiffResult) {
        this.overviewLangaSjukfallDiffPreviousResult = overviewLangaSjukfallDiffResult;
    }

    SimpleKonResponse getOverviewLangaSjukfallDiffCurrentResult() {
        return overviewLangaSjukfallDiffCurrentResult;
    }

    void setOverviewLangaSjukfallDiffCurrentResult(SimpleKonResponse overviewLangaSjukfallDiffResult) {
        this.overviewLangaSjukfallDiffCurrentResult = overviewLangaSjukfallDiffResult;
    }

    SimpleKonResponse getOverviewLanPreviousResult() {
        return overviewLanPreviousResult;
    }

    void setOverviewLanPreviousResult(SimpleKonResponse overviewLanPreviousResult) {
        this.overviewLanPreviousResult = overviewLanPreviousResult;
    }

    SimpleKonResponse getOverviewLanCurrentResult() {
        return overviewLanCurrentResult;
    }

    void setOverviewLanCurrentResult(SimpleKonResponse overviewLanCurrentResult) {
        this.overviewLanCurrentResult = overviewLanCurrentResult;
    }

    public void setMeddelandenPerAmneRange(Range meddelandenPerAmneRange) {
        this.meddelandenPerAmneRange = meddelandenPerAmneRange;
    }

    public Range getMeddelandenPerAmneRange() {
        return meddelandenPerAmneRange;
    }

    public KonDataResponse getMeddelandenPerAmneResult() {
        return meddelandenPerAmneResult;
    }

    public void setMeddelandenPerAmneResult(KonDataResponse meddelandenPerAmneResult) {
        this.meddelandenPerAmneResult = meddelandenPerAmneResult;
    }

    public KonDataResponse getIntygPerTypResult() {
        return intygPerTypResult;
    }

    public void setIntygPerTypResult(KonDataResponse intygPerTypResult) {
        this.intygPerTypResult = intygPerTypResult;
    }

    public Range getIntygPerTypeRange() {
        return intygPerTypeRange;
    }

    public void setIntygPerTypeRange(Range intygPerTypeRange) {
        this.intygPerTypeRange = intygPerTypeRange;
    }

    public SimpleKonResponse getIntygPerSjukfallResult() {
        return intygPerSjukfallResult;
    }

    public void setIntygPerSjukfallResult(SimpleKonResponse intygPerSjukfallResult) {
        this.intygPerSjukfallResult = intygPerSjukfallResult;
    }

    public Range getIntygPerSjukfallRange() {
        return intygPerSjukfallRange;
    }

    public void setIntygPerSjukfallRange(Range intygPerSjukfallRange) {
        this.intygPerSjukfallRange = intygPerSjukfallRange;
    }

    public KonDataResponse getAndelKompletteringarResult() {
        return andelKompletteringarResult;
    }

    public void setAndelKompletteringarResult(KonDataResponse andelKompletteringarResult) {
        this.andelKompletteringarResult = andelKompletteringarResult;
    }

    public Range getAndelKompletteringarRange() {
        return andelKompletteringarRange;
    }

    public void setAndelKompletteringarRange(Range andelKompletteringarRange) {
        this.andelKompletteringarRange = andelKompletteringarRange;
    }

    public SimpleKonResponse getKompletteringarPerFragaResult() {
        return kompletteringarPerFragaResult;
    }

    public void setKompletteringarPerFragaResult(SimpleKonResponse kompletteringarPerFragaResult) {
        this.kompletteringarPerFragaResult = kompletteringarPerFragaResult;
    }

    public Range getKompletteringarPerFragaRange() {
        return kompletteringarPerFragaRange;
    }

    public void setKompletteringarPerFragaRange(Range kompletteringarPerFragaRange) {
        this.kompletteringarPerFragaRange = kompletteringarPerFragaRange;
    }
}
