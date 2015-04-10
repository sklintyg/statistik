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
angular.module('StatisticsApp')
    .factory('printFactory', ['COLORS', function(COLORS) {

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
            }) : COLORS.other;
        };

        //If bwPrint === true then return color objects with patterns otherwise the plain colors we use
        var getMaleColorsOrBwPattern = function(bwPrint) {
            return bwPrint ? _.map(
                _.filter(bwPatterns(), function(bwpattern) {
                    return bwpattern.match(/.*[5|6]\.png/);
                }), function (pattern) {
                    return asColorObjectWithPattern(pattern);
                }) : COLORS.male;
        };

        //If bwPrint === true then return color objects with patterns otherwise the plain colors we use
        var getFemaleColorsOrBwPattern = function(bwPrint) {
            return bwPrint ? _.map(
                _.filter(bwPatterns(), function(bwpattern) {
                    return bwpattern.match(/.*[7|8]\.png/);
                }), function (pattern) {
                    return asColorObjectWithPattern(pattern);
                }) : COLORS.female;
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
        };

        //The public api of this factory
        return {
            addColor: addColor,
            setupSeriesForDisplayType: setupSeriesForDisplayType,
            printAndCloseWindow: printAndCloseWindow,
            print: print
        };
    }]);
