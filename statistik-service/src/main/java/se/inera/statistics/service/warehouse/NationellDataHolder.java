/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
    private SimpleKonResponse<SimpleKonDataRow> aldersgrupperResult;
    private KonDataResponse sjukskrivningsgradResult;
    private SimpleKonResponse<SimpleKonDataRow> sjukfallslangdResult;
    private ArrayList<SimpleKonDataRow> lanResult = new ArrayList<>();
    private SimpleKonResponse<SimpleKonDataRow> langaSjukfallPreviousResult;
    private SimpleKonResponse<SimpleKonDataRow> langaSjukfallCurrentResult;

    private ArrayList<SimpleKonDataRow> overviewAntalIntygResult = new ArrayList<>();
    private ArrayList<SimpleKonDataRow> overviewForandringResult = new ArrayList<>();
    private DiagnosgruppResponse overviewDiagnosgrupperResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewPreviousAldersgrupperResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewCurrentAldersgrupperResult;
    private KonDataResponse overviewSjukskrivningsgradResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewSjukfallslangdPreviousResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewSjukfallslangdCurrentResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallResult;
    private SimpleKonResponse<SimpleKonDataRow> overviewLangaSjukfallDiffResult;
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

    SimpleKonResponse<SimpleKonDataRow> getAldersgrupperResult() {
        return aldersgrupperResult;
    }

    void setAldersgrupperResult(SimpleKonResponse<SimpleKonDataRow> aldersgrupperResult) {
        this.aldersgrupperResult = aldersgrupperResult;
    }

    KonDataResponse getSjukskrivningsgradResult() {
        return sjukskrivningsgradResult;
    }

    void setSjukskrivningsgradResult(KonDataResponse sjukskrivningsgradResult) {
        this.sjukskrivningsgradResult = sjukskrivningsgradResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getSjukfallslangdResult() {
        return sjukfallslangdResult;
    }

    void setSjukfallslangdResult(SimpleKonResponse<SimpleKonDataRow> sjukfallslangdResult) {
        this.sjukfallslangdResult = sjukfallslangdResult;
    }

    ArrayList<SimpleKonDataRow> getLanResult() {
        return lanResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getLangaSjukfallPreviousResult() {
        return langaSjukfallPreviousResult;
    }

    void setLangaSjukfallPreviousResult(SimpleKonResponse<SimpleKonDataRow> langaSjukfallPreviousResult) {
        this.langaSjukfallPreviousResult = langaSjukfallPreviousResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getLangaSjukfallCurrentResult() {
        return langaSjukfallCurrentResult;
    }

    void setLangaSjukfallCurrentResult(SimpleKonResponse<SimpleKonDataRow> langaSjukfallCurrentResult) {
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

    SimpleKonResponse<SimpleKonDataRow> getOverviewPreviousAldersgrupperResult() {
        return overviewPreviousAldersgrupperResult;
    }

    void setOverviewPreviousAldersgrupperResult(SimpleKonResponse<SimpleKonDataRow> overviewPreviousAldersgrupperResult) {
        this.overviewPreviousAldersgrupperResult = overviewPreviousAldersgrupperResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewCurrentAldersgrupperResult() {
        return overviewCurrentAldersgrupperResult;
    }

    void setOverviewCurrentAldersgrupperResult(SimpleKonResponse<SimpleKonDataRow> overviewCurrentAldersgrupperResult) {
        this.overviewCurrentAldersgrupperResult = overviewCurrentAldersgrupperResult;
    }

    KonDataResponse getOverviewSjukskrivningsgradResult() {
        return overviewSjukskrivningsgradResult;
    }

    void setOverviewSjukskrivningsgradResult(KonDataResponse overviewSjukskrivningsgradResult) {
        this.overviewSjukskrivningsgradResult = overviewSjukskrivningsgradResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewSjukfallslangdPreviousResult() {
        return overviewSjukfallslangdPreviousResult;
    }

    void setOverviewSjukfallslangdPreviousResult(SimpleKonResponse<SimpleKonDataRow> overviewSjukfallslangdPreviousResult) {
        this.overviewSjukfallslangdPreviousResult = overviewSjukfallslangdPreviousResult;
    }

    SimpleKonResponse<SimpleKonDataRow> getOverviewSjukfallslangdCurrentResult() {
        return overviewSjukfallslangdCurrentResult;
    }

    void setOverviewSjukfallslangdCurrentResult(SimpleKonResponse<SimpleKonDataRow> overviewSjukfallslangdCurrentResult) {
        this.overviewSjukfallslangdCurrentResult = overviewSjukfallslangdCurrentResult;
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
