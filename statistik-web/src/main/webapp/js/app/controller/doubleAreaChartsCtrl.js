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

angular.module('StatisticsApp').controller('doubleAreaChartsCtrl', [ '$scope', '$rootScope', '$routeParams', '$window', '$timeout', 'statisticsData', 'businessFilter', 'config', 'messageService', 'printFactory', 'diagnosisTreeFilter', '$location',
    function ($scope, $rootScope, $routeParams, $window, $timeout, statisticsData, businessFilter, config, messageService, printFactory, diagnosisTreeFilter, $location) {
        var that = this;
        var chart1 = {};
        var chart2 = {};
        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);

        this.paintChart = function (containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries, chartSpacingLeft, doneLoadingCallback) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback);

            chartOptions.chart.type = 'area';
            chartOptions.chart.marginTop = 27;
            chartOptions.chart.spacingLeft = chartSpacingLeft;
            chartOptions.plotOptions.area.lineWidth = 1;
            chartOptions.plotOptions.area.lineColor = 'grey';
            chartOptions.chart.renderTo = containerId;
            chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
            chartOptions.xAxis.title.text = "Period";
            chartOptions.tooltip.useHTML = true;
            chartOptions.yAxis.title.text = yAxisTitle;
            chartOptions.yAxis.title.x = yAxisTitleXPos;
            chartOptions.yAxis.title.y = -13;
            chartOptions.yAxis.title.align = 'high';
            chartOptions.yAxis.title.offset = 0;
            return new Highcharts.Chart(chartOptions);
        };

        //Expects the table to consist of two headers where the first header has a colspan of two
        var updatePrintDataTable = function ($scope, ajaxResult) {
            var headers = ajaxResult.tableData.headers;
            var rows = ajaxResult.tableData.rows;
            var printTables = [];
            var totWidth = 0;
            var maxWidth = 500;
            var colWidth = 50;
            var currentDataColumn = 1;

            for (var c = 1; c < headers[0].length - 2;) {
                var printTable = {};
                printTable.headers = [];
                printTable.rows = [];

                totWidth = colWidth * 2; //total width of table frame (first and last column which should be used on all print tables)

                //Add headers for first column
                for (var i = 0; i < headers.length; i++) {
                    printTable.headers[i] = [];
                    printTable.headers[i].push(headers[i][0]);
                }

                //Add all row names (first column)
                for (var r = 0; r < rows.length; r++) {
                    var row = {};
                    row.name = rows[r].name;
                    row.data = [];
                    printTable.rows.push(row);
                }

                //Add columns until table is too wide or all columns has been added
                for (var i = 0; (c < headers[0].length - 2) && ((totWidth + colWidth) < maxWidth); i++) {
                    printTable.headers[0].push(headers[0][c]);
                    for (var s = 0; s < 2; s++) {
                        printTable.headers[1].push(headers[1][currentDataColumn]);
                        for (var r = 0; r < rows.length; r++) {
                            printTable.rows[r].data.push(rows[r].data[currentDataColumn]);
                        }
                        currentDataColumn++;
                    }
                    c++;
                    totWidth += colWidth;
                }

                //Add headers for last column (sum)
                for (var i = 0; i < headers.length; i++) {
                    printTable.headers[i].push(headers[i][headers[i].length - 1]);
                }

                //Add all row sum (last column)
                for (var r = 0; r < rows.length; r++) {
                    printTable.rows[r].data.push(rows[r].data[rows[r].data.length - 1]);
                }

                printTables.push(printTable);
            }
            $scope.printDataTables = printTables;
        };

        function updateChartsYAxisMaxValue() {
            var yMax = Math.max(that.chart1.yAxis[0].dataMax, that.chart2.yAxis[0].dataMax);
            that.chart1.yAxis[0].setExtremes(0, yMax);
            that.chart2.yAxis[0].setExtremes(0, yMax);
        }

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            var chartCategories = ajaxResult.femaleChart.categories;

            var chartSeriesFemale = ajaxResult.femaleChart.series;
            printFactory.setupSeriesForDisplayType($routeParams.printBw, chartSeriesFemale, "area");
            that.chart1 = that.paintChart('chart1', 'Antal sjukfall för kvinnor', 118, chartCategories, chartSeriesFemale, -100, doneLoadingCallback);

            var chartSeriesMale = ajaxResult.maleChart.series;
            printFactory.setupSeriesForDisplayType($routeParams.printBw, chartSeriesMale, "area");
            that.chart2 = that.paintChart('chart2', 'Antal sjukfall för män', 97, chartCategories, chartSeriesMale, -80, doneLoadingCallback);

            updateChartsYAxisMaxValue();

            $scope.series = chartSeriesMale;
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.switchChartType(that.chart1.series, chartType);
            ControllerCommons.switchChartType(that.chart2.series, chartType);
            that.chart1.redraw();
            that.chart2.redraw();
        };

        $scope.showInLegend = function(index) {
            return ControllerCommons.showInLegend(that.chart1.series, index) && ControllerCommons.showInLegend(that.chart2.series, index);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.enhetsCount = result.filter.enheter ? result.filter.enheter.length : null;
            $scope.resultMessage = result.message;
            $scope.subTitle = config.title(result.period, $scope.enhetsCount, $routeParams.groupId);
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnitt(populateDetailsOptions, function () {
                    alert("Kunde inte ladda data");
                });
            }

            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result, function() { $scope.doneLoading = true; });

                if ($routeParams.printBw || $routeParams.print) {
                    printFactory.printAndCloseWindow($timeout, $window);
                }
            }, 1);
            $timeout(function () {
                updatePrintDataTable($scope, result);
            }, 100);
        };

        var getMapKeys = function(mapObject) {
            var keys = [];
            for (var key in mapObject) {
                if (mapObject.hasOwnProperty(key)) {
                    keys.push(key);
                }
            }
            return keys;
        };

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? "#/verksamhet/diagnosavsnitt" : "#/nationell/diagnosavsnitt";
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        };

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, "", null, true);
        });
        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, "", null, true);

        $scope.chartContainers = [
            {id: "chart1", name: "diagram för kvinnor"},
            {id: "chart2", name: "diagram för män"}
        ];

        $scope.toggleSeriesVisibility = function (index) {
            var s1 = that.chart1.series[index];
            var s2 = that.chart2.series[index];
            if (s1.visible) {
                s1.hide();
                s2.hide();
            } else {
                s1.show();
                s2.show();
            }
            updateChartsYAxisMaxValue();
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, ControllerCommons.getExtraPathParam($routeParams));
        }


        var isExistingDiagnosisHashValid = $routeParams.diagnosHash !== "-";
        if (isExistingDiagnosisHashValid) {
            $scope.spinnerText = "Laddar information...";
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet(ControllerCommons.getExtraPathParam($routeParams));
                refreshVerksamhet();
            } else {
                $scope.exportTableUrl = config.exportTableUrl($routeParams.groupId);
                statisticsData[config.dataFetcher](populatePageWithData, function () {
                    $scope.dataLoadingError = true;
                }, ControllerCommons.getMostSpecificGroupId($routeParams));
            }
        } else {
            $scope.doneLoading = true;
        }

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        ControllerCommons.updateExchangeableViewsUrl(isVerksamhet, config, $location, $scope, $routeParams);

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        if ($scope.showDiagnosisSelector) {
            ControllerCommons.setupDiagnosisSelector(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location);
        }

        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;

        $scope.useSpecialPrintTable = true;

        $scope.exportChart = function (chartName) {
            ControllerCommons.exportChart(that[chartName], $scope.pageName, $scope.subTitle, $scope.activeDiagnosFilters, 'vertical');
        };

        $scope.print = function (bwPrint) {
            printFactory.print(bwPrint, $rootScope, $window);
        };

        $scope.$on('$destroy', function() {
            if(typeof chart1.destroy === 'function') {
                chart1.destroy();
            }

            if(typeof chart2.destroy === 'function') {
                chart2.destroy();
            }
        });

        return this;

    }
]);

