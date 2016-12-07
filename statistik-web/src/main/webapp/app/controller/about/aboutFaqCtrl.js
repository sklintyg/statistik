/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('aboutFaqCtrl', ['$scope',
    function ($scope) {
        'use strict';

        var faq = [];

        faq.push({
            title: '1. Vad är Statistiktjänsten?',
            body: '<p>Statistiktjänsten för ordinerad sjukskrivning är en webbtjänst som gör det möjligt att se ' +
                        'samlad statistik för ordinerad sjukskrivning från hela landet.</p>' +
                    '<p>Statistiken baseras på de läkarintyg (FK 7263) som skickas elektroniskt från hälso- och sjukvårdens journalsystem. ' +
                            'Tjänsten består av två delar:</p>' +
                    '<ul>' +
                        '<li>nationell statistik som är tillgänglig för alla</li>' +
                        '<li>statistik för verksamhetsuppföljning som kräver särskild behörighet</li>' +
                    '</ul>'
        });
        faq.push({
            title: '2. Vem står bakom Statistiktjänsten?',
            body: '<p>Det landstingsägda bolaget Inera AB har tagit fram tjänsten och ' +
                        'förvaltar den på uppdrag av Sveriges kommuner och landsting (SKL).</p>'
        });
        faq.push({
            title: '3. Vad innehåller Statistiktjänsten?',
            body: '<p>Innehållet i Statistiktjänsten baseras på det elektroniska läkarintyg som utfärdas när en läkare bedömer att en individ behöver sjukskrivas. ' +
						'Tjänsten innehåller information från samtliga elektroniska läkarintyg som utfärdats inom hälso- och sjukvården sedan oktober 2013.</p>' +
                    '<p>Statistiktjänsten registrerar inte om intyget används för ansökan om sjukpenning hos Försäkringskassan, ' +
                        'eller vilken bedömning Försäkringskassan gör.</p>' +
					'<p>Utifrån intygsinformationen hämtar Statistiktjänsten dessutom mer information om läkaren som skrivit intyget och om vårdenheten som han eller ' +
						'hon arbetar på. Dessa uppgifter hämtas från HSA-katalogen och syftet är att kunna ta fram mer värdefull och intressant statistik.</p>'
        });
		faq.push({
            title: '5. Vad är ett sjukfall?',
            body: '<p>I Statistiktjänsten omfattar ett sjukfall alla de elektroniska läkarintyg som utfärdats för en viss patient vid en sjukskrivning ' +
                        'och som följer på varandra med max fem dagars uppehåll. Intygen måste även vara utfärdade av samma vårdgivare för att räknas till ' +
						'samma sjukfall. Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare, så räknas det som ' +
						'två sjukfall.</p>' +
                    '<p>Exempel: Om intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti ' +
                        'räknas de båda intygen till samma sjukfall. ' +
                        'Om intyg 2 istället hade varit giltigt från den 21 augusti skulle intygen ha räknats som två skilda sjukfall.</p>'
        });
		faq.push({
            title: '4. Vilka intyg filtreras bort från statistiken?',
            body: '<p>Läkarintyg som inte motsvarar Statistiktjänstens kvalitetskrav filtreras ' +
						'bort och kommer inte med i några statistikrapporter. Filtreringen görs för att öka kvaliteten i den resulterande statistiken.</p>' +
					'<p>Kraven som ställs på läkarintyg för att de ska kunna vara del av statistiken är:</p>' +
					'<ul>' +
						'<li>Patientens födelsedatum i läkarintyget (utifrån personnummer eller samordningsnummer) måste motsvara ett riktigt datum.</li>' +
						'<li>Startdatumet för sjukskrivningsperioden måste vara efter 2009-12-31.</li>' +
						'<li>Start- eller slutdatum för sjukskrivningsperioden måste vara mindre än fem år fram i tiden.</li>' +
						'<li>Slutdatum för sjukskrivningsperioden måste vara efter startdatumet.</li>' +
						'<li>Intyget får inte vara makulerat.</li>' +
						'<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste finnas i HSA-katalogen.</li>' +
						'<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste vara kopplad till en vårdgivare i HSA-katalogen.</li>' +
					'</ul>'
        });
        faq.push({
            title: '6. Vad är nationell statistik?',
            body: '<p>Nationell statistik är statistik för alla elektroniska läkarintyg som utfärdats av landets olika vårdgivare. ' +
                        'Statistiken avidentifieras och lämnas ut från respektive vårdgivare för att sammanställas på nationell nivå.</p>' +
                    '<p>För att statistiken inte ska kunna kopplas till enskilda personer så appliceras tröskelvärden i den nationella statistiken. ' +
						'Tröskelvärden innebär att om en vårdgivare har färre än fem sjukfall i en viss grupp räknas inte dessa sjukfall in i statistiken. ' +
                        'Det kan leda till en liten felmarginal i statistiken då det presenterade antalet sjukfall ' +
                        'kan vara något lägre än det faktiska antalet på grund av att vissa sjukfall kan ha filtrerats bort.</p>'
        });
        faq.push({
            title: '7. Hur ofta uppdateras Statistiktjänsten?',
            body: '<p>Statistiktjänsten uppdateras löpande med nya läkarintyg. Den nationella statistiken uppdateras vid varje månadsskifte. ' +
						'Statistiken för verksamhetsuppföljning som kräver inloggning uppdateras en gång per dygn.</p>'
        });
        faq.push({
            title: '8. Vilken behörighet krävs för att logga in i Statistiktjänsten?',
            body: '<p>Den allmänna delen av Statistiktjänsten som visar nationell statistik kräver inte någon registrering eller behörighet.</p>' +
                    '<p>Om du har ansvar för verksamhetsuppföljning inom hälso- och sjukvården kan du ta del av statistik ' +
                        'som är särskilt riktad till ditt uppföljningsområde.</p>' +
                    '<p>Uppföljningsområde kan vara en eller flera vårdenheter, eller ' +
                        'hela vårdgivaren om du har ett övergripande uppföljningsansvar. ' +
                        'Det krävs att du har ett medarbetaruppdrag för statistik som delas ut av din verksamhetschef och ' +
                        'sätts av din HSA-administratör.</p>' +
                    '<p>För att kunna logga in och se statistik krävs:</p>' +
                    '<ul>' +
                        '<li>ett SITHS-kort med pinkod</li>' +
                        '<li>en kortläsare med tillhörande programvara, NetID</li>' +
                        '<li>ett medarbetaruppdrag i HSA som ger dig rätt att ta del av statistik</li>' +
                    '</ul>' +
					'<p><a href="http://www.inera.se/sarskild_behorighet" target="_blank">' +
                        'Mer information om behörighet för Statistiktjänsten (extern länk)</a></p>'
        });
		faq.push({
            title: '9. Hur skriver jag ut en rapport?',
            body: '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
                    'När du väljer "PDF" laddas ett pdf-dokument ner till din dator. Dokumentet innehåller rapportens diagram, tabell och ' +
					'uppgifter om eventuella filtreringar. När dokumentet laddats ner kan du öppna det och skriva ut det.</p>'
        });
        faq.push({
            title: '10. Hur exporterar jag tabellen till Excel?',
            body: '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
                    'När du väljer ”Excel” laddas tabellen ner till din dator och går sedan att öppna i Excel.</p>'
        });
        faq.push({
            title: '11. Varför ser jag ingen statistik för den valda verksamheten?',
            body: '<p>Det beror på att det i dagsläget inte finns inrapporterad data för verksamheten eller ' +
                    'att du gjort ett för begränsat filterval.</p>'
        });

        $scope.faq = faq;
    }
]);
