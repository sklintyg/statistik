/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('aboutFaqCtrl',
    function ($scope, _, messageService, smoothScroll, $window) {
        'use strict';

        function getQuestions(prefix, idPrefix) {
            var questions = [];
            var numberOfQuestions = 1;

            while(hasQuestion(prefix, numberOfQuestions)) {
                questions.push({
                    id: 'faq-' + idPrefix + '-' + numberOfQuestions,
                    title: prefix + numberOfQuestions + '.title',
                    closed: true,
                    body: prefix + numberOfQuestions + '.body'
                });

                numberOfQuestions++;
            }

            return questions;
        }

        function hasQuestion(prefix, index) {
            var key = prefix + index + '.title';

            return messageService.propertyExists(key);
        }

        var faq = [];

        faq.push({
            title: 'Statistik',
            icon: 'fa-area-chart',
            questions: getQuestions('faq.stats.', 'stats')
        });

        faq.push({
            title: 'Rapporter',
            icon: 'fa-file-text-o',
            questions: getQuestions('faq.report.', 'report')
        });

        faq.push({
            title: 'Sjukfall',
            icon: 'fa-stethoscope',
            questions: getQuestions('faq.sickness.', 'sickness')
        });

        faq.push({
            title: 'Tekniska frågor',
            icon: 'fa-wrench',
            questions: getQuestions('faq.technical.', 'technical')
        });


        $scope.faq = faq;

        $scope.openAll = function() {
            toggleQuestions(false);
        };

        $scope.closeAll = function() {
            toggleQuestions(true);
        };

        $scope.toggleQuestion = function(question) {
            question.closed = !question.closed;

            if (!question.closed) {
                var elementToScrollTo = $('#' + question.id);

                var windowElement = $($window);
                var windowHeight = windowElement.height() / 2;
                var scrollTop = windowElement.scrollTop();
                var elementPostion = elementToScrollTo.offset().top;

                if (elementPostion - scrollTop > windowHeight) {
                    var offset = 100;
                    var options = {
                        duration: 500,
                        easing: 'easeInOutQuart',
                        offset: offset
                    };

                    //scroll to this questions panel heading, centered vertically
                    smoothScroll(elementToScrollTo[0], options);
                }
            }
        };

        function toggleQuestions(closed) {
            _.each(faq, function(category) {
                _.each(category.questions, function(question) {
                    question.closed = closed;
                });
            });
        }
    }
);
