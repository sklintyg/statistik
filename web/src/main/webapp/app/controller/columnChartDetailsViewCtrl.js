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
    function ($scope, $rootScope, $routeParams, $window, $location, $timeout, $filter, statisticsData,
        config, messageService, chartFactory, pdfFactory, _, ControllerCommons, filterViewState, StaticDataService) {
        'use strict';

        function ensureHighchartTypeIsSet(config) {
            if (!config.hasOwnProperty('highchartType')) {
                config.highchartType = 'column';
            }
            return config;
        }

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
        var isLandsting = ControllerCommons.isShowingLandsting($location);
        var chart = {};
        var defaultChartType = 'column';
        var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, ensureHighchartTypeIsSet(config), defaultChartType);
        $scope.activeChartType = chartTypeInfo.activeChartType;

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram'}
        ];
        $scope.status = {
            isTableOpen: true,
            isChartCollapsed: false
        };

        $scope.chartStyle = {};

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartConfigOptions = {
                categories: chartCategories,
                series: chartSeries,
                type: chartTypeInfo.activeHighchartType,
                doneLoadingCallback: doneLoadingCallback,
                overview: false,
                percentChart: chartTypeInfo.usePercentChart,
                stacked: chartTypeInfo.stacked,
                verticalLabel: config.chartVerticalLabel,
                labelMaxLength: config.chartLabelLength,
                unit: config.chartYAxisTitleUnit ? config.chartYAxisTitleUnit : 'sjukfall',
                usingAndel: config.usingAndel
            };


            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
            chartOptions.legend.enabled = false;
            chartOptions.yAxis.allowDecimals = !!config.allowDecimalsYAxis;
            if (config.chartYAxisTitle) {
                chartOptions.subtitle.text = config.chartYAxisTitle;
            }

            $scope.chartStyle.height = chartOptions.chart.height + 'px';
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            destroyChart(chart);

            if (angular.isFunction(config.hideChart) && config.hideChart(ajaxResult)) {
                $scope.series = [];
                $scope.doneLoading = true;
                $scope.hideChart = true;
                $scope.resultMessageList = ControllerCommons.removeChartMessages($scope.resultMessageList);

                return;
            }

            $scope.hideChart = false;
            $scope.series = chartFactory.addColor(ajaxResult.series);

            var categories = ajaxResult.categories;

            if (angular.isFunction(config.convertChartCategories)) {
                categories = config.convertChartCategories(categories);
            }

            chartFactory.setColorToTotalCasesSeries($scope.series);
            chart = paintChart(categories, $scope.series, doneLoadingCallback);
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location, $routeParams);
        };

        $scope.showInLegend = function(index) {
            return chart && chartFactory.showInLegend(chart.series, index);
        };

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(chart.series[index]);
        };

        $scope.reportActive = function() {
            return ControllerCommons.reportActive(config.activeSettingProperty);
        };

        $scope.activateReport = function() {
            $scope.saving = true;
            ControllerCommons
                .activateReport(config.activeSettingProperty)
                .finally(function() {
                    $scope.saving = false;
                });
        };

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? '#/verksamhet/diagnosavsnitttvarsnitt' : '#/nationell/diagnosavsnitttvarsnitt';
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        };

        var populatePageWithDataSuccess = function(result) {
            $scope.subTitle = angular.isFunction(config.suffixTitle) ? config.suffixTitle($routeParams.kapitelId) : config.title;
            //Period should be on a separate row (INTYG-3288)
            $scope.subTitlePeriod = result.period;
            if (angular.isFunction(config.chartFootnotesExtra)) {
                var footnotesExtra = config.chartFootnotesExtra(result, isVerksamhet, isLandsting, $filter);
                if (footnotesExtra) {
                    $scope.chartFootnotes.push(footnotesExtra);
                }
            }

            ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.filterhash,
                result.filter.diagnoser, result.allAvailableDxsSelectedInFilter,
                result.filteredEnhets, result.allAvailableEnhetsSelectedInFilter,
                result.filter.sjukskrivningslangd, result.allAvailableSjukskrivningslangdsSelectedInFilter,
                result.filter.aldersgrupp, result.allAvailableAgeGroupsSelectedInFilter,
                result.filter.intygstyp, result.allAvailableIntygTypesSelectedInFilter);
            var messages = ControllerCommons.getResultMessageList(result, messageService);
            $scope.resultMessageList = ControllerCommons.removeFilterMessages(messages);
            filterViewState.setMessages(messages);

            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnittAndKategori(populateDetailsOptions, function() {
                    $location.path('/fetchdxsfailed');
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
            StaticDataService.get().then(function() {
                statisticsData[config.dataFetcherVerksamhet](
                    populatePageWithData,
                    function () {
                        $scope.dataLoadingError = true;
                    },
                    ControllerCommons.getExtraPathParam($routeParams));
            });
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
            $scope.exportTableUrl = ControllerCommons.combineUrl($scope.exportTableUrl, $scope.queryString);
        } else {
            $scope.doneLoading = true;
        }

        $scope.routeParams = $routeParams;
        $scope.subTitle = config.title;
        $scope.chartFootnotes = angular.isFunction(config.chartFootnotes) ? config.chartFootnotes(isVerksamhet, isLandsting) : config.chartFootnotes;
        $scope.chartFootnotes = Array.isArray($scope.chartFootnotes) ? $scope.chartFootnotes : [];
        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;
        $scope.showDetailsOptions3 = config.showDetailsOptions3 && isVerksamhet;

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;

        $scope.exportChart = function () {
            chartFactory.exportChart(chart, $scope.viewHeader, $scope.subTitle);
        };

        $scope.printPdf = function () {
            pdfFactory.print($scope, chart);
        };

        function destroyChart(chartToDestroy) {
            if (chartToDestroy && typeof chartToDestroy.destroy === 'function') {
                chartToDestroy.destroy();
            }
        }

        $scope.$on('$destroy', function() {
            destroyChart(chart);
        });

        // Set filter state
        filterViewState.set(config.filter);
    }
);

