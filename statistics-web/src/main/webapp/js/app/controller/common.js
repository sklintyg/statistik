'use strict';

var ControllerCommons = new function(){

    this.addColor = function(rawData) {
        var color = [ "#fbb10c", "#2ca2c6", "#B0B0B0", "#12BC3A", "#9c734d", "#D35ABB", "#4A4A4A" ];
        var maleColor = [ "#008391", "#00CDE3" ]
        var femaleColor = [ "#EA8025", "#FFC18C" ]
        var colorSelector = 0;
        var maleColorSelector = 0;
        var femaleColorSelector = 0;
        for (var i = 0; i < rawData.length; i++) {
            if (rawData[i].sex === "Male") {
                rawData[i].color = maleColor[maleColorSelector++];
            } else if (rawData[i].sex === "Female") {
                rawData[i].color = femaleColor[femaleColorSelector++];
            } else {
                rawData[i].color = color[colorSelector++];
            }
        }
        return rawData;
    };
 
    this.showHideDataTableDefault = "Dölj datatabell";
    this.toggleTableVisibilityGeneric = function(event, $scope) {
        var elem = $(event.target);
        var accordionGroup = $(elem.parents('.accordion-group')[0]);
        var accordionBody = $(accordionGroup.children('.accordion-body'));
        var wasTableVisible = accordionBody.hasClass("in");
        $scope.showHideDataTable = wasTableVisible ? "Visa datatabell" : "Dölj datatabell";
    };
    
    this.exportTableDataGeneric = function() {
        var dt = $('#datatable');
        var csvData = table2CSV(dt);
        $.generateFile({
            filename : 'export.csv',
            content : csvData,
            script : 'fileDownload.jsp'
        });
    };

    this.htmlsafe = function(string) {
        return string.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    }
    
    this.isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
      }
    
    this.makeThousandSeparated = function(input) {
        return ControllerCommons.isNumber(input) ? input.toString().split('').reverse().join('').match(/.{1,3}/g).join(' ').split('').reverse().join('') : input;
    };

    this.getHighChartConfigBase = function(chartCategories, chartSeries) {
        return {
            chart : {
                renderTo : 'chart1',
                backgroundColor : 'transparent'
            },
            title : {
                text : ''
            },
            legend : {
                align : 'top left',
                verticalAlign : 'top',
                x : 80,
                y : 0,
                borderWidth : 0
            },
            xAxis : {
                labels : {
                    rotation : 310,
                    align : 'right'
                },
                categories : chartCategories.map(function(name) {
                    return ControllerCommons.htmlsafe(name);
                }),
                title: { align: 'high' }
            },
            yAxis : {
                min : 0,
                title : {
                    text : 'Antal',
                    align : 'high',
                    verticalAlign : 'top',
                    rotation : 0,
                    floating : true,
                    x : 30,
                    y : -10
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
                enabled : false /* This removes the built in highchart export */
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
                        enabled : false,
                    },
                    showInLegend : false
                }
            },
            tooltip : {
                backgroundColor : '#fff',
                borderWidth : 2
            },
            credits : {
                enabled : false
            },
            series : chartSeries
        };
    }

};
