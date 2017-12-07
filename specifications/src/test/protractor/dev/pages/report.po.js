/* globals Promise */

'use strict';

var pohelper = require('./pohelper.js');
var utils = require('../utils/utils.js');
var text = utils.message;

var ReportPage = function() {
    this.headerTextElem = element(by.id('reportHeader'));
    this.chart1Elem = element(by.id('chart1'));
    this.chart2Elem = element(by.id('chart2'));
    this.chartlegendsElem = element(by.id('chartlegends'));
    this.chartlegendLabels = element.all(by.css('#chartlegends #legend-label .legend-name'));

    this.compareDiagnosisContainer = element(by.css('compare-diagnosis-selection'));
    this.compareDiagnosisLevelChapter = this.compareDiagnosisContainer.element(by.id('comparediagnoses-level-chapter'));
    this.compareDiagnosisLevelSection = this.compareDiagnosisContainer.element(by.id('comparediagnoses-level-section'));
    this.compareDiagnosisLevelCategory = this.compareDiagnosisContainer.element(by.id('comparediagnoses-level-category'));
    this.compareDiagnosisLevelCode = this.compareDiagnosisContainer.element(by.id('comparediagnoses-level-code'));
    this.compareDiagnosisBtn = this.compareDiagnosisContainer.element(by.css('button'));
    this.dialog = element(by.css('.tree-multi-selector'));
    this.compareDiagnosisCloseBtn = this.dialog.element(by.id('treeMultiSelectorCloseBtn'));
    this.compareDiagnosisSaveAndCloseBtn = this.dialog.element(by.id('treeMultiSelectorSaveBtn'));


    this.compareDiagnosisDepthList = function(depth) {
        return this.dialog.all(by.css('.depth' + depth));
    };

    this.isAtPage = function(key) {
        expect(this.headerTextElem.isDisplayed()).toBeTruthy('Titel-text saknas för rapporten');

        if (key) {
            expect(this.headerTextElem.getText()).toEqual(text[key]);
        }
    };

    this.getNumberOfCharts = function() {
        var chart1 = pohelper.isElementPresentAndDisplayed(this.chart1Elem);
        var chart2 = pohelper.isElementPresentAndDisplayed(this.chart2Elem);
        return Promise.all([chart1, chart2]).then(function(results) {
            return results.reduce(function(result, sum) {
                return sum + (result ? 1 : 0);
            }, 0);
        });
    };

    this.getChartLegendLabels = function() {

        return element.all(by.css('#chartlegends #legend-label .legend-name'));

        /*
        return .then(function(legends) {
            return legends.map(function(labelElem) {
                return labelElem.text;
            });
        });*/
    };

    this.getTableRows = function() {
        return element.all(by.css('#chart-data-table .scrolling tr'));

        /*return all.then(function(tablerows) {
            return tablerows.map(function(tr) {
                return tr.all(by.css('td')).then(function(datafields) {
                    return datafields.map(function(td) {
                        return td.text;
                    });
                });
            });
        });*/
    };

    this.getTableRowsLabel = function() {
        return element.all(by.css('#chart-data-table tbody .headcol'));
    };
};

module.exports = new ReportPage();
