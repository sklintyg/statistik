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

(function() {
  'use strict';

  var constantModule = angular.module('StatisticsApp.constants', []);

  /*
   * X and Y Offsets for county
   */
  var countyCoordinates = [
    {name: 'blekinge', xy: {'x': 35, 'y': 14}},
    {name: 'dalarna', xy: {'x': 29, 'y': 49}},
    {name: 'halland', xy: {'x': 15, 'y': 19}},
    {name: 'kalmar', xy: {'x': 40, 'y': 20}},
    {name: 'kronoberg', xy: {'x': 29, 'y': 19}},
    {name: 'gotland', xy: {'x': 55, 'y': 22}},
    {name: 'gävleborg', xy: {'x': 45, 'y': 50}},
    {name: 'jämtland', xy: {'x': 29, 'y': 62}},
    {name: 'jönköping', xy: {'x': 28, 'y': 24}},
    {name: 'norrbotten', xy: {'x': 59, 'y': 87}},
    {name: 'skåne', xy: {'x': 21, 'y': 11}},
    {name: 'stockholm', xy: {'x': 52, 'y': 37}},
    {name: 'södermanland', xy: {'x': 44, 'y': 33}},
    {name: 'uppsala', xy: {'x': 50, 'y': 42}},
    {name: 'värmland', xy: {'x': 18, 'y': 38}},
    {name: 'västerbotten', xy: {'x': 50, 'y': 72}},
    {name: 'västernorrland', xy: {'x': 48, 'y': 62}},
    {name: 'västmanland', xy: {'x': 42, 'y': 40}},
    {name: 'västra götaland', xy: {'x': 12, 'y': 25}},
    {name: 'örebro', xy: {'x': 32, 'y': 36}},
    {name: 'östergötland', xy: {'x': 39, 'y': 29}}
  ];

  constantModule.constant('COUNTY_COORDS', countyCoordinates);

  /* Color definitions to be used with highcharts */
  var colors = {
    male: ['#008391', '#90cad0'],
    female: ['#EA8025', '#f6c08d'],
    total: '#5D5D5D',
    overview: '#57843B',
    other: [
      '#E11964',
      '#032C53',
      '#FFBA3E',
      '#799745',
      '#3CA3FF',
      '#C37EB2',
      '#2A5152',
      '#FB7F4D',
      '#5CC2BC',
      '#704F38',
      '#600030',
      '#006697']
  };

  constantModule.constant('COLORS', colors);

  /* Beginning of time for the StatiscsApp*/
  var timeIntervalMinDate = moment('2013-10-01', 'YYYY-MM-DD');
  constantModule.constant('TIME_INTERVAL_MIN_DATE', timeIntervalMinDate);

  /* Max selectable date will always be the current month*/
  var timeIntervalMaxDate = moment();
  constantModule.constant('TIME_INTERVAL_MAX_DATE', timeIntervalMaxDate);

  /* Use momentjs http://momentjs.com/ This is a MIT license */
  constantModule.constant('moment', moment);

  constantModule.constant('MAX_SELECTED_DXS', 75);
  constantModule.constant('MAX_INIT_ROWS_TABLE', 100);
  constantModule.constant('MAX_INIT_COLUMNS_TABLE', 33 * 3 + 1);
  constantModule.constant('MAX_ROWS_TABLE_PDF', 499);

  var phrasesToHighlight = {
    'sjukfall': 'Ett sjukfall omfattar en patients alla elektroniska läkarintyg som följer på varandra med max fem dagars uppehåll. Intygen måste även vara utfärdade av samma vårdgivare. Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare räknas det som två sjukfall.',
    'meddelanden': 'Meddelanden som skickats elektroniskt från Försäkringskassan till hälso- och sjukvården. Ett meddelande rör alltid ett visst elektroniskt intyg som utfärdats av hälso- och sjukvården och som skickats till Försäkringskassan.',
    'utfärdade intyg': 'Elektroniska intyg som har utfärdats och signerats av hälso- och sjukvården.',
    'Okänd befattning': 'Innehåller sjukfall där läkaren inte går att slå upp i HSA-katalogen eller där läkaren inte har någon befattning angiven.',
    'Ej läkarbefattning': 'Innehåller sjukfall där läkaren inte har någon läkarbefattning angiven i HSA men däremot andra slags befattningar.',
    'Utan giltig ICD-10 kod': 'Innehåller sjukfall som inte har någon diagnoskod angiven eller där den angivna diagnoskoden inte finns i klassificeringssystemet för diagnoser, ICD-10-SE.',
    'Okänt län': 'Innehåller de sjukfall där enheten som utfärdat intygen inte har något län angivet i HSA-katalogen.'
  };
  constantModule.constant('PHRASES_TO_HIGHLIGHT', phrasesToHighlight);

  constantModule.constant('CATEGORY_TO_HIDE', 'Totalt');
})();

