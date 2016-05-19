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
angular.module('StatisticsApp').controller('columnChartDetailsViewCtrl',
    [ '$scope', '$rootScope', '$routeParams', '$window', '$location', '$timeout', 'statisticsData', 'diagnosisTreeFilter',
        'config', 'messageService', 'chartFactory', 'pdfFactory', '_', 'ControllerCommons',
    function ($scope, $rootScope, $routeParams, $window, $location, $timeout, statisticsData, diagnosisTreeFilter,
        config, messageService, chartFactory, pdfFactory, _, ControllerCommons) {
        'use strict';

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
        var isLandsting = ControllerCommons.isShowingLandsting($location);
        var chart = {};
        var defaultChartType = 'column';
        $scope.activeChartType = defaultChartType;

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram'}
        ];

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback);
            chartOptions.chart.type = defaultChartType;
            chartOptions.legend.enabled = false;
            chartOptions.xAxis.title.text = config.chartXAxisTitle;

            var yAxisTitle;
            if (config.chartYAxisTitle) {
                yAxisTitle = config.chartYAxisTitle;
            }
            else if (config.percentChart) {
                yAxisTitle = 'Andel sjukfall i %';
            }
            else {
                yAxisTitle = 'Antal sjukfall';
            }

            chartOptions.subtitle.text = yAxisTitle;
            chartOptions.yAxis.allowDecimals = !!config.allowDecimalsYAxis;
            chartOptions.yAxis.labels.formatter = function () {
                return ControllerCommons.makeThousandSeparated(this.value) + (config.percentChart ? ' %' : '');
            };

            chartOptions.plotOptions.series.stacking = config.percentChart ? 'percent' : 'normal';

            if (config.percentChart) {
                chartOptions.tooltip.pointFormat = '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.0f} %</b><br/>';
            }

            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            $scope.series = chartFactory.addColor(ajaxResult.series);
            chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);
        };

        $scope.switchChartType = function (chartType) {
            chartFactory.switchChartType(chart, chartType);
            $scope.activeChartType = chartType;

            chart.redraw();
        };

        $scope.showInLegend = function(index) {
            return chartFactory.showInLegend(chart.series, index);
        };

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(chart.series[index]);
        };

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? '#/verksamhet/diagnosavsnitttvarsnitt' : '#/nationell/diagnosavsnitttvarsnitt';
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        };

        var populatePageWithData = function (result) {
            $scope.errorPageUrl = null;
            if (result === '' && !isVerksamhet && !isLandsting) {
                $scope.dataLoadingError = true;
                $scope.errorPageUrl = 'app/views/error/statisticNotDone.html';
            } else {
                var enhetsCount = (result.filter && result.filter.enheter) ? result.filter.enheter.length : null;
                $scope.subTitle = config.title(result.period, enhetsCount);
                ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser,
                    result.allAvailableDxsSelectedInFilter,
                    result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets);
                $scope.resultMessage = ControllerCommons.getResultMessage(result, messageService);
                if (config.showDetailsOptions) {
                    $scope.currentPeriod = result.period;
                    statisticsData.getDiagnosisKapitelAndAvsnittAndKategori(populateDetailsOptions, function() {
                        $window.alert('Kunde inte ladda data');
                    });
                }
                $timeout(function() {
                    ControllerCommons.updateDataTable($scope, result.tableData);
                    updateChart(result.chartData, function() {
                        $scope.doneLoading = true;
                    });
                    $timeout(function() {
                        $rootScope.$broadcast('pageDataPopulated');
                    });
                }, 1);
            }
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, ControllerCommons.getExtraPathParam($routeParams));
        }

        function refreshLandsting() {
            statisticsData[config.dataFetcherLandsting](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, ControllerCommons.getExtraPathParam($routeParams));
        }

        var diagnosHashExists = function diagnosHashExists() {
            return $routeParams.diagnosHash !== '-';
        };

        if (diagnosHashExists()) {
            $scope.spinnerText = 'Laddar information...';
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet(ControllerCommons.getExtraPathParam($routeParams));
                refreshVerksamhet();
            } else if (isLandsting) {
                $scope.exportTableUrl = config.exportTableUrlLandsting(ControllerCommons.getExtraPathParam($routeParams));
                refreshLandsting();
            } else {
                $scope.exportTableUrl = config.exportTableUrl;
                statisticsData[config.dataFetcher](populatePageWithData, function () {
                    $scope.dataLoadingError = true;
                });
            }
        } else {
            $scope.doneLoading = true;
        }

        ControllerCommons.updateExchangeableViewsUrl(isVerksamhet, config, $location, $scope, $routeParams);

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;

        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, '', null, true);
        $scope.showDetailOptions3PopoverText = messageService.getProperty(config.pageHelpTextShowDetailOptions, null, '', null, true);

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, '', null, true);
        });

        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;
        $scope.showDetailsOptions3 = config.showDetailsOptions3 && isVerksamhet;

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        if ($scope.showDiagnosisSelector) {
            ControllerCommons.setupDiagnosisSelector(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location);
        }

        $scope.exportChart = function () {
            chartFactory.exportChart(chart, $scope.pageName, $scope.subTitle);
        };

        $scope.printPdf = function () {
            pdfFactory.print($scope, chart);
        };

        $scope.$on('$destroy', function() {
            if(chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });
    }
]);

