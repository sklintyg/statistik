describe('Controller: pageCtrl', function() {
    beforeEach(module('StatisticsApp'));

    // This global variable must be set since it is used (in a questionable way in the run time controllers).
    isLoggedIn = false;
    var ctrl;

    beforeEach(inject(function($rootScope, $controller, $window) {
        var scope = $rootScope.$new();
        ctrl = $controller('pageCtrl', {$scope: scope, $rootScope: $rootScope, $window: $window, $cookies: {}, statisticsData: {}, businessFilter: {}});
    }));
    
    it('should select correct selected verksamhet from available verksamhets', function() {
        var verksamhet1 = { vardgivarId: 'id1', name: 'Verksamhet1' };
        var verksamhet2 = { vardgivarId: 'id2', name: 'Verksamhet2' };
        expect(ctrl.getSelectedVerksamhet('id1', [ verksamhet1, verksamhet2 ])).toEqual(verksamhet1);
    });

    it('should return default verksamhet if no verksamhet matches', function() {
        var verksamhet1 = { vardgivarId: 'id1', name: 'Verksamhet1' };
        var verksamhet2 = { vardgivarId: 'id2', name: 'Verksamhet2' };
        expect(ctrl.getSelectedVerksamhet('id3', [ verksamhet1, verksamhet2 ])).toEqual({ name: "Okänd verksamhet"});
    });

});
