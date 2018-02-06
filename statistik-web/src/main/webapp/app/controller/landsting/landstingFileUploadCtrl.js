/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').controller('landstingFileUploadCtrl',
    /** @ngInject */
    function ($scope, $rootScope, $timeout, $window, statisticsData, messageService, $location) {
        'use strict';

        $scope.prepopulatedLandstingFileUrl = '/api/landsting/prepopulatedLandstingFile?vgid=' + $location.search().vgid;
        $scope.emptyLandstingFileUrl = '/api/landsting/emptyLandstingFile?vgid=' + $location.search().vgid;

        var updateLastUpdateMessage = function() {
            $scope.lastLandstingUpdateMessage = '';
            $timeout(function () {
                statisticsData.getLastLandstingUpdateInfo(function (result) {
                    $scope.lastLandstingUpdateMessage = result.infoMessage;
                    $scope.parsedRows = result.parsedRows;
                    $rootScope.landstingAvailable = result.parsedRows.length > 0;
                });
            }, 1);
        };

        var updateStatus = function (response) {
            $scope.uploadResultMessage = response.message ? response.message : response;
        };

        $scope.uploadUrl = 'api/landsting/fileupload?vgid=' + $location.search().vgid;

        $scope.dropzoneConfig = {
            'options': {
                url: $scope.uploadUrl,
                parallelUploads: 1,
                maxFilesize: 10, //MB
                paramName: 'file',
                uploadMultiple: false,
                addRemoveLinks: false,
                createImageThumbnails: false,
                maxFiles: 1,
                acceptedFiles: '.xls,.xlsx',
                autoProcessQueue: true,
                clickable: true,
                previewTemplate: '<div id="preview-template" style="display: none;"></div>',
                dictDefaultMessage: '',
                dictInvalidFileType: messageService.getProperty('upload.filetype.error', null, '', null, true),
                dictFileTooBig: messageService.getProperty('upload.filesize.error', null, '', null, true)
            },
            'eventHandlers': {
                'success': function (file, response) {
                    $scope.$apply(function() {
                            $scope.uploadSuccess = true;
                            updateStatus(response);
                            updateLastUpdateMessage();
                            $window.location.reload();
                        }
                    );
                },
                'error': function (file, response, xhr) {
                    $scope.$apply(function() {
                        if(xhr && xhr.status === 403) {
                            $location.path('/login');
                        }
                        $scope.uploadSuccess = false;
                        updateStatus(response);
                    });
                },
                'dragover': function() {
                    $scope.$apply(function() {
                        $scope.uploadCompleted = false;
                        $scope.uploadSuccess = false;
                        $scope.draggingIt = true;
                    });
                },
                'dragleave': function() {
                    $scope.$apply(function() {
                            $scope.draggingIt = false;
                        }
                    );
                },
                'drop': function() {
                    $scope.$apply(function () {
                            $scope.draggingIt = false;
                        }
                    );
                },
                'processing': function() {
                    $scope.$apply(function() {
                            $scope.uploadProgress = 0;
                            $scope.uploading = true;
                            $scope.draggingIt = false;
                        }
                    );
                },
                'uploadprogress': function(file, progress) {
                    $scope.$apply(function() {
                            $scope.uploadProgress = progress;
                        }
                    );
                },
                'complete': function() {
                    $scope.$apply(function() {
                            //Remove the progessbar and handle errors
                            $scope.uploadCompleted = true;
                            $scope.uploading = false;
                        }
                    );
                }
            }
        };

        $scope.clearLandstingEnhets = function() {
            $timeout(function () {
                statisticsData.clearLandstingEnhets(function () {
                    $scope.lastLandstingUpdateMessage = '';
                    $scope.parsedRows = [];
                    $rootScope.landstingAvailable = false;
                    $scope.uploadCompleted = false;
                    updateLastUpdateMessage();
                    $window.location.reload();
                });
            }, 1);
        };

        updateLastUpdateMessage();

    }
);
