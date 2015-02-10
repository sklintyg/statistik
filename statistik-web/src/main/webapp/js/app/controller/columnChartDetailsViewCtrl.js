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

angular.module('StatisticsApp').controller('columnChartDetailsViewCtrl', [ '$scope', '$rootScope', '$routeParams', '$window', '$location', '$timeout', 'statisticsData', 'businessFilter', 'config', 'messageService',

    function ($scope, $rootScope, $routeParams, $window, $location, $timeout, statisticsData, businessFilter, config, messageService) {
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
            if (config.percentChart) {
                chartOptions.tooltip.pointFormat = '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.0f}%</b><br/>';
            }
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
            $scope.subTitle = config.title(result.period, result.filter.enheter ? result.filter.enheter.length : null);
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.doneLoading = true;
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData);

                if ($routeParams.printBw || $routeParams.print) {
                    ControllerCommons.printAndCloseWindow($timeout, $window);
                }
            }, 1);
        };

        function getSelectedDiagnosis() {
            if (!$scope.diagnosisOptionsTree) {
                return null;
            }
            return _.map(businessFilter.getSelectedLeaves($scope.diagnosisOptionsTree), function(it){
                return it.numericalId;
            });
        }

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, $routeParams.diagnosHash);
        }

        if (isVerksamhet) {
            $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId);
            refreshVerksamhet();
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

        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        if ($scope.showDiagnosisSelector) {
            statisticsData.getIcd10Structure(function(diagnosisTree){
                businessFilter.setupDiagnosisTreeForSelectionModal(diagnosisTree);
                $scope.diagnosisOptionsTree = {subs: diagnosisTree};
            }, function () { alert("Failed to fetch ICD10 structure tree from server") });
            $scope.diagnosisSelectorData = {
                titleText:messageService.getProperty("comparediagnoses.lbl.val-av-diagnoser", null, "", null, true),
                buttonLabelText:messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
                firstLevelLabelText:messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
                secondLevelLabelText:messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
                thirdLevelLabelText:messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
            };
        }

        $scope.exportChart = function () {
            ControllerCommons.exportChart(chart, $scope.pageName, $scope.subTitle, $scope.activeDiagnosFilters);
        };

        $scope.print = function (bwPrint) {
            ControllerCommons.print(bwPrint, $rootScope, $window);
        };

        $scope.diagnosisSelected = function () {
            var diagnoses = getSelectedDiagnosis();
            statisticsData.getFilterHash(diagnoses, null, null, function(selectionHash){

                //Ugly fix from http://stackoverflow.com/questions/20827282/cant-dismiss-modal-and-change-page-location
                $('#cancelModal').modal('toggle');
                $('.modal-backdrop').remove();

                $location.path("/verksamhet/" + $routeParams.verksamhetId + "/jamforDiagnoser/" + selectionHash);
            }, function(){ throw new Error("Failed to get filter hash value"); });
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
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthCurrentData/csv";
    };
    conf.title = function (month, enhetsCount) {
        return "Antal pågående sjukfall per sjukskrivningslängd" + ControllerCommons.getEnhetCountText(enhetsCount, false) + month;
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
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per åldersgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
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
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerBusinessVerksamhet/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per vårdenhet" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Vårdenhet";
    conf.chartFootnotes = ["alert.vardenhet.information"];
    return conf;
};

angular.module('StatisticsApp').casesPerLakareConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSjukfallPerLakareVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakareVerksamhet/csv";
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
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakaresAlderOchKonVerksamhet/csv";
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
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getSjukfallPerLakarbefattningVerksamhet/csv";
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
    conf.exportTableUrl = "api/getCompareDiagnosisStatistics/csv";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getCompareDiagnosisStatistics/csv";
    };
    conf.title = function (period, enhetsCount) {
        return "Jämförelse av valfria diagnoser" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "Diagnos";
    conf.showDiagnosisSelector = true;
    return conf;
};
