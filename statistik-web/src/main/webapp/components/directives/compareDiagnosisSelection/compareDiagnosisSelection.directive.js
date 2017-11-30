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

                vm.maxSelectedDxs = MAX_SELECTED_DXS;

                vm.diagnosisSelectorData = {
                    titleText: messageService.getProperty('comparediagnoses.lbl.val-av-diagnoser', null, '', null, true),
                    buttonLabelText: messageService.getProperty('lbl.filter.val-av-diagnoser-knapp', null, '', null, true),
                    firstLevelLabelText: messageService.getProperty('lbl.filter.modal.kapitel', null, '', null, true),
                    secondLevelLabelText: messageService.getProperty('lbl.filter.modal.avsnitt', null, '', null, true),
                    thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.kategorier', null, '', null, true),
                    leavesLevelLabelText: messageService.getProperty('lbl.filter.modal.leaves', null, '', null, true)
                };

                vm.diagnoses = {
                    section: 2,
                    chapter: 3,
                    category: 0,
                    code: 1
                };

                if ($location.search().codelevel) {
                    vm.codeLevel = parseInt($location.search().codelevel, 10);
                } else {
                    vm.codeLevel = 0;
                }
                setMaxDepth();

                $scope.$watch('vm.codeLevel', function(newValue, oldValue) {
                    if (newValue === oldValue) {
                        return;
                    }

                    setMaxDepth();

                    $timeout(function() {
                        var newPath = getCompareDiagnosisPath('-');
                        $location.search('codelevel', newValue);
                        $location.path(newPath);
                    });
                });

                diagnosisTreeFilter.setup(vm.routeParams, vm.codeLevel);
                vm.diagnosisTreeFilter = diagnosisTreeFilter;

                vm.diagnosisSelected = function () {
                    diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location);
                };

                function setMaxDepth() {
                    var depth;

                    console.log(vm.codeLevel);

                    switch (vm.codeLevel) {
                    case 1:
                        depth = 4;
                        break;
                    case 2:
                        depth = 1;
                        break;
                    case 3:
                        depth = 2;
                        break;
                    default:
                        depth = 3;
                    }

                    vm.maxDepth = depth;
                }


                function diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location) {
                    var diagnoses = diagnosisTreeFilter.getSelectedDiagnosis();

                    $timeout(function () {
                        $scope.doneLoading = false;
                    }, 1);

                    var params = {
                        diagnoser: diagnoses
                    };

                    var success = function (selectionHash) {
                        var path = $location.path();
                        var search = $location.search();
                        var newPath = getCompareDiagnosisPath(selectionHash);

                        if (path === newPath) {
                            $timeout(function () {
                                $scope.doneLoading = true;
                            }, 1);
                        } else {
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
