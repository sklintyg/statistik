/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* globals Highcharts */
angular.module('StatisticsApp').controller('doubleAreaChartsCtrl',
    ['$scope', '$rootScope', '$routeParams', '$window', '$timeout', 'statisticsData', 'config', 'messageService', 'diagnosisTreeFilter',
            '$location', 'chartFactory', '_', 'pdfFactory', 'ControllerCommons',
    function ($scope, $rootScope, $routeParams, $window, $timeout, statisticsData, config, messageService, diagnosisTreeFilter,
            $location ,chartFactory, _, pdfFactory, ControllerCommons) {
        'use strict';

        var that = this;
        var chart1 = {};
        var chart2 = {};
        $scope.status = {
            isTableOpen: true
        };

        var defaultChartType = 'area';
        var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, config, defaultChartType);
        $scope.activeChartType = chartTypeInfo.activeChartType;

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);

        this.paintChart = function (containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries, chartSpacingLeft, doneLoadingCallback) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback, false, chartTypeInfo.usePercentChart, chartTypeInfo.stacked);
            chartOptions.chart.type = chartTypeInfo.activeHighchartType;
            chartOptions.chart.renderTo = containerId;
            chartOptions.plotOptions.area.lineWidth = 1;
            chartOptions.plotOptions.area.lineColor = 'grey';
            chartOptions.legend.enabled = false;
            chartOptions.xAxis.title.text = 'Period';
            chartOptions.tooltip.useHTML = true;
            chartOptions.subtitle.text = (chartTypeInfo.usePercentChart ? 'Andel ' : 'Antal ') + yAxisTitle;
            return new Highcharts.Chart(chartOptions);
        };

        function updateChartsYAxisMaxValue() {
            // Reset values to default
            that.chart1.yAxis[0].setExtremes(0, null);
            that.chart2.yAxis[0].setExtremes(0, null);

            var yMax = Math.max(that.chart1.yAxis[0].getExtremes().max, that.chart2.yAxis[0].getExtremes().max);

            // Set both values to same max.
            that.chart1.yAxis[0].setExtremes(0, yMax);
            that.chart2.yAxis[0].setExtremes(0, yMax);
        }

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            var chartCategories = ajaxResult.femaleChart.categories;

            var chartSeriesFemale = ajaxResult.femaleChart.series;
            chartFactory.addColor(chartSeriesFemale);
            that.chart1 = that.paintChart('chart1', 'sjukfall för kvinnor', 118, chartCategories, chartSeriesFemale, -100, doneLoadingCallback);

            var chartSeriesMale = ajaxResult.maleChart.series;
            chartFactory.addColor(chartSeriesMale);
            that.chart2 = that.paintChart('chart2', 'sjukfall för män', 97, chartCategories, chartSeriesMale, -80, doneLoadingCallback);

            updateChartsYAxisMaxValue();

            $scope.series = chartSeriesMale;
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location, $routeParams);
        };

        $scope.showInLegend = function(index) {
            return chartFactory.showInLegend(that.chart1.series, index) && chartFactory.showInLegend(that.chart2.series, index);
        };

        var populatePageWithDataSuccess = function(result) {
            ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser,
                result.allAvailableDxsSelectedInFilter,
                result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets);
            $scope.enhetsCount = result.filter.enheter ? result.filter.enheter.length : null;
            $scope.resultMessage = ControllerCommons.getResultMessage(result, messageService);
            $scope.subTitle = config.title(result.period, $scope.enhetsCount, $routeParams.kapitelId);
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnittAndKategori(populateDetailsOptions, function() {
                    $window.alert('Kunde inte ladda data');
                });
            }

            $timeout(function() {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result, function() {
                    $scope.doneLoading = true;
                });
                $timeout(function() {
                    $rootScope.$broadcast('pageDataPopulated');
                });
            }, 1);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.checkNationalResult($scope, result, isVerksamhet, false, populatePageWithDataSuccess);
        };

        function populateDetailsOptions(result) {
            var basePath = isVerksamhet ? '#/verksamhet/diagnosavsnitt' : '#/nationell/diagnosavsnitt';
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        }

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, '', null, true);
        });
        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, '', null, true);
        $scope.showDetailOptions3PopoverText = messageService.getProperty(config.pageHelpTextShowDetailOptions, null, '', null, true);

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram för kvinnor'},
            {id: 'chart2', name: 'diagram för män'}
        ];

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(that.chart1.series[index]);
            chartFactory.toggleSeriesVisibility(that.chart2.series[index]);

            updateChartsYAxisMaxValue();
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, ControllerCommons.getExtraPathParam($routeParams));
        }


        var isExistingDiagnosisHashValid = $routeParams.diagnosHash !== '-';
        if (isExistingDiagnosisHashValid) {
            $scope.spinnerText = 'Laddar information...';
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet(ControllerCommons.getExtraPathParam($routeParams));
                refreshVerksamhet();
            } else {
                $scope.exportTableUrl = config.exportTableUrl($routeParams.kapitelId);
                statisticsData[config.dataFetcher](populatePageWithData, function () {
                    $scope.dataLoadingError = true;
                }, ControllerCommons.getMostSpecificGroupId($routeParams));
            }
        } else {
            $scope.doneLoading = true;
        }

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        if ($scope.showDiagnosisSelector) {
            ControllerCommons.setupDiagnosisSelector(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location);
        }

        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;
        $scope.showDetailsOptions3 = config.showDetailsOptions3 && isVerksamhet;

        $scope.useSpecialPrintTable = true;

        $scope.exportChart = function (chartName) {
            chartFactory.exportChart(that[chartName], $scope.pageName, $scope.subTitle);
        };

        $scope.printPdf = function () {
            pdfFactory.print($scope, [that.chart1, that.chart2]);
        };

        $scope.$on('$destroy', function() {
            if(chart1 && typeof chart1.destroy === 'function') {
                chart1.destroy();
            }

            if(chart2 && typeof chart2.destroy === 'function') {
                chart2.destroy();
            }
        });

        return this;

    }
]);