angular.module('StatisticsApp').nationalSickLeaveLengthConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNationalSickLeaveLengthData';
    conf.dataFetcherVerksamhet = 'getSickLeaveLengthDataVerksamhet';
    conf.exportTableUrl = 'api/getSickLeaveLengthData?format=xlsx';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSickLeaveLengthData?format=xlsx';
    };
    conf.title = messageService.getProperty('title.sickleavelength');
    conf.chartFootnotes = function(isVerksamhet) {
        var text = ['help.nationell.sickleavelength'];

        if (isVerksamhet) {
            text.push('help.verksamhet.sickleavelength');
        }
        return text;
    };

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
    conf.exportTableUrl = 'api/getAgeGroupsStatistics?format=xlsx';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getAgeGroupsStatistics?format=xlsx';
    };
    conf.title = messageService.getProperty('title.agegroup');

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
    conf.exportTableUrl = 'api/getSjukfallPerSexStatistics?format=xlsx';
    conf.title = messageService.getProperty('title.lan.gender');
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
    conf.highchartType = 'column';
    conf.defaultChartType = 'stackedcolumn';
    conf.dataFetcherVerksamhet = 'getSjukfallPerBusinessVerksamhet';
    conf.dataFetcherLandsting = 'getSjukfallPerBusinessLandsting';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerEnhet?format=xlsx';
    };
    conf.exportTableUrlLandsting = function () {
        return 'api/landsting/getNumberOfCasesPerEnhetLandsting?format=xlsx';
    };
    conf.title = messageService.getProperty('title.vardenhet');
    conf.chartVerticalLabel = true;
    conf.chartLabelLength = 40;
    conf.chartFootnotesExtra = function(result, isVerksamhet, isLandsting, $filter) {
        if (isLandsting) {
            return $filter('messageFilter')('help.landsting.vardenhet', '', '', [result.fileUploadDate], '');
        }

        return $filter('messageFilter')('help.verksamhet.vardenhet', '', '', [], '');
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
        return 'api/landsting/getNumberOfCasesPerPatientsPerEnhetLandsting?format=xlsx';
    };
    conf.title = messageService.getProperty('title.vardenhet-listning');
    conf.chartFootnotes = ['help.landsting.vardenhet-listning1'];
    conf.chartFootnotesExtra = function(result, isVerksamhet, isLandsting, $filter) {
        return $filter('messageFilter')('help.landsting.vardenhet-listning2', '', '', [result.fileUploadDate], '');
    };

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
        return 'api/verksamhet/getNumberOfCasesPerLakare?format=xlsx';
    };
    conf.title = messageService.getProperty('title.lakare');
    conf.chartFootnotes = ['help.verksamhet.lakare'];
    conf.chartVerticalLabel = true;

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
        return 'api/verksamhet/getCasesPerDoctorAgeAndGenderStatistics?format=xlsx';
    };
    conf.title = messageService.getProperty('title.lakaregender');
    conf.chartFootnotes = ['help.verksamhet.lakaregender'];
    conf.chartVerticalLabel = true;

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
        return 'api/verksamhet/getNumberOfCasesPerLakarbefattning?format=xlsx';
    };
    conf.title = messageService.getProperty('title.lakare-befattning');
    conf.chartFootnotes = ['help.verksamhet.lakare-befattning'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakarbefattningtidsserie', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakarbefattning', active: true}];
    return conf;
};

