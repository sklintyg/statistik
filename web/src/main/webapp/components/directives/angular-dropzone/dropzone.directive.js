/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

/* globals Dropzone */
angular.module('dropzone', []).directive('dropzone', function () {
    'use strict';

    return function (scope, element, attrs) {
        var config, dropzone;

        config = scope[attrs.dropzone];
        config.options.fallback = function () {
            document.getElementById('uploadZone').style.display='none';
            document.getElementById('fallbackUploadZone').style.display='block';
        };

        // create a Dropzone for the element with the given options
        dropzone = new Dropzone(element[0], config.options);

        // bind the given event handlers
        angular.forEach(config.eventHandlers, function (handler, event) {
            dropzone.on(event, handler);
        });

        dropzone.on('complete', function(file) {
            dropzone.removeFile(file);
        });
    };
});
