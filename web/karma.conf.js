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

// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {
  'use strict';

  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      // bower:js
      'src/main/webapp/bower_components/jquery/dist/jquery.js',
      'src/main/webapp/bower_components/angular/angular.js',
      'src/main/webapp/bower_components/angular-route/angular-route.js',
      'src/main/webapp/bower_components/angular-cookies/angular-cookies.js',
      'src/main/webapp/bower_components/angular-sanitize/angular-sanitize.js',
      'src/main/webapp/bower_components/angular-animate/angular-animate.js',
      'src/main/webapp/bower_components/angular-i18n/angular-locale_sv-se.js',
      'src/main/webapp/bower_components/bootstrap-sass/assets/javascripts/bootstrap.js',
      'src/main/webapp/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      'src/main/webapp/bower_components/bootstrap-multiselect/dist/js/bootstrap-multiselect.js',
      'src/main/webapp/bower_components/outdated-browser/outdatedbrowser/outdatedbrowser.min.js',
      'src/main/webapp/bower_components/lodash/lodash.js',
      'src/main/webapp/bower_components/rgb-color/dist/rgb-color.js',
      'src/main/webapp/bower_components/canvg-canvg/dist/browser/canvg.js',
      'src/main/webapp/bower_components/momentjs/moment.js',
      'src/main/webapp/bower_components/dropzone/dist/min/dropzone.min.js',
      'src/main/webapp/bower_components/ngstorage/ngStorage.js',
      'src/main/webapp/bower_components/highcharts/highcharts.js',
      'src/main/webapp/bower_components/highcharts/modules/exporting.js',
      'src/main/webapp/bower_components/highcharts/modules/offline-exporting.js',
      'src/main/webapp/bower_components/highcharts/highcharts-more.js',
      // endbower
      'src/main/webapp/bower_components/angular-mocks/angular-mocks.js',
      'src/main/webapp/app/app.main.test.js',
      'src/main/webapp/app/app.constant.test.js',
      'src/main/webapp/app/**/*.js',
      'src/main/webapp/components/**/*.js',
      'src/main/webapp/app/**/*.html',
      'src/main/webapp/components/**/*.html'
    ],

    // list of files / patterns to exclude
    exclude: [
      'src/main/webapp/app/app.main.js',
      'src/main/webapp/app/app.constant.js'
    ],

    reporters: ['progress', 'coverage'],

    preprocessors: {
      'src/main/webapp/!(bower_components)/**/*.html': ['ng-html2js'],
      'src/main/webapp/!(bower_components)/**/!(*spec).js': ['coverage']
    },

    coverageReporter: {
      type: 'html',
      dir: 'build/karma/coverage/'
    },

    ngHtml2JsPreprocessor: {
      stripPrefix: 'src/main/webapp', // don't strip trailing slash because we're using absolute urls and need it when matching templates
      // the name of the Angular module to create
      moduleName: 'htmlTemplates'
    },

    // web server port
    port: 47652,

    // Increase timeout
    browserNoActivityTimeout: 30000,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,

    client: {
      jasmine: {
        random: false
      }
    },

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: ['ChromeHeadlessCI'],
    customLaunchers: {
      ChromeHeadlessCI: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox']
      }
    },

    plugins: [
      'karma-chrome-launcher',
      'karma-coverage',
      'karma-jasmine',
      'karma-ng-html2js-preprocessor'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false
  });
};
