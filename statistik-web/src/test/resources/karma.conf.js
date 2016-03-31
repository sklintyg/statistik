// Karma configuration
// Generated on Thu Oct 09 2014 12:29:43 GMT+0200 (CEST)

module.exports = function(config) {
    'use strict';

    var WEBJAR_DIR = 'build/webjars/META-INF/resources/webjars/';
    var angularVersion = '1.2.29';
    config.set({
        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '../../../',

        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine', 'sinon'],

        // list of files / patterns to load in the browser
        files: [
            WEBJAR_DIR + 'jquery/1.10.2/jquery.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/angular.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/angular-route.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/angular-cookies.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/angular-sanitize.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/angular-mocks.js',
            WEBJAR_DIR + 'angularjs/' + angularVersion + '/i18n/angular-locale_sv.js',
            WEBJAR_DIR + 'angular-ui-bootstrap/0.12.1/ui-bootstrap-tpls.min.js',
            WEBJAR_DIR + 'lodash/3.10.1/lodash.min.js',
            WEBJAR_DIR + 'momentjs/2.10.3/moment.js',
            WEBJAR_DIR + 'pdfmake/0.1.20/build/pdfmake.min.js',
            WEBJAR_DIR + 'pdfmake/0.1.20/build/vfs_fonts.js',
            'src/main/webapp/js/**/*.js',
            'src/test/javascript/**/*.js',
            '**/*.html'
        ],

        // list of files to exclude
        exclude: [
        ],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            '**/*.html': ['ng-html2js']
        },

        ngHtml2JsPreprocessor: {
            // If your build process changes the path to your templates,
            // use stripPrefix and prependPrefix to adjust it.
            stripPrefix: "src/main/webapp/",

            // the name of the Angular module to create
            moduleName: 'htmlTemplates'
        },

        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress'],

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,

        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['Chrome'],

        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,

        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: false,

        plugins: [
            'karma-jasmine',
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-phantomjs-launcher',
            'karma-mocha-reporter',
            'karma-ng-html2js-preprocessor',
            'karma-sinon'
        ]
        
    });
};
