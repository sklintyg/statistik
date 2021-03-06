/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('regionFileUploadCtrl',
    /** @ngInject */
    function($scope, $rootScope, $timeout, $window, statisticsData, messageService, $location) {
      'use strict';

      $scope.prepopulatedRegionFileUrl = '/api/region/prepopulatedRegionFile?vgid=' + $location.search().vgid;
      $scope.emptyRegionFileUrl = '/api/region/emptyRegionFile?vgid=' + $location.search().vgid;

      var updateLastUpdateMessage = function() {
        $scope.lastRegionUpdateMessage = '';
        $timeout(function() {
          statisticsData.getLastRegionUpdateInfo(function(result) {
            $scope.lastRegionUpdateMessage = result.infoMessage;
            $scope.parsedRows = result.parsedRows;
            $rootScope.regionAvailable = result.parsedRows.length > 0;
          });
        }, 1);
      };

      var updateStatus = function(response) {
        $scope.uploadResultMessage = response.message ? response.message : response;
      };

      $scope.uploadUrl = 'api/region/fileupload?vgid=' + $location.search().vgid;

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
          'success': function(file, response) {
            $scope.$apply(function() {
                  $scope.uploadSuccess = true;
                  updateStatus(response);
                  updateLastUpdateMessage();
                  $location.search('fileUploadSuccess', true);
                  $timeout(function() {
                    $window.location.reload();
                  }, 1);
                }
            );
          },
          'error': function(file, response, xhr) {
            $scope.$apply(function() {
              if (xhr && xhr.status === 403) {
                $location.path('/login');
              }
              $scope.uploadSuccess = false;
              updateStatus(response);
              $location.search('fileUploadSuccess', null);
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
            $scope.$apply(function() {
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

      $scope.clearRegionEnhets = function() {
        $timeout(function() {
          statisticsData.clearRegionEnhets(function() {
            $scope.lastRegionUpdateMessage = '';
            $scope.parsedRows = [];
            $rootScope.regionAvailable = false;
            $scope.uploadCompleted = false;
            updateLastUpdateMessage();
            $window.location.reload();
          });
        }, 1);
      };

      $scope.fileUploadAgreementAccepted = !!($location.search().fileUploadAgreementAccepted);
      $scope.agreementAcceptedChecked = false;
      $scope.acceptFileUploadAgreement = function() {
        statisticsData.acceptFileUploadAgreement(function() {
          $scope.fileUploadAgreementAccepted = true;
          $location.search('fileUploadAgreementAccepted', true);
        });
      };

      if ($location.search().fileUploadSuccess) {
        $scope.uploadCompleted = true;
        $scope.uploadSuccess = true;
      }

      updateLastUpdateMessage();

    }
);