angular.module('StatisticsApp').diagnosisGroupConfig = function () {
    var conf = {};
    conf.dataFetcher = "getDiagnosisGroupData";
    conf.dataFetcherVerksamhet = "getDiagnosisGroupDataVerksamhet";
    conf.exportTableUrl = function () {
        return "api/getDiagnoskapitelstatistik/csv";
    };
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getDiagnoskapitelstatistik/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per diagnosgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.diagnosisgroup";
    conf.chartFootnotes = ["alert.diagnosisgroup.information"];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/diagnosgrupp', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/diagnosgrupptvarsnitt', active: false}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupConfig = function () {
    var conf = {};
    conf.dataFetcher = "getSubDiagnosisGroupData";
    conf.dataFetcherVerksamhet = "getSubDiagnosisGroupDataVerksamhet";
    conf.exportTableUrl = function (subgroupId) {
        return "api/getDiagnosavsnittstatistik/" + subgroupId + "/csv";
    };
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return "api/verksamhet/getDiagnosavsnittstatistik/" + subgroupId + "/csv";
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.title = function (period, enhetsCount, name) {
        return "Antal sjukfall för " + name + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.diagnosissubgroup";
    conf.chartFootnotes = ["alert.diagnosissubgroup.information"];
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/diagnosavsnitt', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/diagnosavsnitttvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveConfig = function () {
    var conf = {};
    conf.dataFetcher = "getDegreeOfSickLeave";
    conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveVerksamhet";
    conf.exportTableUrl = function () {
        return "api/getDegreeOfSickLeaveStatistics/csv";
    };
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getDegreeOfSickLeaveStatistics/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per sjukskrivningsgrad" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.degreeofsickleave";
    conf.chartFootnotes = ["alert.degreeofsickleave.information"];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningsgrad', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningsgradtvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').casesPerBusinessTimeSeriesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerBusinessTimeSeriesVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getNumberOfCasesPerEnhetTimeSeries/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per vårdenhet" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartFootnotes = ["alert.vardenhet.information"];

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperenhet', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperenhettidsserie', active: true}];

    return conf;
};

