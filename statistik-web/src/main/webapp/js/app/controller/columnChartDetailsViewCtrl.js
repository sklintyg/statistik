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

        var populateDetailsOptions = function (result) {
            var basePath = isVerksamhet ? "#/verksamhet/diagnosavsnitttvarsnitt" : "#/nationell/diagnosavsnitttvarsnitt";
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        };

        var populatePageWithData = function (result) {
            $scope.subTitle = config.title(result.period, result.filter.enheter ? result.filter.enheter.length : null);
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.resultMessage = result.message;
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnitt(populateDetailsOptions, function () {
                    alert("Kunde inte ladda data");
                });
            }
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
            }, ControllerCommons.getExtraPathParam($routeParams));
        }

        var diagnosHashExists = function diagnosHashExists() {
            return $routeParams.diagnosHash !== "-";
        };

        if (diagnosHashExists()) {
            $scope.spinnerText = "Laddar information...";
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.diagnosHash ? $routeParams.diagnosHash : $routeParams.groupId);
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

        if (isVerksamhet && config.exchangeableViews) {
            //If we have a diagnosisHash then added to the next route before anything else
            if($routeParams.diagnosHash) {
                _.each(config.exchangeableViews, function(view) {
                    view.state = view.state + ControllerCommons.createDiagnosHashPathOrAlternativePath();
                });
            }

            var queryParamsString = ControllerCommons.createQueryStringOfQueryParams($location.search());

            //Add queryParams if any
            if(queryParamsString) {
                _.each(config.exchangeableViews, function (view) {
                    view.state = view.state + "?" + queryParamsString;
                });
            }

            $scope.exchangeableViews = config.exchangeableViews;
        }

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;

        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.popoverText = messageService.getProperty(config.pageHelpText, null, "", null, true);

        $scope.chartFootnotes = _.map(config.chartFootnotes, function(msgKey){
            return messageService.getProperty(msgKey, null, "", null, true);
        });

        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;

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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningslangd', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningslangdTidsserie', active: false}];
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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/aldersgrupper', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/aldersgrupperTidsserie', active: false}];
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
    conf.pageHelpText = "help.age-group-current";
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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperenhet', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperenhettidsserie', active: false}];

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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakare', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaretidsserie', active: false}];
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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakaresalderochkon', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakaresalderochkontidsserie', active: false}];
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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallperlakarbefattning', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallperlakarbefattningtidsserie', active: false}];
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

    conf.exchangeableViews = [
        {description: 'Tvärsnitt', state: '#/verksamhet/jamforDiagnoser', active: true},
        {description: 'Tidsserie', state: '#/verksamhet/jamforDiagnoserTidsserie', active: false}];

    return conf;
};

angular.module('StatisticsApp').casesPerMonthTvarsnittConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getNumberOfCasesPerMonthTvarsnittVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getNumberOfCasesPerMonthTvarsnitt/csv";
    };
    conf.title = function (months, enhetsCount) {
        return "Antal sjukfall per månad" + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.chartXAxisTitle = "";
    conf.pageHelpText = "help.casespermonth";

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallPerManad', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallPerManadTvarsnitt', active: true}];

    return conf;
};


angular.module('StatisticsApp').longSickLeavesTvarsnittConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getLongSickLeavesTvarsnittVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getLongSickLeavesTvarsnitt/csv";
    };
    conf.title = function (months, enhetsCount) {
        return "Antal långa sjukfall - mer än 90 dagar" + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.chartXAxisTitle = "";
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/langasjukskrivningar', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/langasjukskrivningartvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveTvarsnittConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveTvarsnittVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getDegreeOfSickLeaveTvarsnitt/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per sjukskrivningsgrad" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.chartXAxisTitle = "";
    conf.pageHelpText = "help.degreeofsickleave";
    conf.chartFootnotes = ["alert.degreeofsickleave.information"];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukskrivningsgrad', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukskrivningsgradtvarsnitt', active: true}];

    return conf;
};

angular.module('StatisticsApp').diagnosisGroupTvarsnittConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getDiagnosisGroupTvarsnittVerksamhet";
    conf.exportTableUrlVerksamhet = function () {
        return "api/verksamhet/getDiagnosGruppTvarsnitt/csv";
    };
    conf.showDetailsOptions = false;
    conf.title = function (period, enhetsCount) {
        return "Antal sjukfall per diagnosgrupp" + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.diagnosisgroup";
    conf.chartFootnotes = ["alert.diagnosisgroup.information"];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/diagnosgrupp', active: false},
        {description: 'Tvärsnitt', state: '#/verksamhet/diagnosgrupptvarsnitt', active: true}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupTvarsnittConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getSubDiagnosisGroupTvarsnittVerksamhet";
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return "api/verksamhet/getDiagnosavsnittTvarsnitt/" + subgroupId + "/csv";
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.title = function (period, enhetsCount, name) {
        return "Antal sjukfall för " + name + ControllerCommons.getEnhetCountText(enhetsCount, false) + period;
    };
    conf.pageHelpText = "help.diagnosissubgroup";
    conf.chartFootnotes = ["alert.diagnosissubgroup.information"];
    conf.alternativeView = "diagnosavsnitt";
    return conf;
};
