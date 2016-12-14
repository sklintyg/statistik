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
    /** @ngInject */
    function ($scope, $rootScope, $routeParams, $window, $location, $timeout, statisticsData, diagnosisTreeFilter,
        config, messageService, chartFactory, pdfFactory, _, ControllerCommons) {
        'use strict';

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
        var isLandsting = ControllerCommons.isShowingLandsting($location);
        var chart = {};
        var defaultChartType = 'column';
        var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, config, defaultChartType);
        $scope.activeChartType = chartTypeInfo.activeChartType;

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram'}
        ];
        $scope.status = {
            isTableOpen: true
        };

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback, false, chartTypeInfo.usePercentChart, chartTypeInfo.stacked);
            chartOptions.chart.type = chartTypeInfo.activeHighchartType;
            chartOptions.legend.enabled = false;
            chartOptions.yAxis.allowDecimals = !!config.allowDecimalsYAxis;
            chartOptions.xAxis.title.text = config.chartXAxisTitle;
            if (config.chartYAxisTitle) {
                chartOptions.subtitle.text = config.chartYAxisTitle;
            }
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            $scope.series = chartFactory.addColor(ajaxResult.series);
            chartFactory.setColorToTotalCasesSeries($scope.series);
            chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location, $routeParams);
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

        var populatePageWithDataSuccess = function(result) {
            $scope.subTitlePeriod = angular.isFunction(config.suffixTitle) ? config.suffixTitle(result.period, $routeParams.kapitelId) : result.period;
            if (angular.isFunction(config.chartFootnotesExtra)) {
                $scope.chartFootnotes.push(config.chartFootnotesExtra(result));
            }

            ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser,
                result.allAvailableDxsSelectedInFilter, result.filter.filterhash,
                result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets,
                result.filter.sjukskrivningslangd, result.allAvailableSjukskrivningslangdsSelectedInFilter,
                result.filter.aldersgrupp, result.allAvailableAgeGroupsSelectedInFilter);
            $scope.resultMessageList = ControllerCommons.getResultMessageList(result, messageService);
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
        };

        var populatePageWithData = function (result) {
            ControllerCommons.checkNationalResultAndEnableExport($scope, result, isVerksamhet, isLandsting, populatePageWithDataSuccess);
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

        $scope.subTitle = config.title;
        $scope.showDetailOptions3PopoverText = messageService.getProperty(config.pageHelpTextShowDetailOptions, null, '', null, true);
        $scope.chartFootnotes = angular.isFunction(config.chartFootnotes) ? config.chartFootnotes(isVerksamhet, isLandsting) : config.chartFootnotes;
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
);

angular.module('StatisticsApp').nationalSickLeaveLengthConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalSickLeaveLengthData';
    conf.dataFetcherVerksamhet = 'getSickLeaveLengthDataVerksamhet';
    conf.exportTableUrl = 'api/getSickLeaveLengthData/csv';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSickLeaveLengthData/csv';
    };
    conf.title = messageService.getProperty('title.sickleavelength');
    conf.chartFootnotes = function(isVerksamhet) {
        var text = ['help.nationell.sickleavelength'];

        if (isVerksamhet) {
            text.push('help.verksamhet.sickleavelength');
        }
        return text;
    };
    conf.chartXAxisTitle = 'Sjukskrivningslängd';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningslangdTidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningslangd', active: true}];
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getAgeGroups';
    conf.dataFetcherVerksamhet = 'getAgeGroupsVerksamhet';
    conf.exportTableUrl = 'api/getAgeGroupsStatistics/csv';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getAgeGroupsStatistics/csv';
    };
    conf.title = messageService.getProperty('title.agegroup');
    conf.chartXAxisTitle = 'Åldersgrupp';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/aldersgrupperTidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/aldersgrupper', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerSexConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalSjukfallPerSexData';
    conf.exportTableUrl = 'api/getSjukfallPerSexStatistics/csv';
    conf.title = messageService.getProperty('title.lan.gender');
    conf.chartXAxisTitle = 'Län';
    conf.chartFootnotes = ['help.nationell.lan.gender'];
    conf.percentChart = true;
    conf.highchartType = 'column';
    return conf;
};

angular.module('StatisticsApp').casesPerBusinessConfig =
    /** @ngInject */
    function (messageService) {
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
    conf.title = messageService.getProperty('title.vardenhet');
    conf.chartXAxisTitle = 'Vårdenhet';
    conf.chartFootnotes = function(isVerksamhet, isLandsting) {
        if (isLandsting) {
            return ['help.landsting.vardenhet'];
        }

        return ['help.verksamhet.vardenhet'];
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperenhettidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperenhet', active: true}];

    return conf;
};

angular.module('StatisticsApp').casesPerPatientsPerBusinessConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherLandsting = 'getSjukfallPerPatientsPerBusinessLandsting';
    conf.exportTableUrlLandsting = function () {
        return 'api/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting/csv';
    };
    conf.title = messageService.getProperty('title.vardenhet-listning');
    conf.chartFootnotes = ['help.landsting.vardenhet-listning1', 'help.landsting.vardenhet-listning2'];

    conf.chartXAxisTitle = 'Vårdenhet';
    conf.chartYAxisTitle = 'Antal sjukfall per 1000 listningar';
    return conf;
};