angular.module('StatisticsApp').compareDiagnosisTimeSeriesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getCompareDiagnosisTimeSeriesVerksamhet";
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return "api/verksamhet/getJamforDiagnoserStatistikTidsserie/" + diagnosisHash + "/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Jämförelse av valfria diagnoser" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.showDiagnosisSelector = true;

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/jamforDiagnoser', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/jamforDiagnoserTidsserie', active: true}];
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupTimeSeriesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getAgeGroupsTimeSeriesVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getAgeGroupsStatisticsAsTimeSeries/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per åldersgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/aldersgrupper', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/aldersgrupperTidsserie', active: true}];
    return conf;
};

angular.module('StatisticsApp').sickLeaveLengthTimeSeriesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSickLeaveLengthTimeSeriesDataVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getSickLeaveLengthTimeSeries/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per sjukskrivningslängd" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartFootnotes = ["info.sickleavelength"];
    conf.pageHelpText = "help.sickleavelength";

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningslangd', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningslangdTidsserie', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningTidsserieConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakarbefattningTidsserieVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getNumberOfCasesPerLakarbefattningSomTidsserie/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall baserat på läkarbefattning" + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.chartFootnotes = ["alert.lakare-befattning.information"];
    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakarbefattning', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakarbefattningtidsserie', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakareTimeSeriesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakareSomTidsserieVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getSjukfallPerLakareSomTidsserie/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per läkare" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakare', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaretidsserie', active: true}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonTidsserieConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakaresAlderOchKonTidsserieVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall baserat på läkares kön och ålder" + ControllerCommons.getEnhetCountText(enhetsCount, true) + period;
    };
    conf.pageHelpText = "alert.lakarkon-alder.questionmark";
    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakaresalderochkon', active: false},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaresalderochkontidsserie', active: true}];
    return conf;
};
