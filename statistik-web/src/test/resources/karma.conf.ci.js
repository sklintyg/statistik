// Karma configuration
// Generated on Thu Oct 09 2014 12:29:43 GMT+0200 (CEST)

var baseConfig = require('./karma.conf.js');

module.exports = function (config) {
    // Load base config
    baseConfig(config);

    config.set({
        autoWatch: false,
        logLevel: config.LOG_ERROR,
        singleRun: true,

        browsers: [ 'PhantomJS' ],

        plugins: [
            'karma-jasmine',
            'karma-junit-reporter',
            'karma-phantomjs-launcher',
            'karma-mocha-reporter',
            'karma-ng-html2js-preprocessor',
            'karma-sinon'
        ],

        reporters: [ 'dots', 'junit' ],

        junitReporter: {
            outputFile: 'target/surefire-reports/TEST-karma-test-results.xml'
        }
        
    });
};
