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

angular.module('StatisticsApp').factory('StaticData',
    /** @ngInject */
    function($filter, _) {
        'use strict';

        var data = {};

        _reset();

        function _reset() {
            data.sjukskrivningLengths = [];
            data.intygTypes = [];
            data.intygTooltips = {};
            data.aldersgrupps = [];
            data.icd10Structure = [];
            return data;
        }

        function setSjukskrivningslangd(app) {
            data.sjukskrivningLengths.length = 0;
            data.sjukskrivningLengthsObject = app.sjukskrivningLengths;

            angular.forEach(app.sjukskrivningLengths, function(value, key)  {
                data.sjukskrivningLengths.push({
                    id: key,
                    name: value
                });
            });

            data.sjukskrivningLengths = $filter('orderBy')(data.sjukskrivningLengths, 'id', false);
        }

        function setAldersgrupps(app) {
            data.aldersgrupps.length = 0;
            data.ageGroups = app.ageGroups;

            angular.forEach(app.ageGroups, function(value, key)  {
                data.aldersgrupps.push({
                    id: key,
                    name: value
                });
            });

            data.aldersgrupps = $filter('orderBy')(data.aldersgrupps, 'id', false);
        }

        function setIntygTypes(app) {
            data.intygTypes.length = 0;
            data.intygTypesObject = app.intygTypes;

            angular.forEach(app.intygTypes, function(value, key)  {
                data.intygTypes.push({
                    id: key,
                    name: value
                });
            });

            data.intygTypes = $filter('orderBy')(data.intygTypes, 'name', false);
        }

        function setIntygToolTip(app) {
            data.intygTooltips = app.intygTooltip;
        }

        function setIcd10Structure(app) {
            data.icd10Structure = app.dxs;

            function showIdInName(dx) {
                var isNumber = angular.isNumber(dx.numericalId);
                return (isNumber && dx.numericalId > 0) || !isNumber;
            }

            function getVisibleName(dx) {
                return showIdInName(dx) ? dx.id + ' ' + dx.name : dx.name;
            }

            _.each(data.icd10Structure, function (kapitel) {
                kapitel.typ = 'kapitel';
                kapitel.subs = kapitel.subItems;
                delete kapitel.subItems;
                kapitel.visibleName = getVisibleName(kapitel);
                _.each(kapitel.subs, function (avsnitt) {
                    avsnitt.typ = 'avsnitt';
                    avsnitt.subs = avsnitt.subItems;
                    delete avsnitt.subItems;
                    avsnitt.visibleName = getVisibleName(avsnitt);
                    _.each(avsnitt.subs, function (kategori) {
                        kategori.visibleName = getVisibleName(kategori);
                        kategori.typ = 'kategori';
                        kategori.subs = kategori.subItems;
                        delete kategori.subItems;
                        _.each(kategori.subs, function(kod) {
                            kod.typ = 'kod';
                            kod.visibleName = getVisibleName(kod);
                            });
                    });
                });
            });
        }

        function icdStructureAsArray(icdStructure) {
            return _.map(icdStructure, function (icd) {
                return icdStructureAsArray(icd.subs).concat(icd);
            });
        }

        function getDiagnosFilterInformationText(diagnosFilterIds, asObject) {
            var icdStructureAsFlatArray = _.flowRight(_.flattenDeep, icdStructureAsArray)(data.icd10Structure);
            if (icdStructureAsFlatArray.length === 0) {
                return [];
            }
            return _.map(diagnosFilterIds, function(diagnosId){
                var icdItem = _.find(icdStructureAsFlatArray, function(icd){
                    return icd.numericalId === parseInt(diagnosId, 10);
                });

                if (asObject) {
                    return {
                        id: diagnosId,
                        text: icdItem ? icdItem.visibleName : ''
                    };
                }
                return icdItem ? icdItem.visibleName : '';
            });
        }


        return {
            reset: _reset,
            init: function() {
                return _reset();
            },
            set: function(app) {
                _reset();
                setSjukskrivningslangd(app);
                setAldersgrupps(app);
                setIcd10Structure(app);
                setIntygTypes(app);
                setIntygToolTip(app);
            },
            get: function() {
                return data;
            },
            getDiagnosFilterInformationText: getDiagnosFilterInformationText
        };
    }
);
