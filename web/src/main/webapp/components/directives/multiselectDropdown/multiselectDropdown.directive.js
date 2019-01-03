/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').directive('multiselectDropdown',
    /** @ngInject */
    function() {
        'use strict';

        return function(scope, element, attrs) {
            var text = attrs.multiselectDropdown;
            var icon = attrs.multiselectDropdownIcon;
            element.multiselect({
                buttonText: function() {
                    return '<span class="fa ' + icon + '"></span> ' + text;
                },
                onChange: function(optionElement, checked) {
                    if (optionElement) {
                        optionElement.removeAttr('selected');
                        if (checked) {
                            optionElement.prop('selected', 'selected');
                        }
                    }

                    element.change();
                },
                enableHTML: true,
                includeSelectAllOption: true,
                selectAllText: 'Markera alla',
                nonSelectedText: ''
            });

            // Watch for any changes to the length of our select element
            scope.$watch(function() {
                return element[0].length;
            }, function() {
                element.multiselect('rebuild');
            });

            // Watch for any changes from outside the directive and refresh
            scope.$watchCollection(attrs.ngModel, function() {
                element.multiselect('refresh');
            });
        };
    });