angular.module('StatisticsApp').nationalSickLeaveLengthConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalSickLeaveLengthData';
    conf.dataFetcherVerksamhet = 'getSickLeaveLengthDataVerksamhet';
    conf.exportTableUrl = 'api/getSickLeaveLengthData/csv';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSickLeaveLengthData/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per sjukskrivningslängd' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Sjukskrivningslängd';
    conf.chartFootnotes = ['info.sickleavelength'];
    conf.pageHelpText = 'help.sickleavelength';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningslangdTidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningslangd', active: true}];
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getAgeGroups';
    conf.dataFetcherVerksamhet = 'getAgeGroupsVerksamhet';
    conf.exportTableUrl = 'api/getAgeGroupsStatistics/csv';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getAgeGroupsStatistics/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per åldersgrupp' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Åldersgrupp';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/aldersgrupperTidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/aldersgrupper', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerSexConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalSjukfallPerSexData';
    conf.exportTableUrl = 'api/getSjukfallPerSexStatistics/csv';
    conf.title = function (period, enhetsCount) {
        return 'Andel sjukfall per kön per län' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.percentChart = true;
    conf.chartXAxisTitle = 'Län';
    conf.chartFootnotes = ['info.lan.information'];
    conf.pageHelpText = 'alert.lan-andel-sjukfall-per-kon.questionmark';
    return conf;
};

angular.module('StatisticsApp').casesPerBusinessConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerBusinessVerksamhet';
    conf.dataFetcherLandsting = 'getSjukfallPerBusinessLandsting';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerEnhet/csv';
    };
    conf.exportTableUrlLandsting = function () {
        return 'api/landsting/getNumberOfCasesPerEnhetLandsting/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per vårdenhet' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Vårdenhet';
    conf.chartFootnotes = ['alert.vardenhet.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperenhettidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperenhet', active: true}];

    return conf;
};

angular.module('StatisticsApp').casesPerPatientsPerBusinessConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherLandsting = 'getSjukfallPerPatientsPerBusinessLandsting';
    conf.exportTableUrlLandsting = function () {
        return 'api/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per 1000 listningar' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Vårdenhet';
    conf.chartYAxisTitle = 'Antal sjukfall per 1000 listningar';
    conf.pageHelpText = 'help.landsting-enhet-listningar';
    return conf;
};

angular.module('StatisticsApp').casesPerLakareConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakareVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakare/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per läkare' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Läkare';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaretidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakare', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakaresAlderOchKonVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getCasesPerDoctorAgeAndGenderStatistics/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall baserat på läkares kön och ålder' + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartXAxisTitle = 'Läkare';
    conf.pageHelpText = 'alert.lakarkon-alder.questionmark';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaresalderochkontidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakaresalderochkon', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakarbefattningVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakarbefattning/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall baserat på läkarbefattning' + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartXAxisTitle = 'Läkarbefattning';
    conf.chartFootnotes = ['alert.lakare-befattning.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakarbefattningtidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakarbefattning', active: true}];
    return conf;
};

angular.module('StatisticsApp').compareDiagnosis =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getCompareDiagnosisVerksamhet';
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return 'api/verksamhet/getJamforDiagnoserStatistik/' + diagnosisHash + '/csv';
    };
    conf.title = function (period, enhetsCount) {
        return 'Jämförelse av valfria diagnoser' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Diagnos';
    conf.showDiagnosisSelector = true;

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/jamforDiagnoserTidsserie', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/jamforDiagnoser', active: true}];

    return conf;
};

angular.module('StatisticsApp').casesPerMonthTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getNumberOfCasesPerMonthTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerMonthTvarsnitt/csv';
    };
    conf.title = function (months, enhetsCount) {
        return 'Antal sjukfall per månad' + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.chartXAxisTitle = '';
    conf.pageHelpText = 'help.casespermonth';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallPerManad', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallPerManadTvarsnitt', active: true}];

    return conf;
};


angular.module('StatisticsApp').longSickLeavesTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getLongSickLeavesTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getLongSickLeavesTvarsnitt/csv';
    };
    conf.title = function (months, enhetsCount) {
        return 'Antal långa sjukfall - mer än 90 dagar' + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.chartXAxisTitle = '';
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/langasjukskrivningar', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/langasjukskrivningartvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDegreeOfSickLeaveTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDegreeOfSickLeaveTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per sjukskrivningsgrad' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = '';
    conf.pageHelpText = 'help.degreeofsickleave';
    conf.chartFootnotes = ['alert.degreeofsickleave.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningsgrad', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningsgradtvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').differentieratIntygandeTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDifferentieratIntygandeTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDifferentieratIntygandeTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall för differentierat intygande' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = '';
    conf.pageHelpText = 'help.differentieratintygande';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/differentieratintygande', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/differentieratintygandetvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').diagnosisGroupTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDiagnosisGroupTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDiagnosGruppTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per diagnosgrupp' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = 'help.diagnosisgroup';
    conf.chartFootnotes = ['alert.diagnosisgroup.information'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/diagnosgrupp', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/diagnosgrupptvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupTvarsnittConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSubDiagnosisGroupTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return 'api/verksamhet/getDiagnosavsnittTvarsnitt/' + subgroupId + '/csv';
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
        {description: 'Tidsserie', state: '#/verksamhet/diagnosavsnitt', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/diagnosavsnitttvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerCountyConfig =
    /** @ngInject */
    function (ControllerCommons) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalCountyData';
    conf.exportTableUrl = 'api/getCountyStatistics/csv';
    conf.allowDecimalsYAxis = true;
    conf.title = function (period, enhetsCount) {
        return 'Antal sjukfall per 1000 invånare fördelat på län' + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = 'Län';
    conf.chartYAxisTitle = 'Antal sjukfall per 1000 invånare';
    conf.chartFootnotes = ['info.lan.information'];
    conf.exchangeableViews = null;
    return conf;
};
