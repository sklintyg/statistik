// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '',

        // testing framework to use (jasmine/mocha/qunit/...)
        frameworks: ['jasmine', 'sinon'],

        // list of files / patterns to load in the browser
        files: [
            // bower:js
            'src/main/webapp/bower_components/jquery/jquery.js',
            'src/main/webapp/bower_components/angular/angular.js',
            'src/main/webapp/bower_components/angular-route/angular-route.js',
            'src/main/webapp/bower_components/angular-cookies/angular-cookies.js',
            'src/main/webapp/bower_components/angular-sanitize/angular-sanitize.js',
            'src/main/webapp/bower_components/angular-i18n/angular-locale_sv-se.js',
            'src/main/webapp/bower_components/bootstrap/dist/js/bootstrap.js',
            'src/main/webapp/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'src/main/webapp/bower_components/bootstrap-multiselect/dist/js/bootstrap-multiselect.js',
            'src/main/webapp/bower_components/lodash/lodash.js',
            'src/main/webapp/bower_components/jquery.tablesorter/js/jquery.tablesorter.js',
            'src/main/webapp/bower_components/outdated-browser/outdatedbrowser/outdatedbrowser.min.js',
            'src/main/webapp/bower_components/pdfmake/build/pdfmake.js',
            'src/main/webapp/bower_components/momentjs/moment.js',
            'src/main/webapp/bower_components/dropzone/dist/min/dropzone.min.js',
            'src/main/webapp/bower_components/ngstorage/ngStorage.js',
            'src/main/webapp/bower_components/highcharts/highcharts.js',
            'src/main/webapp/bower_components/highcharts/modules/exporting.js',
            'src/main/webapp/bower_components/highcharts/highcharts-more.js',
            'src/main/webapp/bower_components/rgb-color/rgb-color.js',
            'src/main/webapp/bower_components/canvg-gabelerner/canvg.js',
            // endbower
            'src/main/webapp/bower_components/angular-mocks/angular-mocks.js',
            'src/main/webapp/app/app.main.js',
            'src/main/webapp/app/**/*.js',
            'src/main/webapp/components/**/*.js',
            'src/main/webapp/app/**/*.html',
            'src/main/webapp/components/**/*.html'
        ],

        // list of files / patterns to exclude
        /*exclude: [
            'src/main/webapp/app/app.main.js'
        ],*/

        preprocessors: {
            '**/*.jade': 'ng-jade2js',
            '**/*.html': 'html2js',
            '**/*.coffee': 'coffee',
            '**/*.js': ['coverage']
        },

        ngHtml2JsPreprocessor: {
            stripPrefix: 'src/main/webapp/',
            // the name of the Angular module to create
            moduleName: 'htmlTemplates'
        },

        ngJade2JsPreprocessor: {
            stripPrefix: 'src/main/webapp/'
        },

        // web server port
        port: 8080,

        // level of logging
        // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
        logLevel: config.LOG_INFO,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,


        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - PhantomJS
        // - IE (only Windows)
        browsers: ['Chrome'],


        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false
    });
};
