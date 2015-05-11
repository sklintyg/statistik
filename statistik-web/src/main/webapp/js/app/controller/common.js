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

var ControllerCommons = new function(){

    this.updateDataTable = function (scope, tableData) {
        scope.headerrows = tableData.headers;
        if (scope.headerrows.length > 1) {
            scope.headerrows[0].centerAlign = true;
        }
        scope.rows = tableData.rows;
    };

    this.htmlsafe = function(string) {
        return string.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    };

    this.isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    };

    this.makeThousandSeparated = function(input) {
        return ControllerCommons.isNumber(input) ? input.toString().split('').reverse().join('').match(/.{1,3}/g).join('\u00A0').split('').reverse().join('') : input;
    };

    this.getFileName = function(chartName) {
        var d = new Date();

        var year = "" + d.getFullYear();
        var month = d.getMonth() < 9 ? "0" + (d.getMonth() + 1) : "" + (d.getMonth() + 1);
        var day = d.getDate() < 10 ? "0" + d.getDate() : "" + d.getDate();
        var date = year + month + day;

        var hour = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        var minute = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        var second = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();
        var time = hour + minute + second;

        return String(chartName).replace(/\s+/g, "_") + "_" + date + "_" + time;
    };

    this.exportChart = function(chart, chartName, title, diagnosFilters, legendLayout) {
        var options = {filename: ControllerCommons.getFileName(chartName)};
        var extendedChartOptions = { legend: { enabled: true } };
        var chartHeight = 400;
        extendedChartOptions.chart = {};
        extendedChartOptions.chart.height = chartHeight;
        extendedChartOptions.chart.width = 600;
        if (legendLayout) {
            extendedChartOptions.legend.layout = legendLayout;
        }
        if (title) {
            extendedChartOptions.title = {
                text: title,
                margin: 30
            };
            extendedChartOptions.subtitle = {
                text: " "
            };
            extendedChartOptions.chart.marginTop = null;
            extendedChartOptions.chart.backgroundColor = "#FFFFFF";
            extendedChartOptions.chart.spacingLeft = 0;
        }
        extendedChartOptions.subtitle = {
            text: " "
        };
        if (diagnosFilters) {
            var fontSize = 12;
            var fontSizeHeader = 15;
            extendedChartOptions.chart.spacingBottom = (diagnosFilters.length + 2) * fontSize + fontSizeHeader + diagnosFilters.length * 2;
            extendedChartOptions.chart.height = chartHeight + extendedChartOptions.chart.spacingBottom;
            extendedChartOptions.chart.events = {
                load: function () {
                    this.renderer.text('Sammanställning av diagnosfilter', 10, chartHeight + fontSize / 2 + fontSizeHeader)
                        .css({
                            color: '#008391',
                            fontSize: fontSizeHeader + 'px'
                        })
                        .add();
                    var arrayLength = diagnosFilters.length;
                    for (var i = 0; i < arrayLength; i++) {
                        this.renderer.text(diagnosFilters[i], 10, chartHeight + (2 + i) * fontSize + fontSizeHeader + i * 2)
                            .css({
                                color: '#008391',
                                fontSize: fontSize + 'px'
                            })
                            .add();
                    }
                }
            };
        }
        chart.exportChart(options, extendedChartOptions);
    };

    this.getHighChartConfigBase = function(chartCategories, chartSeries, doneLoadingCallback) {
        return {
            chart : {
                renderTo : 'chart1',
                backgroundColor : null, //transparent
                plotBorderWidth: 1,
                events: { load: doneLoadingCallback }
            },
            title : {
                text : ''
            },
            legend : {
                align : 'top left',
                x : 120,
                y : 0,
                borderWidth : 0
            },
            xAxis : {
                labels : {
                    rotation : 320,
                    align : 'right',
                    style: {
                    	whiteSpace: 'pre',
                    	width: '200px'
                    },
                    formatter: function() {
                        //If the label is more than 30 characters then cut the text and add ellipsis
                        return this.value.length > 30 ? this.value.substring(0,30) + "..." : this.value;
                    }
                },
                categories : _.map(chartCategories, function(name) {
                    return ControllerCommons.htmlsafe(name);
                }),
                title: {
                	align: 'high',
                	style: {
                    	color: '#008391'
                    }
                }
            },
            yAxis : {
                allowDecimals : false,
                min : 0,
                title : {
                    text : 'Antal sjukfall',
                    align : 'high',
                    verticalAlign : 'top',
                    rotation : 0,
                    floating : true,
                    x : -10,
                    y : 5,
                    style: {
                    	color: '#008391'
                    }
                },
                labels : {
                    formatter : function() {
                        return ControllerCommons.makeThousandSeparated(this.value);
                    }
                },
                plotLines : [ {
                    value : 0,
                    width : 1,
                    color : '#808080'
                } ]
            },
            exporting : {
                enabled : false,
                url: highchartsExportUrl
            },
            plotOptions : {
                line : {
                    allowPointSelect : false,
                    marker : {
                        enabled : false,
                        symbol : 'circle'
                    },
                    cursor : 'pointer',
                    dataLabels : {
                        enabled : false
                    },
                    events : {
                        legendItemClick : function() { // This function removes interaction for plot and legend-items
                            return false;
                        }
                    },
                    showInLegend : true
                },
                column : {
                    stacking : 'normal'
                },
                area : {
                    stacking : 'normal',
                    lineColor : '#666666',
                    lineWidth : 1,
                    marker : {
                        enabled : false,
                        symbol : 'circle'
                    }
                },
                pie : {
                    cursor : 'pointer',
                    dataLabels : {
                        enabled : false
                    },
                    showInLegend : false
                }
            },
            tooltip : {
                backgroundColor : '#fff',
                borderWidth : 2,
                style: {
                	color: '#333333',
                	fontSize: '12px',
                	padding: '8px'
                }
            },
            credits : {
                enabled : false
            },
            series : chartSeries
        };
    };

    this.getEnhetCountText = function(enhetsCount, basedOnAlreadyInText) {
        var singleEnhet = enhetsCount === 1;
        if (basedOnAlreadyInText) {
            return enhetsCount ? " och " + enhetsCount + " enhet" + (singleEnhet ? "" : "er") + " " : " ";
        }
        return enhetsCount ? " baserat på " + enhetsCount + " enhet" + (singleEnhet ? "" : "er") + " " : " ";
    };

    function icdStructureAsArray(icdStructure) {
        return _.map(icdStructure, function (icd) {
            return icdStructureAsArray(icd.subItems).concat(icd);
        });
    }

    this.getDiagnosFilterInformationText = function(diagnosFilterIds, icdStructure) {
        var icdStructureAsFlatArray = _.compose(_.flatten, icdStructureAsArray)(icdStructure);
        return _.map(diagnosFilterIds, function(diagnosId){
            var icdItem = _.find(icdStructureAsFlatArray, function(icd){
                return icd.numericalId === parseInt(diagnosId, 10);
            });
            return icdItem.id + " " + icdItem.name;
        });
    };

    this.populateActiveDiagnosFilter = function(scope, statisticsData, diagnosIds, isPrint) {
        if (!diagnosIds) {
            return;
        }
        statisticsData.getIcd10Structure(function (diagnoses) {
            scope.activeDiagnosFilters = diagnoses ? ControllerCommons.getDiagnosFilterInformationText(diagnosIds, diagnoses) : null;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        }, function () {
            scope.activeDiagnosFilters = diagnosIds;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        });
    };

    this.setupDiagnosisSelector = function(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location) {
        //Initiate the diagnosisTree if we need to.
        diagnosisTreeFilter.setup($routeParams);
        $scope.diagnosisTreeFilter = diagnosisTreeFilter;

        $scope.diagnosisSelectorData = {
            titleText: messageService.getProperty("comparediagnoses.lbl.val-av-diagnoser", null, "", null, true),
            buttonLabelText: messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
            firstLevelLabelText: messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
            secondLevelLabelText: messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
            thirdLevelLabelText: messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
        };

        $scope.diagnosisSelected = function () {
            ControllerCommons.diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location);
        };
    };

    this.diagnosisToCompareSelected = function(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location) {
        var diagnoses = diagnosisTreeFilter.getSelectedDiagnosis();

        $timeout(function () {
            //Ugly fix from http://stackoverflow.com/questions/20827282/cant-dismiss-modal-and-change-page-location
            $('#cancelModal').modal('hide');
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open');
        }, 1);

        $timeout(function () {
            $scope.doneLoading = false;
        }, 1);

        var params = {
            diagnoser: diagnoses
        };

        var success = function (selectionHash) {
            var path = $location.path();
            var newPath = path.replace(/\/[^\/]+$/gm, "/" + selectionHash);
            $location.path(newPath);
        };

        var error = function () {
            throw new Error("Failed to get filter hash value");
        };

        statisticsData.getFilterHash(params).then(success, error);
    };

    this.isShowingVerksamhet = function($location) {
        return $location.path().indexOf("/verksamhet/") === 0;
    };

    this.createQueryStringOfQueryParams = function (queryParams) {
        return !_.isEmpty(queryParams) ? _.map(queryParams, function (value, key) {
            return key + "=" + value;
        }).join('&') : '';
    };

    this.getExtraPathParam = function(routeParams) {
        return routeParams.diagnosHash ? routeParams.diagnosHash : ControllerCommons.getMostSpecificGroupId(routeParams);
    };

    this.getMostSpecificGroupId = function(routeParams) {
        return routeParams.kategoriId ? routeParams.kategoriId : routeParams.groupId;
    };

    this.populateDetailsOptions = function (result, basePath, $scope, $routeParams, messageService, config) {
        var kapitels = result.kapitels;
        for (var i = 0; i < kapitels.length; i++) {
            if (kapitels[i].id === $routeParams.groupId) {
                $scope.selectedDetailsOption = kapitels[i];
                break;
            }
        }
        var avsnitts = result.avsnitts[$routeParams.groupId];
        for (var i = 0; i < avsnitts.length; i++) {
            if (avsnitts[i].id === $routeParams.kategoriId) {
                $scope.selectedDetailsOption2 = avsnitts[i];
                break;
            }
        }

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

        $scope.subTitle = getSubtitle($scope.currentPeriod, $scope.selectedDetailsOption, $scope.selectedDetailsOption2, $scope, config);
    };

    function getSubtitle(period, selectedOption1, selectedOption2, $scope, config) {
        if ((selectedOption2 && selectedOption2.name && selectedOption2.id)) {
            return config.title(period, $scope.enhetsCount, selectedOption2.id + " " + selectedOption2.name);
        }
        if (selectedOption1 && selectedOption1.name && selectedOption1.id) {
            return config.title(period, $scope.enhetsCount, selectedOption1.id + " " + selectedOption1.name);
        }
        return "";
    }

    this.createDiagnosHashPathOrAlternativePath = function($routeParams){
        return ($routeParams.diagnosHash ? "/" + $routeParams.diagnosHash : ($routeParams.groupId ? "/" + $routeParams.groupId + ($routeParams.kategoriId ? "/kategori/" + $routeParams.kategoriId : "") : ""));
    };

    this.updateExchangeableViewsUrl = function(isVerksamhet, config, $location, $scope, $routeParams) {
        if (isVerksamhet && config.exchangeableViews) {
            //If we have a diagnosisHash then added to the next route before anything else
            _.each(config.exchangeableViews, function (view) {
                view.state = view.state + ControllerCommons.createDiagnosHashPathOrAlternativePath($routeParams);
            });
            var queryParamsString = ControllerCommons.createQueryStringOfQueryParams($location.search());
            //Add queryParams if any
            if (queryParamsString) {
                _.each(config.exchangeableViews, function (view) {
                    view.state = view.state + "?" + queryParamsString;
                });
            }
            $scope.exchangeableViews = config.exchangeableViews;
        }
    };

    /* Configure all existing series of a specific chart for a new chart type.
     */
    this.switchChartType = function (chartSeries, chartType, config) {

        config = config || {
            type: chartType,
            stack: chartType === 'area'? 'stacked' : null,
            stacking: chartType === 'area'? 'normal' : null
        };

        var hasSexSet = isSexSetOnChartSeries(chartSeries);

        //This updates the chart object with new options
        _.each(chartSeries, function (series) {

            //If the sex property is available on the series object, then there is a series with sex === null that is a total series.
            //We want this sereis to be hidden when the chart type is === area
            if(hasSexSet) {
                showOrHideTotalSeries(chartType, series, config);
            }

            //If the series only have one data point, then we enable the marker to make it visible in the gui
            config.marker = series.data.length === 1 ? {enabled: true} : {enabled: false};

            series.update(config, false);
        });
    };

    /* This is just a very akward way of telling the chart not to display
    a series with totals when the chart type is area. If the area chart uses stack and stacking the total will add to the other series
    and the numbers will accumulate which isn't correct when show in the chart*/
    var showOrHideTotalSeries = function showOrHideTotalSeries(chartType, series, config) {
        if(chartType === "area" && series.options.sex === null) {
            //Mark legend that it wont be shown.
            // We don't actually use highcharts legends so this won't update the chart legends by itself
            config.showInLegend = false;

            series.hide();
        } else if (chartType !== "area" && series.options.sex === null) {
            config.showInLegend = true;
            series.show();
        } else {
            config.showInLegend = true;
        }
    };

    var isSexSetOnChartSeries = function isSexSetOnChartSeries(chartSeries) {
        var maleSeries = _.find(chartSeries, function(series) {
            return series.options.sex === 'Male';
        });

        var femaleSeries = _.find(chartSeries, function(series) {
            return series.options.sex === 'Female';
        });

        return maleSeries && femaleSeries? true: false;
    };

    this.showInLegend = function(series, index) {
        return series[index].options.showInLegend;
    };

    this.enableMarkerForSeriesWithOneDataPoint = function(series) {
        if (series) {
            _.each(series, function (s) {
                //If we only have one data point
                if (s.data.length === 1) {
                    if (s.marker) {
                        s.marker.enabled = true;
                    } else {
                        s.marker = {enabled: true};
                    }
                }
            });
        }
    };


};


