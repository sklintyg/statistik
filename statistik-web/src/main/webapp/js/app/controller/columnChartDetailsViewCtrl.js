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

angular.module('StatisticsApp').controller('columnChartDetailsViewCtrl', [ '$scope', '$rootScope', '$routeParams', '$window', '$location', '$timeout', 'statisticsData', 'diagnosisTreeFilter', 'config', 'messageService', 'printFactory',
    function ($scope, $rootScope, $routeParams, $window, $location, $timeout, statisticsData, diagnosisTreeFilter, config, messageService, printFactory) {
        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
        var chart = {};

        $scope.chartContainers = [
            {id: "chart1", name: "diagram"}
        ];

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback);
            chartOptions.chart.type = 'column';
            chartOptions.chart.marginLeft = 60;
            chartOptions.chart.marginTop = 27;

            //Set the chart.width to a fixed width when we are about to print.
            //It will prevent the chart from overflowing the printed page.
            //Maybe there is some better way around this since this is not very responsive.
            if($routeParams.printBw || $routeParams.print) {
                chartOptions.chart.width = 768;
            }

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
            if (config.percentChart) {
                chartOptions.tooltip.pointFormat = '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.0f}%</b><br/>';
            }
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            $scope.series = printFactory.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar");
            chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);
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
            $scope.subTitle = config.title(result.period, result.filter.enheter ? result.filter.enheter.length : null);
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.resultMessage = result.message;
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData, function() { $scope.doneLoading = true; });


                if ($routeParams.printBw || $routeParams.print) {
                    printFactory.printAndCloseWindow($timeout, $window);
                }
            }, 1);
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, $routeParams.diagnosHash);
        }

        var diagnosHashExists = function diagnosHashExists() {
            return $routeParams.diagnosHash !== "-";
        };

        if (diagnosHashExists()) {
            $scope.spinnerText = "Laddar information...";
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.diagnosHash);
                refreshVerksamhet();
            } else {
                $scope.exportTableUrl = config.exportTableUrl;
                statisticsData[config.dataFetcher](populatePageWithData, function () {
                    $scope.dataLoadingError = true;
                });
            }
        } else {
            $scope.doneLoading = true;
        }

        if (config.alternativeView) {
            $scope.alternativeView = config.alternativeView + ($routeParams.diagnosHash ? "/" + $routeParams.diagnosHash : "");
        }
        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;

        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, "", null, true);

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, "", null, true);
        });

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        if ($scope.showDiagnosisSelector) {
            ControllerCommons.setupDiagnosisSelector(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location);
        }

        $scope.exportChart = function () {
            ControllerCommons.exportChart(chart, $scope.pageName, $scope.subTitle, $scope.activeDiagnosFilters);
        };

        $scope.print = function (bwPrint) {
            printFactory.print(bwPrint, $rootScope, $window);
        };

        $scope.$on('$destroy', function() {
            if(typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });
    }
]);

angular.module('StatisticsApp').nationalSickLeaveLengthConfig = function () {
    var conf = {};
    conf.dataFetcher = "getNationalSickLeaveLengthData";
    conf.dataFetcherVerksamhet = "getSickLeaveLengthDataVerksamhet";
    conf.exportTableUrl = "api/getSickLeaveLengthData/csv";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getSickLeaveLengthData/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per sjukskrivningslängd" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Sjukskrivningslängd";
    conf.chartFootnotes = ["info.sickleavelength"];
    conf.pageHelpText = "help.sickleavelength";
    return conf;
};

angular.module('StatisticsApp').nationalSickLeaveLengthCurrentConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSickLeaveLengthCurrentDataVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getSickLeaveLengthCurrentData/csv";
    };
    conf.title = function (month, enhetsCount) {
        return "Antal pågående sjukfall per sjukskrivningslängd" + ControllerCommons.getEnhetCountText(enhetsCount, false) + month;
    };
    conf.chartXAxisTitle = "Sjukskrivningslängd";
    conf.pageHelpText = "help.sick-leave-length-current";
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupConfig = function () {
    var conf = {};
    conf.dataFetcher = "getAgeGroups";
    conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet";
    conf.exportTableUrl = "api/getAgeGroupsStatistics/csv";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getAgeGroupsStatistics/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per åldersgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Åldersgrupp";
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupCurrentConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getAgeGroupsCurrentVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getAgeGroupsCurrentStatistics/csv";
    };
    conf.title = function (month, enhetsCount) {
        return "Antal pågående sjukfall per åldersgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + month;
    };
    conf.chartXAxisTitle = "Åldersgrupp";
    conf.pageHelpText = "help.age-group-current"
    return conf;
};

angular.module('StatisticsApp').casesPerSexConfig = function () {
    var conf = {};
    conf.dataFetcher = "getNationalSjukfallPerSexData";
    conf.exportTableUrl = "api/getSjukfallPerSexStatistics/csv";
    conf.title = function (period, enhetsCount) {
        return "Andel sjukfall per kön per län" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.percentChart = true;
    conf.chartXAxisTitle = "Län";
    conf.chartFootnotes = ["info.lan.information"];
    conf.pageHelpText = "alert.lan-andel-sjukfall-per-kon.questionmark";
    return conf;
};

angular.module('StatisticsApp').casesPerBusinessConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerBusinessVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getNumberOfCasesPerEnhet/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per vårdenhet" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Vårdenhet";
    conf.chartFootnotes = ["alert.vardenhet.information"];
    conf.alternativeView = "sjukfallperenhettidsserie";
    return conf;
};

angular.module('StatisticsApp').casesPerLakareConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakareVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getSjukfallPerLakareVerksamhet/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per läkare" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Läkare";
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakaresAlderOchKonVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getCasesPerDoctorAgeAndGenderStatistics/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall baserat på läkares kön och ålder" + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartXAxisTitle = "Läkare";
    conf.pageHelpText = "alert.lakarkon-alder.questionmark";
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakarbefattningVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getNumberOfCasesPerLakarbefattning/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall baserat på läkarbefattning" + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartXAxisTitle = "Läkarbefattning";
    conf.chartFootnotes = ["alert.lakare-befattning.information"];
    return conf;
};

angular.module('StatisticsApp').compareDiagnosis = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getCompareDiagnosisVerksamhet";
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return "api/verksamhet/getJamforDiagnoserStatistik/" + diagnosisHash + "/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Jämförelse av valfria diagnoser" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Diagnos";
    conf.showDiagnosisSelector = true;
    conf.alternativeView = "jamforDiagnoserTidsserie";
    return conf;
};
