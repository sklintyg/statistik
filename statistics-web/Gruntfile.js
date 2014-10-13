/* global module */
module.exports = function(grunt) {
    'use strict';

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-karma');

    var SRC_DIR = 'src/main/webapp/js/app/';
    var TEST_DIR = 'src/test/javascript/';

    var statisticsweb = [SRC_DIR + '**/*.js'];

    grunt.initConfig({

        jshint: {
            statisticsweb: {
                options: {
                    jshintrc: 'src/test/resources/jshintrc',
                    force: true
                },
                src: [ SRC_DIR + '**/*.js', TEST_DIR + '**/*.js', "!src/main/webapp/js/app/css3-mediaqueries.js" ]
            }
        },

        karma: {
            statisticsweb: {
                configFile: 'src/test/resources/karma.conf.ci.js',
                reporters: [ 'mocha' ]
            }
        },

    });

    grunt.registerTask('default', [ 'jshint' ]);
    grunt.registerTask('test', [ 'karma' ]);
};
