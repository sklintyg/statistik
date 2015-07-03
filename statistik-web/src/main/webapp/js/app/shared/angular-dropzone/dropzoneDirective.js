angular.module('dropzone', []).directive('dropzone', function () {
    return function (scope, element, attrs) {
        var config, dropzone;

        config = scope[attrs.dropzone];
        config.options.fallback = function () {
            document.getElementById('uploadZone').style.display="none";
            document.getElementById('fallbackUploadZone').style.display='block';
        };

        // create a Dropzone for the element with the given options
        dropzone = new Dropzone(element[0], config.options);

        // bind the given event handlers
        angular.forEach(config.eventHandlers, function (handler, event) {
            dropzone.on(event, handler);
        });

        dropzone.on("complete", function(file) {
            dropzone.removeFile(file);
        });
    };
});
