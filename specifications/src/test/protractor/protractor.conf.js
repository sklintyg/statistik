/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

// conf.js
/*globals browser,global,exports,Promise*/
 'use strict';
var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

var screenshotReporter = new HtmlScreenshotReporter({
    dest: 'reports',
    filename: 'index.html',
    ignoreSkippedSpecs: true,
    captureOnlyFailedSpecs: true
});

exports.config = {
    directConnect: true,
    rootElement: '#ng-app',

    seleniumAddress: require('./environment.js').envConfig.SELENIUM_ADDRESS,
    baseUrl: require('./environment.js').envConfig.ST_URL,

    specs: ['./dev/specs/**/*.spec.js'],

    // Capabilities to be passed to the webdriver instance.
    capabilities: {
        //shardTestFiles: true,
        //maxInstances: 3,
        'browserName': 'chrome',
        marionette: false
    },

    framework: 'jasmine2',
    jasmineNodeOpts: {
        // If true, print colors to the terminal.
        showColors: true,
        // Default time to wait in ms before a test fails.
        defaultTimeoutInterval: 3 * 60 * 1000
    },

    // Setup the report before any tests start
    beforeLaunch: function() {
        return new Promise(function(resolve){
            screenshotReporter.beforeLaunch(resolve);
        });
    },

    onPrepare: function() {
        global.isAngularSite = function(flag){
            browser.ignoreSynchronization = !flag;
        };

        global.logg = function(text){
            console.log(text);
        };

        browser.driver.manage().window().setSize(1280, 1024);
        //browser.driver.manage().window().setSize(1600, 1200);
        //browser.driver.manage().window().maximize();

        var reporters = require('jasmine-reporters');
        jasmine.getEnv().addReporter(
            new reporters.JUnitXmlReporter({
                savePath:'reports/',
                filePrefix: 'junit',
                consolidateAll:true})
        );

        jasmine.getEnv().addReporter(screenshotReporter);

        // Disable animations so e2e tests run more quickly
        var disableNgAnimate = function() {
            angular.module('disableNgAnimate', []).run(['$animate', function($animate) {
                console.log('Animations are disabled');
                $animate.enabled(false);
            }]);
        };

        var disableCssAnimate = function() {
            angular
                .module('disableCssAnimate', [])
                .run(function() {
                    var style = document.createElement('style');
                    style.type = 'text/css';
                    style.innerHTML = '* {' +
                        '-webkit-transition: none !important;' +
                        '-moz-transition: none !important' +
                        '-o-transition: none !important' +
                        '-ms-transition: none !important' +
                        'transition: none !important' +
                        '}';
                    document.getElementsByTagName('head')[0].appendChild(style);
                });
        };

        browser.addMockModule('disableNgAnimate', disableNgAnimate);
        browser.addMockModule('disableCssAnimate', disableCssAnimate);
    },

    // Close the report after all tests finish
    afterLaunch: function(exitCode) {
        return new Promise(function(resolve){
            screenshotReporter.afterLaunch(resolve.bind(this, exitCode));
        });
    }
};