angular.module('StatisticsApp').casesPerLakareConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakareVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakare/csv';
    };
    conf.title = messageService.getProperty('title.lakare');
    conf.chartFootnotes = ['help.verksamhet.lakare'];
    conf.chartXAxisTitle = 'Läkare';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaretidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakare', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakaresAlderOchKonVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getCasesPerDoctorAgeAndGenderStatistics/csv';
    };
    conf.title = messageService.getProperty('title.lakaregender');
    conf.chartFootnotes = ['help.verksamhet.lakaregender'];
    conf.chartXAxisTitle = 'Läkare';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaresalderochkontidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakaresalderochkon', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakarbefattningVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakarbefattning/csv';
    };
    conf.title = messageService.getProperty('title.lakare-befattning');
    conf.chartXAxisTitle = 'Läkarbefattning';
    conf.chartFootnotes = ['help.verksamhet.lakare-befattning'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakarbefattningtidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakarbefattning', active: true}];
    return conf;
};

angular.module('StatisticsApp').compareDiagnosis =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getCompareDiagnosisVerksamhet';
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return 'api/verksamhet/getJamforDiagnoserStatistik/' + diagnosisHash + '/csv';
    };
    conf.title = messageService.getProperty('title.diagnoscompare');
    conf.chartXAxisTitle = 'Diagnos';
    conf.showDiagnosisSelector = true;

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/jamforDiagnoserTidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/jamforDiagnoser', active: true}];

    return conf;
};

angular.module('StatisticsApp').casesPerMonthTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getNumberOfCasesPerMonthTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerMonthTvarsnitt/csv';
    };
    conf.title = messageService.getProperty('title.sickleave');
    conf.chartXAxisTitle = '';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallPerManad', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallPerManadTvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').meddelandenPerMonthTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getNumberOfMeddelandenPerMonthTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfMeddelandenPerMonthTvarsnitt/csv';
    };
    conf.title = messageService.getProperty('title.meddelanden');
    conf.chartXAxisTitle = '';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/meddelanden', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/meddelandenTvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').intygPerMonthTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getNumberOfIntygPerMonthTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfIntygPerMonthTvarsnitt/csv';
    };
    conf.title = messageService.getProperty('title.intyg');
    conf.chartYAxisTitle = 'Antal intyg';
    conf.chartXAxisTitle = '';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/intyg', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/intygTvarsnitt', active: false}];

    return conf;
};


angular.module('StatisticsApp').longSickLeavesTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getLongSickLeavesTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getLongSickLeavesTvarsnitt/csv';
    };
    conf.title = messageService.getProperty('title.sickleavelength90');
    conf.chartXAxisTitle = '';
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/langasjukskrivningar', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/langasjukskrivningartvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDegreeOfSickLeaveTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDegreeOfSickLeaveTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = messageService.getProperty('title.degreeofsickleave');
    conf.chartXAxisTitle = '';
    conf.chartFootnotes = ['help.nationell.degreeofsickleave'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningsgrad', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningsgradtvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').differentieratIntygandeTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDifferentieratIntygandeTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDifferentieratIntygandeTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = messageService.getProperty('title.differentierat');
    conf.chartFootnotes = ['help.verksamhet.differentierat1', 'help.verksamhet.differentierat2', 'help.verksamhet.differentierat3'];
    conf.chartXAxisTitle = '';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/differentieratintygande', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/differentieratintygandetvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').diagnosisGroupTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDiagnosisGroupTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDiagnosGruppTvarsnitt/csv';
    };
    conf.showDetailsOptions = false;
    conf.title = messageService.getProperty('title.diagnosisgroup');
    conf.chartFootnotes = ['help.nationell.diagnosisgroup'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosgrupp', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosgrupptvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSubDiagnosisGroupTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return 'api/verksamhet/getDiagnosavsnittTvarsnitt/' + subgroupId + '/csv';
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.showDetailsOptions3 = true;
    conf.suffixTitle = function (period, name) {
        return name + ' ' + period;
    };
    conf.title = messageService.getProperty('title.diagnosgroup');
    conf.pageHelpTextShowDetailOptions = 'help.diagnosissubgroup.showdetailoptions';
    conf.chartFootnotes = function(isVerksamhet) {
        if (isVerksamhet) {
            return ['help.verksamhet.diagnosgroup'];
        }

        return ['help.nationell.diagnosgroup'];
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosavsnitt', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosavsnitttvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerCountyConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalCountyData';
    conf.exportTableUrl = 'api/getCountyStatistics/csv';
    conf.allowDecimalsYAxis = true;
    conf.title = messageService.getProperty('title.lan');
    conf.chartXAxisTitle = 'Län';
    conf.chartYAxisTitle = 'Antal sjukfall per 1000 invånare';
    conf.chartFootnotes = ['help.nationell.lan'];
    conf.chartFootnotesExtra = function(result) {
        return 'Rapporten jämför antal sjukfall med Statistiska Centralbyråns befolkningssiffror från ' + result.sourceDate + '.';
    };
    conf.exchangeableViews = null;
    return conf;
};