angular.module('StatisticsApp').diagnosisGroupConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';


    var conf = {};
    conf.dataFetcher = 'getDiagnosisGroupData';
    conf.dataFetcherVerksamhet = 'getDiagnosisGroupDataVerksamhet';
    conf.exportTableUrl = function () {
        return 'api/getDiagnoskapitelstatistik/csv';
    };
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDiagnoskapitelstatistik/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per diagnosgrupp' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = 'help.diagnosisgroup';
    conf.chartFootnotes = ['alert.diagnosisgroup.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosgrupp', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosgrupptvarsnitt', active: false}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getSubDiagnosisGroupData';
    conf.dataFetcherVerksamhet = 'getSubDiagnosisGroupDataVerksamhet';
    conf.exportTableUrl = function (subgroupId) {
        return 'api/getDiagnosavsnittstatistik/' + subgroupId + '/csv';
    };
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return 'api/verksamhet/getDiagnosavsnittstatistik/' + subgroupId + '/csv';
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.showDetailsOptions3 = true;
    conf.title = function (period, enhetsCount, name) {
        return 'Antal sjukfall för ' + name + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = 'help.diagnosissubgroup';
    conf.pageHelpTextShowDetailOptions = 'help.diagnosissubgroup.showdetailoptions';
    conf.chartFootnotes = ['alert.diagnosissubgroup.information'];
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosavsnitt', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosavsnitttvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getDegreeOfSickLeave';
    conf.dataFetcherVerksamhet = 'getDegreeOfSickLeaveVerksamhet';
    conf.exportTableUrl = function () {
        return 'api/getDegreeOfSickLeaveStatistics/csv';
    };
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDegreeOfSickLeaveStatistics/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per sjukskrivningsgrad' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = 'help.degreeofsickleave';
    conf.chartFootnotes = ['alert.degreeofsickleave.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningsgrad', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningsgradtvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').differentieratIntygandeConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDifferentieratIntygandeVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDifferentieratIntygandeStatistics/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Andel sjukfall för differentierat intygande' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = 'help.differentieratintygande';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/differentieratintygande', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/differentieratintygandetvarsnitt', active: false}];

    conf.defaultChartType = 'percentarea';

    return conf;
};

angular.module('StatisticsApp').casesPerBusinessTimeSeriesConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerBusinessTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerEnhetTimeSeries/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per vårdenhet' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartFootnotes = ['alert.vardenhet.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperenhettidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperenhet', active: false}];

    return conf;
};

angular.module('StatisticsApp').compareDiagnosisTimeSeriesConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getCompareDiagnosisTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return 'api/verksamhet/getJamforDiagnoserStatistikTidsserie/' + diagnosisHash + '/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Jämförelse av valfria diagnoser' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.showDiagnosisSelector = true;

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/jamforDiagnoserTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/jamforDiagnoser', active: false}];
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupTimeSeriesConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getAgeGroupsTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getAgeGroupsStatisticsAsTimeSeries/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per åldersgrupp' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/aldersgrupperTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/aldersgrupper', active: false}];
    return conf;
};

angular.module('StatisticsApp').sickLeaveLengthTimeSeriesConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSickLeaveLengthTimeSeriesDataVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSickLeaveLengthTimeSeries/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per sjukskrivningslängd' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartFootnotes = ['info.sickleavelength'];
    conf.pageHelpText = 'help.sickleavelength';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningslangdTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningslangd', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningTidsserieConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakarbefattningTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakarbefattningSomTidsserie/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall baserat på läkarbefattning' + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartFootnotes = ['alert.lakare-befattning.information'];
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakarbefattningtidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakarbefattning', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakareTimeSeriesConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakareSomTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSjukfallPerLakareSomTidsserie/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per läkare' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaretidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakare', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonTidsserieConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakaresAlderOchKonTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall baserat på läkares kön och ålder' + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.pageHelpText = 'alert.lakarkon-alder.questionmark';
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaresalderochkontidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakaresalderochkon', active: false}];
    return conf;
};
