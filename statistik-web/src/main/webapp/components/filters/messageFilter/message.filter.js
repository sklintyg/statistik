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
/**
 * Will look up key from message file and return translation.
 */
angular.module('StatisticsApp').filter('messageFilter',
    /** @ngInject */
    function($log, $rootScope, $filter, messageService) {
        'use strict';

        return function(interpolatedKey, fallback, fallbackDefaultLang, params, lang, disableHighlightWords) {
            var result;
            var normalizedKey = angular.lowercase(interpolatedKey);
            var useLanguage;
            if (typeof lang !== 'undefined') {
                useLanguage = lang;
            } else {
                useLanguage = $rootScope.lang;
            }

            result = messageService.getProperty(normalizedKey, null, fallback, useLanguage,
                (typeof fallbackDefaultLang !== 'undefined'));

            if (typeof params !== 'undefined') {
                var myparams = params;
                for (var i = 0; i < myparams.length; i++) {
                    result = result.replace('%' + i, myparams[i]);
                }
            }

            if (disableHighlightWords) {
                return result;
            } else {
                return $filter('highlightWords')(result);
            }
        };
    });
