// Karma configuration
// Generated on Thu Oct 09 2014 12:29:43 GMT+0200 (CEST)

module.exports = function(config) {
    'use strict';

    var WEBJAR_DIR = 'build/webjars/META-INF/resources/webjars/';

    config.set({
        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '../../../',

        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            WEBJAR_DIR + 'angularjs/1.2.14/angular.js',
            WEBJAR_DIR + 'angularjs/1.2.14/angular-route.js',
            WEBJAR_DIR + 'angularjs/1.2.14/angular-cookies.js',
            WEBJAR_DIR + 'angularjs/1.2.14/angular-sanitize.js',
            WEBJAR_DIR + 'angularjs/1.2.14/angular-mocks.js',
            WEBJAR_DIR + 'angular-ui-bootstrap/0.10.0/ui-bootstrap-tpls.min.js',
            WEBJAR_DIR + 'underscorejs/1.7.0/underscore-min.js',
            'src/main/**/highcharts.js',
            'src/main/**/app.js',
            'src/main/**/messages.js',
            'src/main/**/messageService.js',
            'src/main/**/common.js',
            'src/main/**/treeMultiSelectUtil.js',
            'src/main/**/directiveTmsCtrl.js',
            'src/main/**/pageCtrl.js',
            'src/main/**/businessOverviewCtrl.js',
            'src/main/**/treeMultiSelectUtil.js',
            'src/main/**/businessFilter.js',
            'src/main/**/printFactory.js',
            'src/test/javascript/**/angular-mocks.js',
            'src/test/javascript/**/pageCtrlTest.js',
            'src/test/javascript/**/commonTest.js',
            'src/test/javascript/**/directiveTmsCtrlTest.js',
            'src/test/javascript/**/businessFilterTest.js',
            'src/test/javascript/**/printFactoryTest.js'
        ],

        // list of files to exclude
        exclude: [
        ],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
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
            'karma-mocha-reporter'
        ]
        
    });
};
