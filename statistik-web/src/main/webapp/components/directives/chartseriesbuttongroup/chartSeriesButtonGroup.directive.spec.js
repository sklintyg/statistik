/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe('Directive: chart-series-button-group', function() {
    'use strict';

    beforeEach(module('StatisticsApp.chartSeriesButtonGroup'));
    beforeEach(module('htmlTemplates'));

    var element, outerScope, innerScope;

    var basicSetOfExchangeableViews = [
        {description: 'Tidsserie', state: '#/verksamhet/sjukfallPerManad', active: true},
        {description: 'Tvärsnitt', state: '#/verksamhet/sjukfallPerManadTvarsnitt', active: false}];

    beforeEach(inject(function($rootScope, $compile) {
        element = angular.element('<chart-series-bg views="exchangeableViews"></chart-series-bg>');
        outerScope = $rootScope;
        $compile(element)(outerScope);

        outerScope.$digest(); //digest the outerscope before the innerScope is called

        innerScope = element.isolateScope();
    }));

    function setUpExchangeableViews(views) {
        outerScope.$apply(function() {
            outerScope.exchangeableViews = views;
        });
    }

    describe('innerscope is created', function() {
        beforeEach(function() {
            setUpExchangeableViews(basicSetOfExchangeableViews);
        });

        it('innerscope.views should be defined', function() {
            expect(innerScope.views).toBeDefined();
        });

        it('innerscope.views array should be of the correct length', function() {
            expect(innerScope.views.length).toEqual(2);
        });
    });

    describe('template produces the right html', function() {
        beforeEach(function() {
            setUpExchangeableViews(basicSetOfExchangeableViews);
        });

        it('anchors have the right href, class and description', function() {
            //Find the anchor that is supposed to be active
            expect(element.find('.active').attr( 'href' )).toEqual(basicSetOfExchangeableViews[0].state);
            expect(element.find('.active').text().trim()).toEqual(basicSetOfExchangeableViews[0].description);

            //Find the other anchor that is not active
            expect(element.find('a').not('.active').attr( 'href' )).toEqual(basicSetOfExchangeableViews[1].state);
            expect(element.find('a').not('.active').text().trim()).toEqual(basicSetOfExchangeableViews[1].description);
        });

    });
});
