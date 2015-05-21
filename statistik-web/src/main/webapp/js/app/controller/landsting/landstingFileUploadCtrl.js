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

'use strict';

angular.module('StatisticsApp').controller('landstingFileUploadCtrl', [ '$scope', '$rootScope',
    function ($scope, $rootScope) {

        var updateStatus = function (response) {
            $scope.uploadResultMessage = response.message ? response.message : response;
            $scope.parsedRows = response.parsedRows;
        };

        $scope.dropzoneConfig = {
            'options': {
                url: 'api/verksamhet/landsting/fileupload',
                parallelUploads: 1,
                maxFilesize: 10, //MB
                paramName: "file",
                uploadMultiple: false,
                addRemoveLinks: false,
                createImageThumbnails: false,
                maxFiles: 1,
                acceptedFiles: ".xls,.xlsx",
                autoProcessQueue: true,
                clickable: true,
                previewTemplate: '<div id="preview-template" style="display: none;"></div>'
            },
            'eventHandlers': {
                'success': function (file, response) {
                    $scope.$apply(updateStatus(response));
                },
                'error': function (file, response) {
                    $scope.$apply(updateStatus(response));
                }
            }
        };

    }
]);
