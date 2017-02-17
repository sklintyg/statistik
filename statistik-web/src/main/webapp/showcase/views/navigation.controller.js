angular.module('showcase').controller('showcase.NavigationCtrl',
    ['$scope', '$window', '$localStorage', 'AppModel', 'UserModel',
        function($scope, $window, $localStorage, AppModel, UserModel) {
            'use strict';
            
            $scope.showCookieBanner = false;
            $scope.doShowCookieBanner = function() {
                $localStorage.cookieBannerShown = false;
                $scope.showCookieBanner = !$scope.showCookieBanner;
            };
            
            $scope.today = new Date();
 
            $scope.isCollapsed = true;

            //Fake some values for the header directive dependencies
            $scope.AppModel = AppModel;
            $scope.AppModel.set({
                loggedIn: true,
                loginUrl: 'loginUrl',
                loginVisible: true,
                highchartsExportUrl: 'highcharts',
                sjukskrivningLengths: {
                    'id': 'text',
                    'ad': 'text2'
                }
            });

            $scope.UserModel = UserModel;
            $scope.UserModel.setLoginInfo({
                'hsaId':'HSA-AM',
                'name':'Förnamn Efternamn',
                'vgs':[
                    {
                        'hsaId':'VG1',
                        'name':'Fitnessetestvårdgivare1',
                        'landstingsVardgivareStatus':'NO_LANDSTINGSVARDGIVARE',
                        'processledare':true,
                        'landstingAdmin':false,
                        'landstingsvardgivare':false,
                        'delprocessledare':false,
                        'landstingsvardgivareWithUpload':false,
                        'verksamhetschef':false
                    }
                ],
                'loggedIn':true
            });

            $scope.UserModel.setUserAccessInfo({
                'hsaId':'HSA-AM',
                'vgInfo':{
                    'hsaId':'VG1',
                    'name':'Fitnessetestvårdgivare1',
                    'landstingsVardgivareStatus':'NO_LANDSTINGSVARDGIVARE',
                    'processledare':true,
                    'landstingAdmin':false,
                    'landstingsvardgivare':false,
                    'delprocessledare':false,
                    'landstingsvardgivareWithUpload':false,
                    'verksamhetschef':false
                },
                'businesses':[
                {
                    'id':'VG1-ENHET-1',
                    'name':'Enheten VG1-ENHET-1',
                    'vardgivarId':'VG1',
                    'vardgivarName':null,
                    'lansId':'23',
                    'lansName':'Jämtlands län',
                    'kommunId':'80',
                    'kommunName':'Östersund',
                    'verksamhetsTyper':[
                        {
                            'id':'16',
                            'name':'Psykiatrisk verksamhet'
                        },
                        {
                            'id':'02',
                            'name':'Vårdcentral'
                        }
                    ]
                },
                {
                    'id':'VG1-ENHET-10',
                    'name':'Enheten VG1-ENHET-10',
                    'vardgivarId':'VG1',
                    'vardgivarName':null,
                    'lansId':'23',
                    'lansName':'Jämtlands län',
                    'kommunId':'80',
                    'kommunName':'Östersund',
                    'verksamhetsTyper':[
                        {
                            'id':'02',
                            'name':'Vårdcentral'
                        }
                    ]
                },
                {
                    'id':'VG1-ENHET-11',
                    'name':'Enheten VG1-ENHET-11',
                    'vardgivarId':'VG1',
                    'vardgivarName':null,
                    'lansId':'23',
                    'lansName':'Jämtlands län',
                    'kommunId':'00',
                    'kommunName':'Okänd kommun',
                    'verksamhetsTyper':[
                        {
                            'id':'20',
                            'name':'Övrig medicinsk serviceverksamhet'
                        },
                        {
                            'id':'02',
                            'name':'Vårdcentral'
                        }
                    ]
                }]});

            //hack to show logged in user
            $scope.UserModel.get().enableVerksamhetMenu = true;


        }]);
