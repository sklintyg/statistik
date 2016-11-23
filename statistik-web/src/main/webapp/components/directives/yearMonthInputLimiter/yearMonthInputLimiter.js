angular.module('StatisticsApp').directive(
        'yearMonthInputLimiter',
        /** @ngInject */
        function() {
            'use strict';
            return {
                restrict: 'A',
                require: 'ngModel',
                link: function(scope, element, attrs, ngModel) {
                    var patternByLength = [
                    /.*/i, //Anything
                    /^[1|2]$/i, // 1 or 2
                    /^(19|20)$/i, // 19  or 20
                    /^(19|20)\d$/i, // 19X or 20X
                    /^(19|20)\d{2}$/i, // 19XX or 20XX
                    /^(19|20)\d{2}\-?$/i, // dddd and possibly a '-'- (ddddd are expanded into  dddd-d)
                    /^(19|20)\d{2}\-(0|1)?$/i, // XXXX-X
                    /^(19|20)\d{2}\-(0|1|0[1-9]|1[0-2])?$/i // Complete date match XXXX-XX

                    ];
                    var yearAndMorePattern = /^\d{5,6}$/i;

                    //Triggered whenever the viewValue is updated (keypress, paste etc)
                    function handleViewValueUpdate(newValue, oldValue) {
                        var hasUpdatedValue = false;
                        //If no value to work with, don't do anything
                        if (newValue) {
                            //Expand values ddddd and dddddd to dddd-dd
                            if (newValue.match(yearAndMorePattern)) {
                                newValue = (newValue.substr(0, 4) + '-' + newValue.substr(4));
                                hasUpdatedValue = true;
                            }
                            //Should this new value be accepted or rejected?
                            var patternIndexToMatch = newValue.length < patternByLength.length ? newValue.length
                                    : patternByLength.length - 1;
                            if (!newValue.match(patternByLength[patternIndexToMatch])) {
                                //Reject new value, revert to previous
                                updateViewValue(oldValue);
                            } else {
                                //new value is ok, but did we modifiy it here?
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
