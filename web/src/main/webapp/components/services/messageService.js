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

/**
 * message directive for externalizing text resources.
 *
 * All resourcekeys are expected to be defined in lowercase and available in a
 * global js object named "messages"
 * Also supports dynamic key values such as key="status.{{scopedvalue}}"
 *
 * Usage: <message key="some.resource.key" [fallback="defaulttextifnokeyfound"]/>
 */
angular.module('StatisticsApp').factory('messageService',
    /** @ngInject */
    function($rootScope) {
        'use strict';

        var _messageResources = null;
        var _links = null;

        function _propertyExists(key, language, fallbackToDefaultLanguage) {
            var value;

            if (!language) {
                language = $rootScope.lang;
                if (!language && fallbackToDefaultLanguage) {
                    language = $rootScope.DEFAULT_LANG;
                }
            }

            if (language) {
                value = _getPropertyInLanguage(language, key, null);
                if (value === null || value === undefined) {
                    value = false;
                }
            } else {
                value = false;
            }

            return value;
        }

        function _getProperty(key, variables, defaultValue, language, fallbackToDefaultLanguage) {
            var value;

            if (!language) {
                language = $rootScope.lang;
                if (!language && fallbackToDefaultLanguage) {
                    language = $rootScope.DEFAULT_LANG;
                }
            }

            if (language) {
                value = _getPropertyInLanguage(language, key, variables);
                if (value === null || value === undefined) {
                    value = defaultValue === null || defaultValue === undefined ?
                        '[Missing "' + key + '"]' : defaultValue;
                }
            } else {
                value = '[Missing language]';
            }

            return value;
        }

        function _getPropertyInLanguage(lang, key, variables) {
            _checkResources();

            var normalizedKey = angular.lowercase(key);

            var message = _messageResources[lang][normalizedKey];

            angular.forEach(variables, function(value, key) {
                var regexp = new RegExp('\\$\\{' + key + '\\}', 'g');
                message = message.replace(regexp, value);
            });

            // Find <LINK: dynamic links and replace
            var regex2 = /<LINK:(.*?)>/gi, result;

            while ( (result = regex2.exec(message)) ) {
                var replace = result[0];
                var linkKey = result[1];

                var dynamicLink = _buildDynamicLink(linkKey);

                var regexp = new RegExp(replace, 'g');
                message = message.replace(regexp, dynamicLink);
            }

            return message;
        }

        function _addResources(resources) {
            _checkResources();
            angular.extend(_messageResources.sv, resources.sv);
            angular.extend(_messageResources.en, resources.en);
        }

        function _checkResources() {
            if (_messageResources === null) {
                _messageResources = {
                    'sv': {
                        'initial.key': 'Initial nyckel'
                    },
                    'en': {
                        'initial.key': 'Initial key'
                    }
                };
            }
        }

        function _buildDynamicLink(linkKey) {
            var link = _links[linkKey];

            if (!angular.isDefined(link)) {
                return 'WARNING: could not resolve dynamic link: ' + linkKey;
            }

            var dynamicLink = '<a class="external-link" href="' + link.url + '"';
            dynamicLink += link.tooltip ? ' title="' + link.tooltip + '"' : '';
            dynamicLink += link.target ? ' target="' + link.target + '">' : '>';
            dynamicLink += link.text;

            if (link.target) {
                dynamicLink += ' <i class="fa fa-external-link"></i></a>';
            }

            dynamicLink += '</a>';

            return dynamicLink;
        }

        function _addLinks(links) {
            _links = links;
        }

        return {
            propertyExists: _propertyExists,
            getProperty: _getProperty,
            addResources: _addResources,
            addLinks: _addLinks
        };
    }
);
