/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

(function() {
    'use strict';

    angular
        .module('StatisticsApp')
        .factory('ArrayHelper', arrayHelper);

    /** @ngInject */
    function arrayHelper(_, ObjectHelper) {

        return {
            isDifferent: _isDifferent,
            sortSwedish: _sortSwedish
        };

        function _isDifferent(arrayOne, arrayTwo) {
            var changed = arrayOne.length !== arrayTwo.length;

            if (changed) {
                return changed;
            }

            var difference = _.xor(arrayOne, arrayTwo);

            return difference.length !== 0;
        }

        function isSet(value) {
            return typeof value !== 'undefined' && value !== null;
        }

        function _sortSwedish(arrayToSort, propertyName, alwaysLast) {
            var swedishAlphabet = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZzÅåÄäÖö';
            return arrayToSort.sort(function (first, second) {
                if (first[propertyName] === second[propertyName]) {
                    return 0;
                }

                if (ObjectHelper.isEmpty(first[propertyName])) {
                    return 1;
                }

                if (ObjectHelper.isEmpty(second[propertyName])) {
                    return -1;
                }

                if (isSet(alwaysLast) && first[propertyName].indexOf(alwaysLast) > -1) {
                    return 1;
                }

                if (isSet(alwaysLast) && second[propertyName].indexOf(alwaysLast) > -1) {
                    return -1;
                }

                for (var i = 0; true; i++) {
                    if (first[propertyName].length <= i) {
                        return -1;
                    }
                    if (second[propertyName].length <= i) {
                        return 1;
                    }
                    var posFirst = swedishAlphabet.indexOf(first[propertyName][i]);
                    var posSecond = swedishAlphabet.indexOf(second[propertyName][i]);
                    if (posFirst !== posSecond) {
                        return posFirst - posSecond;
                    }
                }
            });
        }
    }
})();
