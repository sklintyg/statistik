# Statistik

## Introduktion
Systemet är logiskt uppdelat på två delsystem: Intygsstatistik och Statistikapplikationen. Därutöver finns mindra applikationer och stödsystem, t ex statistik-gatling för att göra lasttester och HSA fileservice för att hämta filer från HSA.

### Intygsstatistik
Intygsstatistik beräknar statistik utifrån given rådata och gör denna statistik tillgängligt via ett api.

### Statistikapplikationen
Statistikapplikationen hanterar GUI och inloggning. Kommunicerar med intygsstatistik för att hämta aktuell statistik.

Det finns två olika typer av användare på statistikapplikationen:

+ Ej inloggad användare som får tillgång till statistik på nationell nivå.
+ Inloggad användare från vårdenhet som både har tillgång till statistiken för sin enhet samt den övergripande på nationell nivå.

### HSA fileservice
HSA fileservice hämta en lista över sjukvårdsenheter från HSA och uppdaterar intygsstatistik med aktuella enhatsnamn. Listan som hämtas uppdateras varje dygn, så det finns ingen anledning att köra applikationen oftare än så.

## Komma igång med lokal installation

Den här sektionen beskriver hur man bygger Inera Statistics för att kunna köras helt fristående.

Vi använder Gradle för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

    git clone https://github.com/sklintyg/statistik.git
    
Läs vidare i gemensam dokumentation [devops/develop README-filen](https://github.com/sklintyg/devops/tree/release/2021-1/develop/README.md)

## Omkörning av statistiken

Beskrivning finns i [Omprocessning av intygsstatistik](https://inera.atlassian.net/wiki/spaces/FI/pages/40042500/Omprocessning+av+intygsstatistik)

## Köhanterare
ActiveMQ används för att ta emot sjukintyg. I koden är köhanteringen inte bunden till ActiveMQ, utan det bör gå att byta till någon annan köhanterare genom konfigurationsändringar.

## Test
Vi använder tre typer av tester, Fitnesse/Slim, JUnit, Jasmine.

### JUnit
JUnit används för enhetstester, funktionestester samt integrationstester. Normalt anses en testklass innehålla vanliga
enhetstester, Gradle kör dessa tester per default. Avslutas klassnamnet med IntegrationTest? så är det ett integrationstest, och
Gradle kör bara dessa tester om man specifikt säger till (integrationTests). Funktionella tester, dvs klasser som avslutas med FunctionalTest? körs aldrig från Gradle, utan måste körast manuellt från en IDE eller dylikt.

### Fitnesse/Slim
Det finns en separat modul som använder Fitnesse, test. Fitnesse kan antingen köras som automattest eller som en wiki. I wikiläge startas en webserver på http://localhost:9125/StatisticsTests , och surfar man dit kan man skapa, redigera och köra individuella tester. I automatläge körs alla tester igenom och en rapport skapas (ungefär som motsvarande för JUnit-tester).

#### statistics-specification
Den här modulen testar end-to-end-scenarior, där man kontrollerar att instoppade intyg ger korrekt statistik. Innan man kör måste
man ha en webserver som snurrar.

### Jasmine/Karma
Används för Javascripttester

## Spring
Spring-konfigurationen är lite utspridd, men det går att nysta upp om man utgår från web.xml, leta efter:

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/application-context.xml</param-value>
    </context-param>

Vi nyttjar springprofiler för att styra konfiguration vid uppstart. I exemplet ovan användes

-Dspring.profiles.active=dev,embedded

som talar om att dev-profilen med en inbäddad databas ska användas.

De profiler som finns är:

|Profilnamn     |Beskrivning|
|---------------|-----------|
|dev            |starta applikationen i utvecklingsläge|
|caching-enabled|profil från infra som aktiverar redis-cache, krävs för att aktivera job schedulers och ta emot inkommande intyg etc.|
|test           |profil som använder extern redis ska användas tillsammans med dev och caching-enabled|
|hsa-stub       |gå inte mot hsa, utan använd en stub istället|
|security-fake  |stöd enbart simulerad inloggning|
|security-both  |stöd saml-inloggning och simulerad inloggning|
|security-saml  |stöd enbart saml-inloggning|
|hsacached      |Cachar hsa-anrop för att ej överlasta hsa vid omprocessning|
|testapi        |Aktiverar REST-interface som enbart används för testning, tex möjlighet att sätta klockan eller rensa intyg |

## Namngivning av klasser och metoder
I projektets inledning användes engelska vid namngivning, men vi har sedan dess gått över till svengelska (enligt nedanstående förklaring). Det kan finnas kvar rester av engelska, men det byts ut när vi upptäcker det.

Att översätta facktermer är inte lätt, subtila betydelseskiftningar påverkar tolkningen, och det är lätt hänt att man vid kommunikation översätter tillbaka till en annan benämning än den ursprungliga.

Vi försöker därför använda samma konsekventa terminologi inom ett område, för hela spektrat av intressenter. Det betyder att vi behöver använda svenska för verksamhetsspecifika termer, men också att engelska bör användas för tekniska termer. Vi har också bestämt oss för att pidginisera koden, dvs inte försöka blanda korrekt svensk och engelsk grammatik utan använda väldefinierade pluralformer (sjukfalls för flera sjukfall t ex) osv. Vi använder endast ASCII i koden, ä och å blir a, och ö blir o.

## JMX
Vi exponerar vissa klasser via JMX.

Vi använder Springs jmx-stöd, konfigurationen finns i application-context-jmx.

Det har inte gjorts något särskilt anpassningsarbete, utan bara helt rått exponerat klasser med metoder som vi vill komma åt. Om vi ska fortsätta använda JMX bör vi städa upp här och skapa särskilda klasser/interface där vi enbart exponerar de metoder vi vill, på ett format som vi vill ha.

## Web services felsvar
Det enda ställena som accepterar indata, är när man hämtar diagnosinformation (diagnoskapitel) samt när man vill se verksamhetsspecifik infromation (verksamhetsid).

### Diagnoskapitel
Skickar man in ett icke existerande diagnoskapitel får man tillbaka ett tomt dataset. Svaret har http status 200 OK.

### Verksamhetsid
Försöker man komma åt en verksamhet som man saknar behörighet till, så returneras http status 403 Access Denied. Detta oavsett om verksamhetsid:t existerar eller inte.

### Icke existerande URL:er
404 Not Found

## Licens
Copyright (C) 2021 Inera AB (http://www.inera.se)

Statistik is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Statistik is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.

Se även [LICENSE](LICENSE.txt). 