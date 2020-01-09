/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

/* globals Highcharts */
angular.module('StatisticsApp').controller('singleLineChartCtrl',
    /** @ngInject */
    function($scope, $rootScope, $routeParams, $timeout, $window, $filter, statisticsData, config, $location,
        messageService, chartFactory, pdfFactory, _, ControllerCommons, filterViewState, $route, UserModel) {
      'use strict';

      var chart;

      var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
      var isRegion = ControllerCommons.isShowingRegion($location);

      var defaultChartType = 'line';
      var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, config, defaultChartType, isVerksamhet);
      $scope.activeChartType = chartTypeInfo.activeChartType;

      $scope.status = {
        isTableOpen: true,
        isChartCollapsed: false
      };

      $scope.chartContainers = [
        {id: 'chart1', name: 'diagram'}
      ];

      var paintChart = function(chartCategories, chartSeries, doneLoadingCallback) {
        var yAxisTitleUnit = config.chartYAxisTitleUnit ? config.chartYAxisTitleUnit : 'sjukfall';
        var chartConfigOptions = {
          categories: chartCategories,
          series: chartSeries,
          type: chartTypeInfo.activeHighchartType,
          doneLoadingCallback: doneLoadingCallback,
          overview: false,
          percentChart: chartTypeInfo.usePercentChart,
          stacked: chartTypeInfo.stacked,
          verticalLabel: false,
          labelMaxLength: null,
          unit: yAxisTitleUnit,
          maxWidthPercentage: 80
        };

        var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
        chartOptions.legend.enabled = false;
        if (config.chartYAxisTitle) {
          chartOptions.subtitle.text = config.chartYAxisTitle;
        }
        chartOptions.text = '#008391';
        chartOptions.tooltip.text = '#000';
        return new Highcharts.Chart(chartOptions);
      };

      var updateChart = function(ajaxResult, doneLoadingCallback) {
        $scope.series = chartFactory.addColor(ajaxResult.series);
        chartFactory.setColorToTotalCasesSeries($scope.series);
        destroyChart(chart);
        chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);

        $scope.series = chart.series;
      };

      $scope.switchChartType = function(chartType) {
        ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location,
            $routeParams);
      };

      $scope.showInLegend = function(index) {
        return chart && chartFactory.showInLegend(chart.series, index);
      };

      var populatePageWithDataSuccess = function(result) {
        ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.filterhash,
            result.filter.diagnoser, result.allAvailableDxsSelectedInFilter,
            result.filteredEnhets, result.allAvailableEnhetsSelectedInFilter,
            result.filter.sjukskrivningslangd, result.allAvailableSjukskrivningslangdsSelectedInFilter,
            result.filter.aldersgrupp, result.allAvailableAgeGroupsSelectedInFilter,
            result.filter.intygstyper, result.allAvailableIntygTypesSelectedInFilter);

        var messages = ControllerCommons.getResultMessageList(result, messageService);
        $scope.resultMessageList = ControllerCommons.removeFilterMessages(messages);
        filterViewState.setMessages(messages);

        $scope.subTitle = angular.isFunction(config.suffixTitle) ? config.suffixTitle($routeParams.kapitelId) : config.title;
        //Period should be on a separate row (INTYG-3288)
        $scope.subTitlePeriod = result.period;
        if (angular.isFunction(config.chartFootnotesExtra)) {
          var footnotesExtra = config.chartFootnotesExtra(result, isVerksamhet, isRegion, $filter);
          if (footnotesExtra) {
            $scope.chartFootnotes.push(footnotesExtra);
          }
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

      var populatePageWithData = function(result) {
        ControllerCommons.checkNationalResultAndEnableExport($scope, result, isVerksamhet, isRegion, populatePageWithDataSuccess);
      };

      $scope.toggleSeriesVisibility = function(index) {
        chartFactory.toggleSeriesVisibility(chart.series[index]);
      };

      $scope.exportChart = function() {
        chartFactory.exportChart(chart, $scope.viewHeader, $scope.subTitle + ' ' + $scope.subTitlePeriod);
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
          $route.reload();
        });
      };

      $scope.$watch(
          function() {
            return UserModel.get().settings[config.activeSettingProperty];
          },
          function(newValue, oldValue) {
            if (newValue !== oldValue) {
              $route.reload();
            }
          }
      );

      function refreshVerksamhet() {
        statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function() {
          $scope.dataLoadingError = true;
        });
      }

      function refreshRegion() {
        statisticsData[config.dataFetcherRegion](populatePageWithData, function() {
          $scope.dataLoadingError = true;
        });
      }

      if (isVerksamhet) {
        $scope.exportTableUrl = config.exportTableUrlVerksamhet();
        refreshVerksamhet();
      } else if (isRegion) {
        $scope.exportTableUrl = config.exportTableUrlRegion();
        refreshRegion();
      } else {
        $scope.exportTableUrl = config.exportTableUrl;
        statisticsData[config.dataFetcher](populatePageWithData, function() {
          $scope.dataLoadingError = true;
        });
      }
      $scope.exportTableUrl = ControllerCommons.combineUrl($scope.exportTableUrl, $scope.queryString);

      $scope.subTitle = config.title;
      $scope.chartFootnotes = angular.isFunction(config.chartFootnotes) ? config.chartFootnotes(isVerksamhet) : config.chartFootnotes;
      $scope.chartFootnotes = Array.isArray($scope.chartFootnotes) ? $scope.chartFootnotes : [];
      $scope.spinnerText = 'Laddar information...';
      $scope.doneLoading = false;
      $scope.dataLoadingError = false;

      $scope.showEnhetDepthOptions = config.showEnhetDepthOptions;
      $scope.vardenhetDepthOptionVardenhetSelected = $routeParams.vardenhetdepth !== 'false';
      if ($scope.showEnhetDepthOptions && $routeParams.vardenhetdepth === undefined) {
        $location.search('vardenhetdepth', 'true');
      }

      $scope.enhetDepthOptionChange = function() {
        $location.search('vardenhetdepth', !!$scope.vardenhetDepthOptionVardenhetSelected ? 'false' : 'true');
      };

      $scope.printPdf = function() {
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

angular.module('StatisticsApp').casesPerMonthConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.dataFetcher = 'getNumberOfCasesPerMonth';
      conf.dataFetcherVerksamhet = 'getNumberOfCasesPerMonthVerksamhet';
      conf.dataFetcherRegion = 'getNumberOfCasesPerMonthRegion';
      conf.exportTableUrl = 'api/getNumberOfCasesPerMonth?format=xlsx';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getNumberOfCasesPerMonth?format=xlsx';
      };
      conf.exportTableUrlRegion = function() {
        return 'api/region/getNumberOfCasesPerMonthRegion?format=xlsx';
      };
      conf.title = messageService.getProperty('title.sickleave');
      conf.chartFootnotesExtra = function(result, isVerksamhet, isRegion, $filter) {
        if (isRegion) {
          return $filter('messageFilter')('help.region.sjukfall-totalt', '', '', [result.fileUploadDate], '');
        }
      };

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallPerManad', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallPerManadTvarsnitt', active: false}];

      return conf;
    };

