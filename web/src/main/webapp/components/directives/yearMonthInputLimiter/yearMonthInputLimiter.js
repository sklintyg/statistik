angular.module('StatisticsApp').directive(
    'yearMonthInputLimiter',
    /** @ngInject */
    function() {
      'use strict';
      return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {

          // Partial entry pattern digts, optionally a dash, but after dash only 0 - 12
          var validEntry = /^[0-9]*\-?(0|1|0[1-9]|1[0-2])?$/i;

          //auto-expand pattern
          var yearAndMore = /^\d{5,6}$/i;

          //Triggered whenever the viewValue is updated (keypress, paste etc)
          function handleViewValueUpdate(newValue, oldValue) {
            var hasUpdatedValue = false;
            //If no value to work with, don't do anything
            if (newValue) {
              //1: check if match a pattern we could expand into dddd-dd
              if (newValue.match(yearAndMore)) {
                newValue = (newValue.substr(0, 4) + '-' + newValue.substr(4));
                hasUpdatedValue = true;
              }
              //2: Should the new value be accepted or rejected?
              if (!newValue.match(validEntry)) {
                //Reject new value, revert to previous
                updateViewValue(oldValue);
              } else {
                //new value is ok, but did we modify it?
                if (hasUpdatedValue) {
                  updateViewValue(newValue);
                }
              }

            }
          }

          function updateViewValue(value) {
            ngModel.$setViewValue(value);
            ngModel.$render();
          }

          scope.$watch(function() {
            return ngModel.$viewValue;
          }, handleViewValueUpdate);

        }
      };
    });