angular.module('StatisticsApp').compareDiagnosis =
    /** @ngInject */
    function (messageService, MAX_SELECTED_DXS) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getCompareDiagnosisVerksamhet';
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return 'api/verksamhet/getJamforDiagnoserStatistik/' + diagnosisHash + '?format=xlsx';
    };
    conf.title = messageService.getProperty('title.diagnoscompare');
    conf.chartVerticalLabel = true;
    conf.showDiagnosisSelector = true;
    conf.hideChart = function(data) {
        return data.categories.length > MAX_SELECTED_DXS;
    };

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
        return 'api/verksamhet/getNumberOfCasesPerMonthTvarsnitt?format=xlsx';
    };
    conf.title = messageService.getProperty('title.sickleave');

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
        return 'api/verksamhet/getNumberOfMeddelandenPerMonthTvarsnitt?format=xlsx';
    };
    conf.title = messageService.getProperty('title.meddelanden');

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
    conf.filter = {
        intygstyper: false,
        sjukskrivningslangd: false
    };
    conf.dataFetcherVerksamhet = 'getNumberOfIntygPerMonthTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfIntygPerMonthTvarsnitt?format=xlsx';
    };
    conf.title = messageService.getProperty('title.intyg');
    conf.chartYAxisTitle = 'Antal intyg';

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/intyg', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/intygTvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').intygPerTypeTvarsnittConfig =
    /** @ngInject */
    function (messageService, chartFactory) {
    'use strict';

    var conf = {};
    conf.filter = {
        intygstyper: false,
        sjukskrivningslangd: false
    };
    conf.dataFetcherVerksamhet = 'getIntygPerTypeTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getIntygPerTypeTvarsnitt?format=xlsx';
    };
    conf.title = messageService.getProperty('title.intygstyp');
    conf.chartYAxisTitle = 'Antal intyg';
    conf.convertChartCategories = chartFactory.addCategoryIntygTooltip;

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/intygPerTyp', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/intygPerTypTvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').longSickLeavesTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getLongSickLeavesTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getLongSickLeavesTvarsnitt?format=xlsx';
    };
    conf.title = messageService.getProperty('title.sickleavelength90');
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
        return 'api/verksamhet/getDegreeOfSickLeaveTvarsnitt?format=xlsx';
    };
    conf.showDetailsOptions = false;
    conf.title = messageService.getProperty('title.degreeofsickleave');
    conf.chartFootnotes = ['help.nationell.degreeofsickleave'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningsgrad', active: false},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningsgradtvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').diagnosisGroupTvarsnittConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getDiagnosisGroupTvarsnittVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDiagnosGruppTvarsnitt?format=xlsx';
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
        return 'api/verksamhet/getDiagnosavsnittTvarsnitt/' + subgroupId + '?format=xlsx';
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.showDetailsOptions3 = true;
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
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
    conf.exportTableUrl = 'api/getCountyStatistics?format=xlsx';
    conf.allowDecimalsYAxis = true;
    conf.title = messageService.getProperty('title.lan');
    conf.chartYAxisTitle = 'Antal sjukfall per 1000 invånare';
    conf.chartFootnotes = ['help.nationell.lan'];
    conf.chartFootnotesExtra = function(result) {
        return 'Rapporten jämför antal sjukfall med Statistiska Centralbyråns befolkningssiffror från ' + result.sourceDate + '.';
    };
    conf.exchangeableViews = null;
    return conf;
};

