/* globals browser */

'use strict';

var pohelper = require('./pohelper.js');

var headerTextElem = element(by.id('reportHeader'));
var chart1Elem = element(by.id('chart1'));
var chart2Elem = element(by.id('chart2'));
var chartlegendsElem = element(by.id('chartlegends'));
var chartlegendLabels = element.all(by.css('#chartlegends #legend-label .legend-name'));

var verifyAt = function() {
    expect(headerTextElem.isDisplayed()).toBeTruthy('Titel-text saknas för rapporten');
};

var getNumberOfCharts = function() {
    var chart1 = pohelper.isElementPresentAndDisplayed(chart1Elem);
    var chart2 = pohelper.isElementPresentAndDisplayed(chart2Elem);
    return Promise.all([chart1, chart2]).then(function(results) {
        return results.reduce(function(result, sum){
            return sum += result ? 1 : 0
        }, 0);
    });
};

var getChartLegendLabels = function() {
    return element.all(by.css('#chartlegends #legend-label .legend-name')).then(function(legends) {
        return legends.map(function(labelElem) {
            return labelElem.text;
        });
    });
};

var getTableRows = async function() {
    var all = await element.all(by.css('#chart-data-table .datatable tr'));
    return all.then(function(tablerows) {
        return tablerows.map(function(tr) {
            return tr.all(by.css('td')).then(function(datafields) {
                return datafields.map(function(td) {
                    return td.text;
                });
            });
        });
    });
};

module.exports = {
    'verifyAt': verifyAt,
    'headerTextElem': headerTextElem,
    'getNumberOfCharts': getNumberOfCharts,
    'getChartLegendLabels': getChartLegendLabels,
    'chart1Elem': chart1Elem,
    'chart2Elem': chart2Elem,
    'getTableRows': getTableRows
};
