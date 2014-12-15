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

'use strict';

angular.module('StatisticsApp').controller('columnChartDetailsViewCtrl', [ '$scope', '$routeParams', '$window', '$timeout', 'statisticsData', 'businessFilter', 'config', 'messageService',

    function ($scope, $routeParams, $window, $timeout, statisticsData, businessFilter, config, messageService) {
        var isVerksamhet = $routeParams.verksamhetId ? true : false;
        var chart = {};

        $scope.chartContainers = [
            {id: "chart1", name: "diagram"}
        ];

        var paintChart = function (chartCategories, chartSeries) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
            chartOptions.chart.type = 'column';
            chartOptions.chart.marginLeft = 60;
            chartOptions.chart.marginTop = 27;
            chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
            chartOptions.xAxis.title.text = config.chartXAxisTitle;
            chartOptions.yAxis.title.text = config.percentChart ? "Andel sjukfall i %" : 'Antal sjukfall';
            chartOptions.yAxis.title.x = config.percentChart ? 60 : 30;
            chartOptions.yAxis.title.y = -13;
            chartOptions.yAxis.title.align = 'high';
            chartOptions.yAxis.title.offset = 0;
            chartOptions.yAxis.labels.formatter = function () {
                return ControllerCommons.makeThousandSeparated(this.value) + (config.percentChart ? "%" : "");
            };
            chartOptions.plotOptions.column.stacking = config.percentChart ? 'percent' : 'normal';
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult) {
            $scope.series = ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar");
            chart = paintChart(ajaxResult.categories, $scope.series);
        };

        $scope.toggleSeriesVisibility = function (index) {
            var series = chart.series[index];
            if (series.visible) {
                series.hide();
            } else {
                series.show();
            }
        };

        var populatePageWithData = function (result) {
            $scope.subTitle = config.title(result.period);
            $scope.doneLoading = true;
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData);

                if ($routeParams.printBw || $routeParams.print) {
                    ControllerCommons.printAndCloseWindow($timeout, $window);
                }
            }, 1);
        };

        function refreshVerksamhet(samePage) {
            statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, businessFilter.getSelectedBusinesses(samePage), businessFilter.getSelectedDiagnoses(samePage), populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        $scope.$on('filterChange', function (event, data) {
            if (isVerksamhet) {
                refreshVerksamhet(true);
            }
        });

        if (isVerksamhet) {
            $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId);
            refreshVerksamhet(false);
        } else {
            $scope.exportTableUrl = config.exportTableUrl;
            statisticsData[config.dataFetcher](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.spinnerText = "Laddar information...";
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;

        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, "", null, true);
        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, "", null, true);
        });


        $scope.exportChart = function () {
            ControllerCommons.exportChart(chart, $scope.pageName);
        };

        $scope.print = function (bwPrint) {
            window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
        };

    }
]);

angular.module('StatisticsApp').nationalSickLeaveLengthConfig = function () {
    var conf = {};
    conf.dataFetcher = "getNationalSickLeaveLengthData";
    conf.dataFetcherVerksamhet = "getSickLeaveLengthDataVerksamhet";
    conf.exportTableUrl = "api/getSickLeaveLengthData/csv";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthData/csv"
    };
    conf.title = function (period) {
        return "Antal sjukfall per sjukskrivningslängd " + period;
    };
    conf.chartXAxisTitle = "Sjukskrivningslängd";
    return conf;
};

angular.module('StatisticsApp').nationalSickLeaveLengthCurrentConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSickLeaveLengthCurrentDataVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthCurrentData/csv";
    };
    conf.title = function (month) {
        return "Antal pågående sjukfall per sjukskrivningslängd " + month;
    };
    conf.chartXAxisTitle = "Sjukskrivningslängd";
    conf.pageHelpText = "help.sick-leave-length-current"
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupConfig = function () {
    var conf = {};
    conf.dataFetcher = "getAgeGroups";
    conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet";
    conf.exportTableUrl = "api/getAgeGroupsStatistics/csv";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getAgeGroupsStatistics/csv";
    };
    conf.title = function (period) {
        return "Antal sjukfall per åldersgrupp " + period;
    };
    conf.chartXAxisTitle = "Åldersgrupp";
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupCurrentConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getAgeGroupsCurrentVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getAgeGroupsCurrentStatistics/csv";
    };
    conf.title = function (month) {
        return "Antal pågående sjukfall per åldersgrupp " + month;
    };
    conf.chartXAxisTitle = "Åldersgrupp";
    conf.pageHelpText = "help.age-group-current"
    return conf;
};

angular.module('StatisticsApp').casesPerSexConfig = function () {
    var conf = {};
    conf.dataFetcher = "getNationalSjukfallPerSexData";
    conf.exportTableUrl = "api/getSjukfallPerSexStatistics/csv";
    conf.title = function (period) {
        return "Andel sjukfall per kön per län " + period;
    };
    conf.percentChart = true;
    conf.chartXAxisTitle = "Län";
    conf.chartFootnotes = ["info.lan.information"];
    return conf;
};

angular.module('StatisticsApp').casesPerBusinessConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerBusinessVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerBusinessVerksamhet/csv";
    };
    conf.title = function (period) {
        return "Antal sjukfall per vårdenhet " + period;
    };
    conf.chartXAxisTitle = "Vårdenhet";
    return conf;
};

angular.module('StatisticsApp').casesPerLakareConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakareVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakareVerksamhet/csv";
    };
    conf.title = function (period) {
        return "Antal sjukfall per läkare " + period;
    };
    conf.chartXAxisTitle = "Läkare";
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakaresAlderOchKonVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakaresAlderOchKonVerksamhet/csv";
    };
    conf.title = function (period) {
        return "Antal sjukfall baserat på läkares kön och ålder " + period;
    };
    conf.chartXAxisTitle = "Läkare";
    conf.pageHelpText = "help.lakare-alder-och-kon"
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakarbefattningVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakarbefattningVerksamhet/csv";
    };
    conf.title = function (period) {
        return "Antal sjukfall baserat på läkarbefattning " + period;
    };
    conf.chartXAxisTitle = "Läkarbefattning";
    return conf;
};