angular.module('StatisticsApp').meddelandenPerAmneTvarsnittConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.filter = {
            intygstyper: true,
            sjukskrivningslangd: false
        };
        conf.chartYAxisTitleUnit = 'meddelanden';
        conf.dataFetcherVerksamhet = 'getMeddelandenPerAmneTvarsnittVerksamhet';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getMeddelandenPerAmneTvarsnitt?format=xlsx';
        };
        conf.title = messageService.getProperty('title.meddelandenperamne');
        conf.chartFootnotes = ['help.nationell.degreeofsickleave'];

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/meddelandenPerAmne', active: false},
            {description: 'Tvärsnitt', state: '/verksamhet/meddelandenPerAmneTvarsnitt', active: true}];

        return conf;
    };

angular.module('StatisticsApp').meddelandenPerAmneOchEnhetTvarsnittConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.filter = {
            intygstyper: true,
            sjukskrivningslangd: false
        };
        conf.highchartType = 'column';
        conf.defaultChartType = 'stackedcolumn';
        conf.dataFetcherVerksamhet = 'getMeddelandenPerAmneOchEnhetTvarsnittVerksamhet';
        conf.chartYAxisTitleUnit = 'meddelanden';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getMeddelandenPerAmnePerEnhetTvarsnitt?format=xlsx';
        };
        conf.title = messageService.getProperty('title.meddelandenperamneochenhet');
        conf.chartFootnotes = ['help.verksamhet.meddelandenperamneochenhet'];

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/meddelandenPerAmneOchEnhet', active: false},
            {description: 'Tvärsnitt', state: '/verksamhet/meddelandenPerAmneOchEnhetTvarsnitt', active: true}];

        return conf;
    };

angular.module('StatisticsApp').meddelandenPerAmneOchLakareTvarsnittConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.filter = {
            intygstyper: true,
            sjukskrivningslangd: false
        };
        conf.activeSettingProperty = 'showMessagesPerLakare';
        conf.highchartType = 'column';
        conf.defaultChartType = 'stackedcolumn';
        conf.dataFetcherVerksamhet = 'getMeddelandenPerAmneOchLakareTvarsnittVerksamhet';
        conf.chartYAxisTitleUnit = 'meddelanden';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getMeddelandenPerAmnePerLakareTvarsnitt?format=xlsx';
        };
        conf.title = messageService.getProperty('title.meddelandenperamneochlakare');
        conf.chartFootnotes = ['help.verksamhet.meddelandenperamneochlakare'];

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/meddelandenPerAmneOchLakare', active: false},
            {description: 'Tvärsnitt', state: '/verksamhet/meddelandenPerAmneOchLakareTvarsnitt', active: true}];

        return conf;
    };

angular.module('StatisticsApp').meddelandenPerAmneOchEnhetLandstingConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.filter = {
            intygstyper: true,
            sjukskrivningslangd: false
        };
        conf.highchartType = 'column';
        conf.defaultChartType = 'stackedcolumn';
        conf.dataFetcherLandsting = 'getMeddelandenPerAmneOchEnhetLandsting';
        conf.chartYAxisTitleUnit = 'meddelanden';
        conf.exportTableUrlLandsting = function () {
            return 'api/landsting/getMeddelandenPerAmneOchEnhetLandsting?format=xlsx';
        };
        conf.title = messageService.getProperty('title.meddelandenperamneochenhet');
        conf.chartFootnotes = [];
        conf.chartFootnotesExtra = function(result, isVerksamhet, isLandsting, $filter) {
            return $filter('messageFilter')('help.landsting.meddelandenperamneochenhet', '', '', [result.fileUploadDate], '');
        };
        return conf;
    };

angular.module('StatisticsApp').andelKompletteringarTvarsnittConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.dataFetcherVerksamhet = 'getAndelKompletteringarTvarsnittVerksamhet';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getAndelKompletteringarTvarsnitt?format=xlsx';
        };
        conf.showDetailsOptions = false;
        conf.title = messageService.getProperty('title.andelkompletteringar');
        conf.chartYAxisTitleUnit = 'intyg';
        conf.usingAndel = true;
        conf.chartYAxisTitle = 'Andel intyg';

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/andelkompletteringar', active: false},
            {description: 'Tvärsnitt', state: '/verksamhet/andelkompletteringartvarsnitt', active: true}];

        return conf;
    };
