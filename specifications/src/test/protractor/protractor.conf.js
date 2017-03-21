/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
/*globals browser,global,exports*/
 'use strict';
var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');

exports.config = {
    rootElement: '#ng-app',

    seleniumAddress: require('./environment.js').envConfig.SELENIUM_ADDRESS,
    baseUrl: require('./environment.js').envConfig.ST_URL,

    specs: ['./dev/specs/**/*.spec.js'],

    // Capabilities to be passed to the webdriver instance.
    capabilities: {
        //shardTestFiles: true,
        //maxInstances: 3,
        'browserName': 'firefox',
        marionette: false
    },

    framework: 'jasmine2',
    jasmineNodeOpts: {
        // If true, print colors to the terminal.
        showColors: true,
        // Default time to wait in ms before a test fails.
        defaultTimeoutInterval: 3 * 60 * 1000
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

        jasmine.getEnv().addReporter(
            new HtmlScreenshotReporter({
                dest: 'reports',
                filename: 'index.html',
                cleanDestination: false,
                showSummary: false,
                showConfiguration: false,
                reportTitle: null
            })
        );

        var SpecReporter = require('jasmine-spec-reporter');
        jasmine.getEnv().addReporter(new SpecReporter({
            displayStacktrace: 'none',      // display stacktrace for each failed assertion, values: (all|specs|summary|none)
            displaySuccessesSummary: false, // display summary of all successes after execution
            displayFailuresSummary: true,   // display summary of all failures after execution
            displayPendingSummary: true,    // display summary of all pending specs after execution
            displaySuccessfulSpec: true,    // display each successful spec
            displayFailedSpec: true,        // display each failed spec
            displayPendingSpec: false,      // display each pending spec
            displaySpecDuration: false,     // display each spec duration
            displaySuiteNumber: false,      // display each suite number (hierarchical)
            colors: {
                success: 'green',
                failure: 'red',
                pending: 'yellow'
            },
            prefixes: {
                success: '✓ ',
                failure: '✗ ',
                pending: '* '
            },
            customProcessors: []
        }));
    }
};