angular.module('StatisticsApp').longSickLeavesConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.dataFetcherVerksamhet = 'getLongSickLeavesDataVerksamhet';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getLongSickLeavesData?format=xlsx';
      };
      conf.title = messageService.getProperty('title.sickleavelength90');

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/langasjukskrivningar', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/langasjukskrivningartvarsnitt', active: false}];

      return conf;
    };

angular.module('StatisticsApp').meddelandenPerMonthConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.filter = {
        intygstyper: true,
        sjukskrivningslangd: false
      };
      conf.dataFetcher = 'getNumberOfMeddelandenPerMonth';
      conf.dataFetcherVerksamhet = 'getNumberOfMeddelandenPerMonthVerksamhet';
      conf.exportTableUrl = 'api/getNumberOfMeddelandenPerMonth?format=xlsx';
      conf.chartYAxisTitle = 'Antal meddelanden';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getNumberOfMeddelandenPerMonth?format=xlsx';
      };
      conf.title = messageService.getProperty('title.meddelanden');

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/meddelanden', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/meddelandenTvarsnitt', active: false}];

      return conf;
    };

angular.module('StatisticsApp').meddelandenPerAmneOchEnhetConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.filter = {
        intygstyper: true,
        sjukskrivningslangd: false
      };
      conf.dataFetcherVerksamhet = 'getMeddelandenPerAmneOchEnhetVerksamhet';
      conf.chartYAxisTitle = 'Antal meddelanden';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getMeddelandenPerAmnePerEnhet?format=xlsx';
      };
      conf.title = messageService.getProperty('title.meddelandenperamneochenhetverksamhet');
      conf.chartFootnotes = ['help.verksamhet.meddelandenperamneochenhetverksamhet'];
      conf.chartYAxisTitleUnit = 'meddelanden';

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/meddelandenPerAmneOchEnhet', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/meddelandenPerAmneOchEnhetTvarsnitt', active: false}];

      conf.showEnhetDepthOptions = true;

      return conf;
    };

angular.module('StatisticsApp').meddelandenPerAmneOchLakareConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.filter = {
        intygstyper: true,
        sjukskrivningslangd: false
      };
      conf.activeSettingProperty = 'showMessagesPerLakare';
      conf.dataFetcherVerksamhet = 'getMeddelandenPerAmneOchLakareVerksamhet';
      conf.chartYAxisTitle = 'Antal meddelanden';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getMeddelandenPerAmnePerLakare?format=xlsx';
      };
      conf.title = messageService.getProperty('title.meddelandenperamneochlakare');
      conf.chartFootnotes = ['help.verksamhet.meddelandenperamneochlakare'];
      conf.chartYAxisTitleUnit = 'meddelanden';

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/meddelandenPerAmneOchLakare', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/meddelandenPerAmneOchLakareTvarsnitt', active: false}];

      return conf;
    };

angular.module('StatisticsApp').intygPerMonthConfig =
    /** @ngInject */
    function(messageService) {
      'use strict';

      var conf = {};
      conf.filter = {
        intygstyper: false,
        sjukskrivningslangd: false
      };
      conf.dataFetcherVerksamhet = 'getNumberOfIntygPerMonthVerksamhet';
      conf.chartYAxisTitle = 'Antal intyg';
      conf.exportTableUrlVerksamhet = function() {
        return 'api/verksamhet/getNumberOfIntygPerMonth?format=xlsx';
      };
      conf.title = messageService.getProperty('title.intyg');

      conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/intyg', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/intygTvarsnitt', active: false}];

      return conf;
    };
