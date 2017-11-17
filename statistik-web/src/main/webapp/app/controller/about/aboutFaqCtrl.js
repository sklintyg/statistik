/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
    function ($scope, _) {
        'use strict';

        function getStatsQuestions() {
            var questions = [];

            questions.push({
                title: 'Vad innehåller Intygsstatistik?',
                closed: true,
                body: '<p>Innehållet i Intygsstatistik baseras främst på de elektroniska läkarintyg som utfärdas när en läkare ' +
                'bedömer att en individ behöver exempelvis sjukskrivas eller är i behov av aktivitetsersättning. ' +
                'Tjänsten innehåller information från samtliga elektroniska läkarintyg som utfärdats inom hälso- och sjukvården efter oktober 2013. ' +
                'Intygsstatistik registrerar inte utfallet av intyget, det vill säga vilken bedömning Försäkringskassan gör.</p>' +
                '<p>Utöver läkarintygen innehåller Intygsstatistik dessutom statistik över den ärendekommunikation som sker mellan intygsutförare och Försäkringskassan. ' +
                'Tjänsten innehåller information från samtliga meddelanden som har skickats elektroniskt som ett ärende från Försäkringskassan till intygsutföraren.</p>' +
                '<p>Utifrån intygsinformationen hämtar Intygsstatistik därtill mer information om läkaren som skrivit intyget och om vårdenheten som han eller hon arbetar på. ' +
                'Dessa uppgifter hämtas från HSA-katalogen och syftet är att kunna ta fram mer värdefull och intressant statistik.</p>'
            });

            questions.push({
                title: 'Vilka intyg filtreras bort från statistiken?',
                closed: true,
                body: '<p>Läkarintyg som inte motsvarar Intygsstatistiks kvalitetskrav filtreras bort och kommer inte med i några statistikrapporter. ' +
                        'Filtreringen görs för att öka kvaliteten i den resulterande statistiken.</p>' +
                '<p>Kraven som ställs på läkarintyg för att de ska kunna vara del av statistiken är:</p>' +
                '<ul>' +
                    '<li>Patientens födelsedatum i läkarintyget (utifrån personnummer eller samordningsnummer) måste motsvara ett riktigt datum.</li>' +
                    '<li>Intyget får inte vara makulerat.</li>' +
                    '<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste finnas i den nationella HSA-katalogen ' +
                        '(en elektronisk katalog som innehåller kvalitetssäkrade uppgifter om bland annat enheter och personer i hälso- och sjukvården).</li>' +
                    '<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste vara kopplad till en vårdgivare i den nationella HSA-katalogen.</li>' +
                '</ul>' +
                '<p>Ytterligare krav som ställs endast på Läkarintyg för sjukpenning är:</p>' +
                '<ul>' +
                    '<li>Startdatumet för sjukskrivningsperioden måste vara efter 2009-12-31.</li>' +
                    '<li>Start- eller slutdatum för sjukskrivningsperioden måste vara mindre än fem år fram i tiden.</li>' +
                    '<li>Slutdatum för sjukskrivningsperioden måste vara efter startdatumet.</li>' +
                '</ul>'
            });

            return questions;
        }

        function getReportQuestions() {
            var questions = [];

            questions.push({
                title: 'Hur skriver jag ut en rapport?',
                closed: true,
                body: '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
                'När du väljer "PDF" laddas ett pdf-dokument ner till din dator. Dokumentet innehåller rapportens diagram, tabell och uppgifter om eventuella filtreringar. ' +
                'När dokumentet laddats ner kan du öppna det och skriva ut det.</p>'
            });

            questions.push({
                title: 'Hur exporterar jag tabellen till Excel?',
                closed: true,
                body: '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
                'När du väljer ”Excel” laddas tabellen ner till din dator och går sedan att öppna i Excel.</p>'
            });

            return questions;
        }

        function getSicknessQuestions() {
            var questions = [];

            questions.push({
                title: 'Vad är ett sjukfall?',
                closed: true,
                body: '<p>Intygsstatistik omfattar ett sjukfall de elektroniska läkarintyg (FK 7263 och FK 7804) som utfärdats för en viss patient vid en ' +
                    'sjukskrivning och som följer på varandra med max fem dagars uppehåll. ' +
                    'Intygen måste även vara utfärdade av samma vårdgivare för att räknas till samma sjukfall. ' +
                    'Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare, så räknas det som två sjukfall.</p>' +
                    '<p>Exempel: Om intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti räknas de båda intygen till samma sjukfall. ' +
                    'Om intyg 2 istället hade varit giltigt från den 21 augusti skulle intygen ha räknats som två skilda sjukfall.</p>'
            });

            return questions;
        }

        function getTechnicalQuestions() {
            var questions = [];

            questions.push({
                title: 'Hur ofta uppdateras Intygsstatistik?',
                closed: true,
                body: '<p>Intygsstatistik uppdateras löpande med nya läkarintyg och meddelanden. Den nationella statistiken uppdateras vid varje månadsskifte. ' +
                'Statistiken för verksamhetsuppföljning som kräver inloggning uppdateras en gång per dygn.</p>'
            });

            questions.push({
                title: 'Vilka olika typer av behörighet finns för att logga in i Intygsstatistik?',
                closed: true,
                body: '<p>Den allmänna delen av Intygsstatistik som visar nationell statistik kräver inte någon registrering eller behörighet. ' +
                    'Om du har ansvar för verksamhetsuppföljning inom hälso- och sjukvården kan du ta del av statistik som är särskilt riktad till ditt uppföljningsområde. ' +
                    'Uppföljningsområde kan vara en eller flera vårdenheter, eller hela vårdgivaren om du har ett övergripande uppföljningsansvar. ' +
                    'Det krävs att du har ett medarbetaruppdrag för statistik som delas ut av din verksamhetschef och sätts av din HSA-administratör. För att kunna logga in och se statistik krävs:</p>' +
                    '<ul>' +
                        '<li>ett SITHS-kort med pinkod</li>' +
                        '<li>en kortläsare med tillhörande programvara, NetID</li>' +
                        '<li>ett medarbetaruppdrag i HSA som ger dig rätt att ta del av statistik</li>' +
                    '</ul>' +
                    '<p><span dynamiclink key="sarskildBehorighetStatistik"></span></p>'
            });

            return questions;
        }

        var faq = [];

        faq.push({
            title: 'Statistik',
            icon: 'fa-area-chart',
            questions: getStatsQuestions()
        });

        faq.push({
            title: 'Rapporter',
            icon: 'fa-file-text-o',
            questions: getReportQuestions()
        });

        faq.push({
            title: 'Sjukfall',
            icon: 'fa-stethoscope',
            questions: getSicknessQuestions()
        });

        faq.push({
            title: 'Tekniska frågor',
            icon: 'fa-wrench',
            questions: getTechnicalQuestions()
        });


        $scope.faq = faq;

        $scope.openAll = function() {
            toggleQuestions(false);
        };

        $scope.closeAll = function() {
            toggleQuestions(true);
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
