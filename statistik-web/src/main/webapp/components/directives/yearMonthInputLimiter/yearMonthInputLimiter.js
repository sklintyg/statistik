/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
                                //new value is ok, but did we modifiy it?
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
