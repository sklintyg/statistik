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

'use strict';

angular.module('StatisticsApp').controller('aboutFaqCtrl', ['$scope',
    function ($scope) {

        var faq = [];

        faq.push({
            title: '1. Vad innebär Statistiktjänsten?',
            body: '<p>Statistiktjänsten för ordinerad sjukskrivning är en webbtjänst som gör det möjligt att se samlad statistik för ordinerad sjukskrivning från hela landet.</p>' +
                    '<p>Statistiken baseras på de läkarintyg (FK 7263) som skickas elektroniskt från hälso- och sjukvårdens journalsystem. Tjänsten består av två delar:</p>' +
                    '<ul>' +
                        '<li>statistik som är tillgänglig för alla</li>' +
                        '<li>statistik som kräver särskild behörighet</li>' +
                    '</ul>'
        });

        faq.push({
            title: '2. Vem står bakom Statistiktjänsten?',
            body: '<p>Det landstingsägda bolaget Inera AB har tagit fram tjänsten och förvaltar den på uppdrag av Sveriges kommuner och landsting.</p>'
        });

        faq.push({
            title: '3. Vad innehåller Statistiktjänsten?',
            body: '<p>Statistiktjänsten innehåller endast intygsinformation från hälso- och sjukvården. Innehållet baseras på det elektroniska läkarintyg som utfärdas när en läkare bedömer att en individ behöver sjukskrivas. Tjänsten innehåller information från samtliga elektroniska läkarintyg som utfärdats inom hälso- och sjukvården.</p>'+
                    '<p>Statistiktjänsten registrerar inte om intyget används för ansökan om sjukpenning hos Försäkringskassan, eller vilken bedömning Försäkringskassan gör. Statistiken baseras bara på hälso- och sjukvårdens ordinerade sjukskrivning.</p>'+
                    '<p>Alla geografiska data är baserade på var vårdenheten ligger.</p>'
        });

        faq.push({
            title: '4. Vad är nationell statistik?',
            body: '<p>Nationell statistik är statistik för alla elektroniska läkarintyg som utfärdats av landets olika vårdgivare. Statistiken avidentifieras och slås ihop innan den sammanställs på nationell nivå.</p>' +
                    '<p>För att statistiken inte ska kunna kopplas till enskilda personer filtreras den från uppgifter som skulle kunna röja en persons identitet. Det kan leda till en liten felmarginal i statistiken då det presenterade antalet sjukfall kan skilja sig något från det faktiska antalet, på grund av filtreringen.</p>'
        });

        faq.push({
            title: '5. Hur ofta uppdateras Statistiktjänsten?',
            body: '<p>Statistiktjänsten uppdateras löpande med nya läkarintyg. Den övervägande delen av statistiken som presenteras innehåller data fram till och med den senast avslutade kalendermånaden.</p>' +
                    '<p>Undantaget är rapporter för verksamhetsstatistik om pågående sjukskrivning, som endast visar pågående månads läkarintyg. Dessa rapporter kan bara ses i inloggat läge av användare inom hälso- och sjukvården.</p>'
        });
        faq.push({
            title: '6. Hur presenteras statistiken i Statistiktjänsten?',
            body: '<p>Statistiken presenteras i grafiska bilder över hur sjukskrivningarna fördelas på exempelvis:'+
                    '<ul>' +
                        '<li>Antal sjukfall</li>' +
                        '<li>Diagnosgrupp</li>' +
                        '<li>Patientens ålder</li>' +
                        '<li>Patientens kön</li>' +
                        '<li>Sjukskrivningslängd</li>' +
                        '<li>Sjukskrivningsgrad</li>' +
                    '</ul>' +
                    'Till varje graf finns även en tabell som innehåller bildens dataunderlag.' +
                    '</p>'
        });
        faq.push({
            title: '7. Vad innebär ett sjukfall?',
            body: '<p>Ett sjukfall omfattar alla de elektroniska läkarintyg som utfärdats för en viss patient vid en sjukskrivning och som följer på varandra med max fem dagars uppehåll.</p>' +
                    '<p>Exempel: Om intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti ses de båda intygen som samma sjukfall. Men om intyg 2 istället varit giltigt från den 21 augusti skulle intygen ha räknats som två skilda sjukfall.</p>'
        });
        faq.push({
            title: '8. Vad innebär pågående sjukfall?',
            body: '<p>Rapporten ”pågående sjukfall” visar så aktuell information om sjukfallen som möjligt. Alla sjukfall som varar någon gång under pågående månad ingår i rapporten. I slutet av månaden kommer därför även sjukfall som avslutats under månaden att visas som pågående sjukfall.</p>'
        });
        faq.push({
            title: '9. Vilka olika typer av behörighet finns för att se statistik i Statistiktjänsten?',
            body: '<p>Den allmänna delen av Statistiktjänsten som visar nationell statistik kräver inte någon registrering eller behörighet.</p>' +
                    '<p>Om du har ansvar för verksamhetsuppföljning inom hälso- och sjukvården kan du ta del av statistik som är särskilt riktad till ditt uppföljningsområde.</p>' +
                    '<p>Uppföljningsområde kan vara en eller flera vårdenheter, eller hela vårdgivaren om du har ett övergripande uppföljningsansvar. Det krävs att du har ett medarbetaruppdrag för statistik som delas ut av din verksamhetschef och sätts av din HSA-administratör.</p>' +
                    '<p>För att kunna logga in och se statistik för enskilda vårdenheter eller en hel vårdgivare krävs:</p>' +
                    '<ul>' +
                        '<li>ett SITHS-kort med pinkod</li>' +
                        '<li>en kortläsare med tillhörande programvara, NetID</li>' +
                        '<li>ett medarbetaruppdrag i HSA som ger dig rätt att ta del av statistik</li>' +
                    '</ul>' +
					'<p><a href="http://www.inera.se/sarskild_behorighet" target="_blank">Mer information om behörighet för Statistiktjänsten (extern länk)</a></p>' 
        });
        faq.push({
            title: '10. Hur exporterar jag tabellen till Excel?',
            body: '<p>På varje rapportsida finns en knapp med texten ”Spara/Skriv ut” ovanför diagrammet. När du väljer ”Spara tabell till Excel” laddas tabellen automatiskt ner på din dator och går att öppna i Excel.</p>'
        });
        faq.push({
            title: '11. Vilka webbläsare stöds av Statistiktjänsten?',
            body: '<p>Funktionerna i Statistiktjänsten är anpassade för webbläsaren Internet Explorer, version 9 eller senare.</p>' +
                    '<p>Du måste även ha JavaScript aktiverat i din webbläsare för att kunna använda Statistiktjänsten.</p>'
        });
        faq.push({
            title: '12. Varför ser jag ingen statistik för den valda verksamheten?',
            body: '<p>Det beror på att det i dagsläget inte finns inrapporterad data för verksamheten eller att du gjort ett för begränsat filterval.</p>'
        });

        
        $scope.faq = faq;
    }
]);