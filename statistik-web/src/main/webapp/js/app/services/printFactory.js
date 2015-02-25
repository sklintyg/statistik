'use strict';
angular.module('StatisticsApp')
    .factory('printFactory', function() {

        var addColor = function (rawData, bwPrint) {
            var colorSelector = 0, maleColorSelector = 0, femaleColorSelector = 0;

            var colors = getColorsOrBwPattern(bwPrint),
                maleColor = getMaleColorsOrBwPattern(bwPrint),
                femaleColor = getFemaleColorsOrBwPattern(bwPrint);

            _.each(rawData, function (data) {
                if (data.sex === "Male") {
                    data.color = maleColor[maleColorSelector++];
                } else if (data.sex === "Female") {
                    data.color = femaleColor[femaleColorSelector++];
                } else {
                    data.color = colors[colorSelector++];
                }

            });
            return rawData;
        };

        //If bwPrint === true then return color objects with patterns otherwise the plain colors we use
        var getColorsOrBwPattern = function(bwPrint) {
            return bwPrint ? _.map(_.take(bwPatterns(), 8), function (pattern) {
                return asColorObjectWithPattern(pattern);
            }) : ["#E40E62", "#00AEEF", "#57843B", "#002B54", "#F9B02D", "#724E86", "#34655e", "#8A6B61"];
        };

        //If bwPrint === true then return color objects with patterns otherwise the plain colors we use
        var getMaleColorsOrBwPattern = function(bwPrint) {
            return bwPrint ? _.map(
                _.filter(bwPatterns(), function(bwpattern) {
                    return bwpattern.match(/.*[5|6]\.png/);
                }), function (pattern) {
                    return asColorObjectWithPattern(pattern);
                }) : ["#008391", "#90cad0"];
        };

        //If bwPrint === true then return color objects with patterns otherwise the plain colors we use
        var getFemaleColorsOrBwPattern = function(bwPrint) {
            return bwPrint ? _.map(
                _.filter(bwPatterns(), function(bwpattern) {
                    return bwpattern.match(/.*[7|8]\.png/);
                }), function (pattern) {
                    return asColorObjectWithPattern(pattern);
                }) : ["#EA8025", "#f6c08d"];
        };

        //This function will create a correct path to the pattern images used for black and white printing
        var bwPatterns = function() {
            return function() {
                return _.map(['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'], function(image) {
                    return window.location.pathname + 'img/print-patterns/' + image + '.png';
                });
            };
        }();

        var asColorObjectWithPattern = function(pattern) {
            return {
                pattern: pattern,
                width: 6,
                height: 6
            };
        };

        var addBwPatternForChart = function(series, chartType) {
            var patterns = bwPatterns();
            var dashStyles = [ 'shortdashdotdot', 'dashdot', 'dot', 'longdash', 'shortdot', 'solid', 'shortdash', 'shortdashdot', 'dash', 'longdashdot', 'longdashdotdot' ];

            _.each(series, function(s, index) {
                s.animation = false;
                if (chartType === "bar" || chartType === "area" || chartType === 'pie') {
                    s.color = asColorObjectWithPattern(patterns[index % patterns.length]);
                    s.fillColor = asColorObjectWithPattern(patterns[index % patterns.length]);
                } else if (chartType === "line") {
                    s.color = 'black';
                    s.dashStyle = dashStyles[index % dashStyles.length];
                }
            });

            return series;
        };

        //Setup the series for either color or black and white.
        var setupSeriesForDisplayType = function(setupForBwPrint, series, chartType) {
            return setupForBwPrint && chartType ? addBwPatternForChart(series, chartType) : this.addColor(series, setupForBwPrint);
        };

        var printAndCloseWindow = function($timeout, $window) {
            $( document ).ready( function(){
                $timeout(function() {
                    $window.print();
                    if ('afterprint' in window) {
                        $(window).on('afterprint', function(){$window.close();});
                    } else {
                        $timeout(function() {
                            $window.close();
                        }, 100);
                    }
                }, 3000);
            } );
        };

        var print = function(bwPrint, rootScope, windowParam) {
            var printQuery = bwPrint ? "printBw=true" : "print=true";
            var prefixChar = rootScope.queryString ? "&" : "?";
            windowParam.open(windowParam.location + prefixChar + printQuery);
        }

        //The public api of this factory
        return {
            addColor: addColor,
            setupSeriesForDisplayType: setupSeriesForDisplayType,
            printAndCloseWindow: printAndCloseWindow,
            print: print
        }
    });
