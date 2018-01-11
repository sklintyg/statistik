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
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Hold data between calculations per vg.
 */
class NationellDataHolder {

    private ArrayList<SimpleKonDataRow> antalIntygResult = new ArrayList<>();
    private DiagnosgruppResponse diagnosgrupperResult;
    private Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnittResult = new HashMap<>();
    private SimpleKonResponse aldersgrupperResult;
    private KonDataResponse sjukskrivningsgradResult;
    private SimpleKonResponse sjukfallslangdResult;
    private ArrayList<SimpleKonDataRow> lanResult = new ArrayList<>();
    private SimpleKonResponse langaSjukfallPreviousResult;
    private SimpleKonResponse langaSjukfallCurrentResult;

    private ArrayList<SimpleKonDataRow> overviewAntalIntygResult = new ArrayList<>();
    private ArrayList<SimpleKonDataRow> overviewForandringResult = new ArrayList<>();
    private DiagnosgruppResponse overviewDiagnosgrupperResult;
    private SimpleKonResponse overviewPreviousAldersgrupperResult;
    private SimpleKonResponse overviewCurrentAldersgrupperResult;
    private KonDataResponse overviewSjukskrivningsgradResult;
    private SimpleKonResponse overviewSjukfallslangdPreviousResult;
    private SimpleKonResponse overviewSjukfallslangdCurrentResult;
    private SimpleKonResponse overviewLangaSjukfallResult;
    private SimpleKonResponse overviewLangaSjukfallDiffResult;
    private ArrayList<SimpleKonDataRow> overviewLanPreviousResult = new ArrayList<>();
    private ArrayList<SimpleKonDataRow> overviewLanCurrentResult = new ArrayList<>();

    ArrayList<SimpleKonDataRow> getAntalIntygResult() {
        return antalIntygResult;
    }

    DiagnosgruppResponse getDiagnosgrupperResult() {
        return diagnosgrupperResult;
    }

    void setDiagnosgrupperResult(DiagnosgruppResponse diagnosgrupperResult) {
        this.diagnosgrupperResult = diagnosgrupperResult;
    }

    Map<Icd10.Kapitel, DiagnosgruppResponse> getDiagnosavsnittResult() {
        return diagnosavsnittResult;
    }

    SimpleKonResponse getAldersgrupperResult() {
        return aldersgrupperResult;
    }

    void setAldersgrupperResult(SimpleKonResponse aldersgrupperResult) {
        this.aldersgrupperResult = aldersgrupperResult;
    }

    KonDataResponse getSjukskrivningsgradResult() {
        return sjukskrivningsgradResult;
    }

    void setSjukskrivningsgradResult(KonDataResponse sjukskrivningsgradResult) {
        this.sjukskrivningsgradResult = sjukskrivningsgradResult;
    }

    SimpleKonResponse getSjukfallslangdResult() {
        return sjukfallslangdResult;
    }

    void setSjukfallslangdResult(SimpleKonResponse sjukfallslangdResult) {
        this.sjukfallslangdResult = sjukfallslangdResult;
    }

    ArrayList<SimpleKonDataRow> getLanResult() {
        return lanResult;
    }

    SimpleKonResponse getLangaSjukfallPreviousResult() {
        return langaSjukfallPreviousResult;
    }

    void setLangaSjukfallPreviousResult(SimpleKonResponse langaSjukfallPreviousResult) {
        this.langaSjukfallPreviousResult = langaSjukfallPreviousResult;
    }

    SimpleKonResponse getLangaSjukfallCurrentResult() {
        return langaSjukfallCurrentResult;
    }

    void setLangaSjukfallCurrentResult(SimpleKonResponse langaSjukfallCurrentResult) {
        this.langaSjukfallCurrentResult = langaSjukfallCurrentResult;
    }

    ArrayList<SimpleKonDataRow> getOverviewAntalIntygResult() {
        return overviewAntalIntygResult;
    }

    ArrayList<SimpleKonDataRow> getOverviewForandringResult() {
        return overviewForandringResult;
    }

    DiagnosgruppResponse getOverviewDiagnosgrupperResult() {
        return overviewDiagnosgrupperResult;
    }

    void setOverviewDiagnosgrupperResult(DiagnosgruppResponse overviewDiagnosgrupperResult) {
        this.overviewDiagnosgrupperResult = overviewDiagnosgrupperResult;
    }

    SimpleKonResponse getOverviewPreviousAldersgrupperResult() {
        return overviewPreviousAldersgrupperResult;
    }

    void setOverviewPreviousAldersgrupperResult(SimpleKonResponse overviewPreviousAldersgrupperResult) {
        this.overviewPreviousAldersgrupperResult = overviewPreviousAldersgrupperResult;
    }

    SimpleKonResponse getOverviewCurrentAldersgrupperResult() {
        return overviewCurrentAldersgrupperResult;
    }

    void setOverviewCurrentAldersgrupperResult(SimpleKonResponse overviewCurrentAldersgrupperResult) {
        this.overviewCurrentAldersgrupperResult = overviewCurrentAldersgrupperResult;
    }

    KonDataResponse getOverviewSjukskrivningsgradResult() {
        return overviewSjukskrivningsgradResult;
    }

    void setOverviewSjukskrivningsgradResult(KonDataResponse overviewSjukskrivningsgradResult) {
        this.overviewSjukskrivningsgradResult = overviewSjukskrivningsgradResult;
    }

    SimpleKonResponse getOverviewSjukfallslangdPreviousResult() {
        return overviewSjukfallslangdPreviousResult;
    }

    void setOverviewSjukfallslangdPreviousResult(SimpleKonResponse overviewSjukfallslangdPreviousResult) {
        this.overviewSjukfallslangdPreviousResult = overviewSjukfallslangdPreviousResult;
    }

    SimpleKonResponse getOverviewSjukfallslangdCurrentResult() {
        return overviewSjukfallslangdCurrentResult;
    }

    void setOverviewSjukfallslangdCurrentResult(SimpleKonResponse overviewSjukfallslangdCurrentResult) {
        this.overviewSjukfallslangdCurrentResult = overviewSjukfallslangdCurrentResult;
    }

    SimpleKonResponse getOverviewLangaSjukfallResult() {
        return overviewLangaSjukfallResult;
    }

    void setOverviewLangaSjukfallResult(SimpleKonResponse overviewLangaSjukfallResult) {
        this.overviewLangaSjukfallResult = overviewLangaSjukfallResult;
    }

    SimpleKonResponse getOverviewLangaSjukfallDiffResult() {
        return overviewLangaSjukfallDiffResult;
    }

    void setOverviewLangaSjukfallDiffResult(SimpleKonResponse overviewLangaSjukfallDiffResult) {
        this.overviewLangaSjukfallDiffResult = overviewLangaSjukfallDiffResult;
    }

    ArrayList<SimpleKonDataRow> getOverviewLanPreviousResult() {
        return overviewLanPreviousResult;
    }

    void setOverviewLanPreviousResult(ArrayList<SimpleKonDataRow> overviewLanPreviousResult) {
        this.overviewLanPreviousResult = overviewLanPreviousResult;
    }

    ArrayList<SimpleKonDataRow> getOverviewLanCurrentResult() {
        return overviewLanCurrentResult;
    }

    void setOverviewLanCurrentResult(ArrayList<SimpleKonDataRow> overviewLanCurrentResult) {
        this.overviewLanCurrentResult = overviewLanCurrentResult;
    }

}
