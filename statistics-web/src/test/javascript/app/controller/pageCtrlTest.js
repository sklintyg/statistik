describe('Controller: pageCtrl', function() {
    // Instantiate a new version of my module before each test
    beforeEach(module('StatisticsApp'));

    var ctrl, scope, isLoggedIn;

    beforeEach(function() {
        var jesper = "holmberg";
    });

    // Before each unit test, instantiate a new instance of the controller
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
