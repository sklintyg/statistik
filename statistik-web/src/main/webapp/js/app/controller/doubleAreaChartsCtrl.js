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

angular.module('StatisticsApp').controller('doubleAreaChartsCtrl', [ '$scope', '$rootScope', '$routeParams', '$window', '$timeout', 'statisticsData', 'config', 'messageService', 'diagnosisTreeFilter', '$location', 'chartFactory', '_', 'pdfFactory',
    function ($scope, $rootScope, $routeParams, $window, $timeout, statisticsData, config, messageService, diagnosisTreeFilter, $location ,chartFactory, _, pdfFactory) {
        var that = this;
        var chart1 = {};
        var chart2 = {};

        var defaultChartType = 'area';
        $scope.activeChartType = defaultChartType;
        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);

        this.paintChart = function (containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries, chartSpacingLeft, doneLoadingCallback, percentChart) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback);

            chartOptions.chart.type = defaultChartType;
            chartOptions.chart.renderTo = containerId;
            chartOptions.plotOptions.area.lineWidth = 1;
            chartOptions.plotOptions.area.lineColor = 'grey';
            chartOptions.legend.enabled = false;
            chartOptions.xAxis.title.text = "Period";
            chartOptions.tooltip.useHTML = true;
            chartOptions.subtitle.text = (config.percentChart ? "Andel " : "Antal ") + yAxisTitle;
            chartOptions.yAxis.labels.formatter = function () {
                return ControllerCommons.makeThousandSeparated(this.value) + (percentChart ? " %" : "");
            };
            chartOptions.plotOptions.series.stacking = percentChart ? 'percent' : 'normal';

            if (percentChart) {
                chartOptions.tooltip.pointFormat = '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.0f} %</b><br/>';
            }

            return new Highcharts.Chart(chartOptions);
        };

        var updatePrintDataTable = function (ajaxResult) {
            var headers = ajaxResult.tableData.headers,
            rows = ajaxResult.tableData.rows,
            printTables = [], currentDataColumn = 1, maxHeadersPerPage = 5;

            var topLevelHeaders = _.rest(headers[0]); //Take all headers but the first, the first is handled separately
            var totalNumberOfPrintPages = Math.ceil(topLevelHeaders.length/maxHeadersPerPage); //Calculate how many print pages we will have

            /*
                This block takes care of everything we need to do for each and every page
                we need to print, effectively splitting every page in its own print table
             */
            _.each(_.range(0, totalNumberOfPrintPages), function() {
                    //For every print page we need to set up an new printTable
                    var printTable = {headers: [], rows: []};

                    //Add headers for first column
                    _.each(headers, function(header, headerIndex) {
                        printTable.headers[headerIndex] = [header[0]];
                    });

                    //Add row name for each row
                    _.each(rows, function(row) {
                        printTable.rows.push({name: row.name, data: []});
                    });

                    //Add the data for the print page
                    //Start by taking a maxHeadersPerPage chunk of all the toplevel headers
                    _.each(_.take(topLevelHeaders, maxHeadersPerPage), function(topLevelHeader) {

                        //Add the top level topLevelHeader
                        printTable.headers[0].push(topLevelHeader);

                        //Add the sub level headers, the colspan of the top level header
                        //decides how many sub level columns we have.
                        _.each(_.range(topLevelHeader.colspan), function() {
                            printTable.headers[1].push(headers[1][currentDataColumn]);

                            //Add the data that goes into the sublevel
                            _.each(rows, function(row, rowindex) {
                                printTable.rows[rowindex].data.push(row.data[currentDataColumn - 1]);
                            });

                            currentDataColumn++;
                        });
                    });

                    //Taking a new chunk of the top level headers for the next iteration
                    topLevelHeaders = _.drop(topLevelHeaders, maxHeadersPerPage);
                    printTables.push(printTable);
                }
            );

            return printTables;
        };

        function updateChartsYAxisMaxValue() {
            var yMax = Math.max(that.chart1.yAxis[0].dataMax, that.chart2.yAxis[0].dataMax);
            that.chart1.yAxis[0].setExtremes(0, yMax + 1);
            that.chart2.yAxis[0].setExtremes(0, yMax + 1);
        }

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            var chartCategories = ajaxResult.femaleChart.categories;

            var chartSeriesFemale = ajaxResult.femaleChart.series;
            chartFactory.addColor(chartSeriesFemale);
            that.chart1 = that.paintChart('chart1', 'sjukfall för kvinnor', 118, chartCategories, chartSeriesFemale, -100, doneLoadingCallback, config.percentChart);

            var chartSeriesMale = ajaxResult.maleChart.series;
            chartFactory.addColor(chartSeriesMale);
            that.chart2 = that.paintChart('chart2', 'sjukfall för män', 97, chartCategories, chartSeriesMale, -80, doneLoadingCallback, config.percentChart);

            updateChartsYAxisMaxValue();

            $scope.series = chartSeriesMale;
        };

        $scope.switchChartType = function (chartType) {
            chartFactory.switchChartType(that.chart1, chartType);
            chartFactory.switchChartType(that.chart2, chartType);
            that.chart1.redraw();
            that.chart2.redraw();

            $scope.activeChartType = chartType;
            updateChartsYAxisMaxValue();
        };

        $scope.showInLegend = function(index) {
            return chartFactory.showInLegend(that.chart1.series, index) && chartFactory.showInLegend(that.chart2.series, index);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser, result.allAvailableDxsSelectedInFilter, result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets);
            $scope.enhetsCount = result.filter.enheter ? result.filter.enheter.length : null;
            $scope.resultMessage = ControllerCommons.getResultMessage(result, messageService);
            $scope.subTitle = config.title(result.period, $scope.enhetsCount, $routeParams.kapitelId);
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnittAndKategori(populateDetailsOptions, function () {
                    alert("Kunde inte ladda data");
                });
            }

            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result, function() { $scope.doneLoading = true; });
                $timeout(function () {
                    $rootScope.$broadcast('pageDataPopulated');
                });
            }, 1);
            $timeout(function () {
                $scope.printDataTables = updatePrintDataTable(result);
            }, 100);
        };

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? "#/verksamhet/diagnosavsnitt" : "#/nationell/diagnosavsnitt";
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        };

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, "", null, true);
        });
        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, "", null, true);
        $scope.showDetailOptions3PopoverText = messageService.getProperty(config.pageHelpTextShowDetailOptions, null, "", null, true);

        $scope.chartContainers = [
            {id: "chart1", name: "diagram för kvinnor"},
            {id: "chart2", name: "diagram för män"}
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


        var isExistingDiagnosisHashValid = $routeParams.diagnosHash !== "-";
        if (isExistingDiagnosisHashValid) {
            $scope.spinnerText = "Laddar information...";
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
    conf.showDetailsOptions3 = true;
    conf.title = function (period, enhetsCount, name) {
        return "Antal sjukfall för " + name + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.diagnosissubgroup";
    conf.pageHelpTextShowDetailOptions = "help.diagnosissubgroup.showdetailoptions";
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

angular.module('StatisticsApp').differentieratIntygandeConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getDifferentieratIntygandeVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getDifferentieratIntygandeStatistics/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Andel sjukfall för differentierat intygande" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.differentieratintygande";

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/differentieratintygande', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/differentieratintygandetvarsnitt', active: false}];

    conf.percentChart = true;

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
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperenhettidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperenhet', active: false}];

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
        {description: 'Tidsserie', state: '#/verksamhet/jamforDiagnoserTidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/jamforDiagnoser', active: false}];
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
        {description: 'Tidsserie', state: '#/verksamhet/aldersgrupperTidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/aldersgrupper', active: false}];
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
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningslangdTidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningslangd', active: false}];
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
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakarbefattningtidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakarbefattning', active: false}];
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
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaretidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakare', active: false}];
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
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaresalderochkontidsserie', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakaresalderochkon', active: false}];
    return conf;
};
