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