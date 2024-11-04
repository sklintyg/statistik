describe('redirectInterceptor', function() {
    'use strict';
    var $q, $location, redirectInterceptor;
    
    beforeEach(module('StatisticsApp'));
    beforeEach(inject(function(_$q_, _$location_, _redirectInterceptor_) {
        $q = _$q_;
        $location = _$location_;
        redirectInterceptor = _redirectInterceptor_;
    }));
    
    it('should redirect to /login with error=loginRequired on 403 response error', function() {
        spyOn($location, 'path').and.callThrough();
        spyOn($location, 'search').and.callThrough();
        
        var rejection = { status: 403 };
        
        redirectInterceptor.responseError(rejection);
        
        expect($location.path).toHaveBeenCalledWith('/login');
        expect($location.search).toHaveBeenCalledWith({ error: 'loginRequired' });
    });
});