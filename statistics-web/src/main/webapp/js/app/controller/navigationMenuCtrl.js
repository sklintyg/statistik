 'use strict';

 app.navigationMenuCtrl = function ($scope, $rootScope) {

     $scope.organisationMenuLabel = isLoggedIn ? "Verksamhetsstatistik" : "Logga in för verksamhetsstatistik";
     
     $scope.showNational = false;
     $scope.showOperation = false;
     $scope.showAbout = false;
     $scope.is_loggedin = isLoggedIn;

     $scope.toggleNationalAccordion = function() {
         $scope.showNational = !$scope.showNational;
         $scope.showOperation = false;
         $scope.showAbout = false;
     };
     
     $scope.toggleOperationAccordion = function() {
         if (isLoggedIn) {
             $scope.showOperation = !$scope.showOperation;
             $scope.showNational = false;
             $scope.showAbout = false;
         }
     };
     
     $scope.toggleAboutAccordion = function() {
         $scope.showAbout = !$scope.showAbout;
         $scope.showNational = false;
         $scope.showOperation = false;
     };
     
     $scope.$on('navigationUpdate', function(event, navigationGroupId) {
         if (navigationGroupId === "about-statistics-collapse"){
             $scope.showNational = false;
             $scope.showOperation = false;
             $scope.showAbout = true;
         } else if (navigationGroupId === "national-statistics-collapse"){
             $scope.showNational = true;
             $scope.showOperation = false;
             $scope.showAbout = false;
         } else if (navigationGroupId === "business-statistics-collapse"){
             $scope.showNational = false;
             $scope.showOperation = true;
             $scope.showAbout = false;
         } else {
             $scope.showNational = true;
             $scope.showOperation = false;
             $scope.showAbout = false;
         }
     }); 

 };
