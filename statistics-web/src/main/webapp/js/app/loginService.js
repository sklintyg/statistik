app.statisticsApp.
service('loginService', function ($http, $location, $scope, $rootScope, $cookieStore) {
        login = function () {
            $http.get('/api/login/getLoginInfo', $scope.user).success(function (res) {
                $scope.status = 'You are ' +
                    (res.loggedIn ? '' : 'not ') + 'logged in';
                if (res.loggedIn) {
                    $cookieStore.put('sessionToken', res.token);
                }
            });
        };
        var isLoggedIn = function () { $http.get('/api/login/getLoginInfo').success(function (status) {
            if (!status.isLoggedIn) {
                $location.path('/login');
            }
        });
        };
        $rootScope.$on('$routeChangeStart', function (current, next) {
            if (!loginState && next !== '/api/login/getLoginInfo') {
                $location('/api/login/getLoginInfo');
            }
        });
        return {
            login: login,
            isLoggedIn: isLoggedIn
        };
    });
