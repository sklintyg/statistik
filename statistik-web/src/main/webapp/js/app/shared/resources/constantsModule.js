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
var constantModule = angular.module('StatisticsApp.constants', []);

/*
 * X and Y Offsets for county
 */
var countyCoordinates = [
    {name: 'blekinge', xy: {"x": 35, "y": 15}},
    {name: 'dalarna', xy: {"x": 31, "y": 50}},
    {name: 'halland', xy: {"x": 14, "y": 20}},
    {name: 'kalmar', xy: {"x": 40, "y": 20}},
    {name: 'kronoberg', xy: {"x": 32, "y": 19}},
    {name: 'gotland', xy: {"x": 55, "y": 22}},
    {name: 'gävleborg', xy: {"x": 45, "y": 50}},
    {name: 'jämtland', xy: {"x": 29, "y": 66}},
    {name: 'jönköping', xy: {"x": 28, "y": 24}},
    {name: 'norrbotten', xy: {"x": 59, "y": 94}},
    {name: 'skåne', xy: {"x": 21, "y": 11}},
    {name: 'stockholm', xy: {"x": 52, "y": 37}},
    {name: 'södermanland', xy: {"x": 44, "y": 34}},
    {name: 'uppsala', xy: {"x": 50, "y": 42}},
    {name: 'värmland', xy: {"x": 21, "y": 42}},
    {name: 'västerbotten', xy: {"x": 51, "y": 80}},
    {name: 'västernorrland', xy: {"x": 48, "y": 67}},
    {name: 'västmanland', xy: {"x": 42, "y": 42}},
    {name: 'västra götaland', xy: {"x": 12, "y": 32}},
    {name: 'örebro', xy: {"x": 32, "y": 38}},
    {name: 'östergötland', xy: {"x": 40, "y": 30}},
    {name: 'DEFAULT', xy: {"x": 12, "y": 94}}];

constantModule.constant('COUNTY_COORDS',countyCoordinates);

/* Color definitions to be used with highcharts */
var colors = {
    male: ["#008391", "#90cad0"],
    female: ["#EA8025", "#f6c08d"],
    other: ["#E11964", "#FB7F4D", "#FFBA3E", "#032C53", "#3CA3FF", "#5CC2BC", "#799745", "#2A5152", "#C37EB2", "#704F38"]
};

constantModule.constant('COLORS', colors);
