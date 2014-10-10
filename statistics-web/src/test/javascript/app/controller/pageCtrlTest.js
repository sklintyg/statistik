describe('Controller: pageCtrl', function() {
    beforeEach(module('StatisticsApp'));

    isLoggedIn = false;
    var ctrl, scope;

    beforeEach(inject(function($rootScope, $controller, $window) {
        scope = $rootScope.$new();
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
