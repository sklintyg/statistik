/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp')
    .directive('compareDiagnosisSelection', [function() {
        'use strict';

        return {
            scope: {
                routeParams: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/compareDiagnosisSelection/compareDiagnosisSelection.html',
            controllerAs: 'vm',
            bindToController: true,
            controller: function($scope, MAX_SELECTED_DXS, messageService, $timeout, $location, ControllerCommons, diagnosisTreeFilter, statisticsData) {
                var vm = this;

                _init();

                $scope.$watch('vm.level', function(newValue, oldValue) {
                    if (newValue === oldValue) {
                        return;
                    }

                    var urlLevel = diagnosisTreeFilter.levelToUrlLevel(newValue);

                    $timeout(function() {
                        var newPath = getCompareDiagnosisPath('-');
                        $location.search('codelevel', urlLevel);
                        $location.path(newPath);
                    });
                });

                function _init() {
                    vm.maxSelectedDxs = MAX_SELECTED_DXS;

                    vm.diagnosisSelectorData = {
                        titleText: messageService.getProperty('comparediagnoses.lbl.val-av-diagnoser', null, '', null, true),
                        buttonLabelText: messageService.getProperty('lbl.filter.val-av-diagnoser-knapp', null, '', null, true),
                        firstLevelLabelText: messageService.getProperty('lbl.filter.modal.kapitel', null, '', null, true),
                        secondLevelLabelText: messageService.getProperty('lbl.filter.modal.avsnitt', null, '', null, true),
                        thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.kategorier', null, '', null, true),
                        leavesLevelLabelText: messageService.getProperty('lbl.filter.modal.leaves', null, '', null, true)
                    };

                    vm.diagnoses = diagnosisTreeFilter.levels;

                    if ($location.search().codelevel) {
                        vm.urlLevel = parseInt($location.search().codelevel, 10);
                    } else {
                        vm.urlLevel = 0;
                    }

                    vm.level = diagnosisTreeFilter.urlLevelToCorrectLevel(vm.urlLevel);

                    diagnosisTreeFilter.setup(vm.routeParams.diagnosHash, vm.level);
                    vm.diagnosisTreeFilter = diagnosisTreeFilter;
                    vm.diagnosisSelected = diagnosisToCompareSelected;
                }

                function diagnosisToCompareSelected() {
                    var diagnoses = diagnosisTreeFilter.getSelectedDiagnosis();

                    var params = {
                        diagnoser: diagnoses
                    };

                    var success = function (selectionHash) {
                        var path = $location.path();
                        var search = $location.search();
                        var newPath = getCompareDiagnosisPath(selectionHash);

                        if (path !== newPath) {
                            delete search.chartType;

                            $location.path(newPath);
                            $location.search(search);
                        }
                    };

                    var error = function () {
                        throw new Error('Failed to get filter hash value');
                    };

                    statisticsData.getFilterHash(params).then(success, error);
                }

                function getCompareDiagnosisPath(selectionHash) {
                    return '/verksamhet/jamforDiagnoser/' + selectionHash;
                }
            }
        };
    }]);
