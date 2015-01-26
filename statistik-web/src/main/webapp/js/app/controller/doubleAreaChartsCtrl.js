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

angular.module('StatisticsApp').controller('doubleAreaChartsCtrl', [ '$scope', '$routeParams', '$window', '$timeout', 'statisticsData', 'businessFilter', 'config', 'messageService',
    function ($scope, $routeParams, $window, $timeout, statisticsData, businessFilter, config, messageService) {
        var that = this;
        var chart1 = {};
        var chart2 = {};
        var isVerksamhet = $routeParams.verksamhetId ? true : false;

        this.paintChart = function (containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries, chartSpacingLeft) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
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

                var totWidth = colWidth * 2; //total width of table frame (first and last column which should be used on all print tables)

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

        var updateChart = function (ajaxResult) {
            var chartCategories = ajaxResult.femaleChart.categories;

            var chartSeriesFemale = ajaxResult.femaleChart.series;
            ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, chartSeriesFemale, "area");
            that.chart1 = that.paintChart('chart1', 'Antal sjukfall för kvinnor', 118, chartCategories, chartSeriesFemale, -100);

            var chartSeriesMale = ajaxResult.maleChart.series;
            ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, chartSeriesMale, "area");
            that.chart2 = that.paintChart('chart2', 'Antal sjukfall för män', 97, chartCategories, chartSeriesMale, -80);

            updateChartsYAxisMaxValue();

            $scope.series = chartSeriesMale;
        };

        var populatePageWithData = function (result) {
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.doneLoading = true;
            $scope.enhetsCount = result.filter.enheter ? result.filter.enheter.length : null;
            $scope.subTitle = config.title(result.period, $scope.enhetsCount, $routeParams.groupId);
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnitt(populateDetailsOptions, function () {
                    alert("Kunde inte ladda data");
                });
            }

            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result);

                if ($routeParams.printBw || $routeParams.print) {
                    ControllerCommons.printAndCloseWindow($timeout, $window);
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

        function getSubtitle(period, selectedOption1, selectedOption2) {
            if ((selectedOption2 && selectedOption2.name && selectedOption2.id)) {
                return config.title(period, $scope.enhetsCount, selectedOption2.id + " " + selectedOption2.name);
            }
            if (selectedOption1 && selectedOption1.name && selectedOption1.id) {
                return config.title(period, $scope.enhetsCount, selectedOption1.id + " " + selectedOption1.name);
            }
            return "";
        }

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? "#/verksamhet/" + $routeParams.verksamhetId + "/diagnosavsnitt" : "#/nationell/diagnosavsnitt";

            var kapitels = result.kapitels;
            for (var i = 0; i < kapitels.length; i++) {
                if (kapitels[i].id == $routeParams.groupId) {
                    $scope.selectedDetailsOption = kapitels[i];
                    break;
                }
            }
            var avsnitts = result.avsnitts[$routeParams.groupId];
            for (var i = 0; i < avsnitts.length; i++) {
                if (avsnitts[i].id == $routeParams.kategoriId) {
                    $scope.selectedDetailsOption2 = avsnitts[i];
                    break;
                }
            }
            $scope.subTitle = getSubtitle($scope.currentPeriod, $scope.selectedDetailsOption, $scope.selectedDetailsOption2);

            $scope.detailsOptions = _.map(kapitels, function (e) {
                e.url = basePath + "/" + e.id;
                return e;
            });
            $scope.detailsOptions2 = _.map(avsnitts, function (e) {
                e.url = basePath + "/" + $routeParams.groupId + "/kategori/" + e.id;
                return e;
            });

            //Add default option for detailsOptions2
            var defaultId = messageService.getProperty("lbl.valj-annat-diagnosavsnitt", null, "", null, true);
            $scope.detailsOptions2.unshift({"id": defaultId, "name":"", "url":basePath + "/" + $routeParams.groupId});
            if (!$scope.selectedDetailsOption2) {
                $scope.selectedDetailsOption2 = $scope.detailsOptions2[0];
            }
        };

        $scope.chartFootnotes = config.chartFootnotes;

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

        $scope.popoverText = config.tooltipHelpText;

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, getMostSpecificGroupId());
        }

        function getMostSpecificGroupId() {
            return $routeParams.kategoriId ? $routeParams.kategoriId : $routeParams.groupId;
        }

        if (isVerksamhet) {
            $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId, $routeParams.groupId);
            refreshVerksamhet();
        } else {
            $scope.exportTableUrl = config.exportTableUrl($routeParams.groupId);
            statisticsData[config.dataFetcher](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, getMostSpecificGroupId());
        }

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;

        $scope.spinnerText = "Laddar information...";
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;

        $scope.useSpecialPrintTable = true;

        $scope.exportChart = function (chartName) {
            ControllerCommons.exportChart(that[chartName], $scope.pageName, $scope.subTitle, $scope.activeDiagnosFilters, 'vertical');
        };

        $scope.print = function (bwPrint) {
            window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
        };

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
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getDiagnoskapitelstatistik/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per diagnosgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.tooltipHelpText = "Diagnoskoder används för att gruppera sjukdomar för att kunna göra översiktliga statistiska sammanställningar och analyser. Statistiktjänsten är uppdelad i sju övergripande diagnosgrupper. I varje grupp ingår olika kapitel med diagnoskoder. Diagnoskoderna finns i klassificeringssystemet ICD-10-SE.";
    conf.chartFootnotes = ["När ett sjukfall har flera intyg under samma månad hämtas uppgift om diagnos från det senaste intyget. För ett sjukfall som varar flera månader så hämtas diagnos för varje månad. I tabellen visas statistiken på diagnoskapitelnivå, men i grafen är statistiken aggregerad för att underlätta presentationen."];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupConfig = function () {
    var conf = {};
    conf.dataFetcher = "getSubDiagnosisGroupData";
    conf.dataFetcherVerksamhet = "getSubDiagnosisGroupDataVerksamhet";
    conf.exportTableUrl = function (subgroupId) {
        return "api/getDiagnosavsnittstatistik/" + subgroupId + "/csv";
    };
    conf.exportTableUrlVerksamhet = function (verksamhetId, subgroupId) {
        return "api/verksamhet/" + verksamhetId + "/getDiagnosavsnittstatistik/" + subgroupId + "/csv";
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.title = function (period, enhetsCount, name) {
        return "Antal sjukfall för " + name + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.tooltipHelpText = "Ett diagnoskapitel innehåller flera avsnitt med sjukdomar som i sin tur omfattar olika diagnoskoder. Det finns totalt 21 diagnoskapitel. Grafen visar endast de sex vanligaste förekommande avsnitten eller diagnoserna uppdelade på kvinnor respektive män. I tabellen visas samtliga inom valt kapitel eller avsnitt.";
    conf.chartFootnotes = ["När ett sjukfall har flera intyg under samma månad hämtas uppgift om diagnos från det senaste intyget. För ett sjukfall som varar flera månader så hämtas diagnos för varje månad."];
    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveConfig = function () {
    var conf = {};
    conf.dataFetcher = "getDegreeOfSickLeave";
    conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveVerksamhet";
    conf.exportTableUrl = function () {
        return "api/getDegreeOfSickLeaveStatistics/csv";
    };
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getDegreeOfSickLeaveStatistics/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per sjukskrivningsgrad" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.tooltipHelpText = "Sjukskrivningsgrad visar hur stor del av patientens arbetsförmåga som är nedsatt. Sjukskrivningsgraden anges i procent i förhållande till patientens aktuella arbetstid.";
    conf.chartFootnotes = ["När ett sjukfall har flera intyg under samma månad hämtas uppgift om sjukskrivningsgrad från det senaste intyget. Om detta intyg innehåller flera olika sjukskrivningsgrader hämtas den senaste sjukskrivningsgraden för den månaden. För ett sjukfall som varar flera månader så hämtas sjukskrivningsgrad för varje månad."];
    return conf;
};
