#statistik
#statistik
##Introduktion
Systemet är logiskt uppdelat på två delsystem: Statistiktjänsten och Statistikapplikationen. Därutöver finns mindra applikationer och stödsystem, t ex statistik-gatling för att göra lasttester och HSA fileservice för att hämta filer från HSA.
###Statistiktjänsten
Statistiktjänsten beräknar statistik utifrån given rådata och gör denna statistik tillgängligt via ett api.
###Statistikapplikationen
Statistikapplikationen hanterar GUI och inloggning. Kommunicerar med statistiktjänsten för att hämta aktuell statistik.

Det finns två olika typer av användare på statistikapplikationen:

+ Ej inloggad användare som får tillgång till statistik på nationell nivå.
+ Inloggad användare från vårdenhet som både har tillgång till statistiken för sin enhet samt den övergripande på nationell nivå. 

###HSA fileservice
HSA fileservice hämta en lista över sjukvårdsenheter från HSA och uppdaterar statistiktjänsten med aktuella enhatsnamn. Listan som hämtas uppdateras varje dygn, så det finns ingen anledning att köra applikationen oftare än så.

##Komma igång med lokal installation
Den här sektionen beskriver hur man bygger Inera Statistics för att kunna köras helt fristående.

Vi använder Gradle, för närvarande version 2.13, för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

    git clone git@github.com:sklintyg/statistik.git

Efter att man har klonat repository navigera till den klonade katalogen och kör följande kommando:

    gradle clean build

Det här kommandot kommer att bygga samtliga moduler i systemet. Om man inte vill köra testerna vid bygge använder man sig av
assemble istället för build

    gradle clean assemble

Nu ska det gå att starta applikationen med:

    gradle appRun

Nu går det att öppna en webbläsare och surfa till http://localhost:8080/ Observera jetty körs i gradleprocessen, så gradle "blir
inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.

###Uppgradera från node 0.12
Installera nodjs 6.6.0 tex med NVM, https://github.com/creationix/nvm

Har du nvm så kan du skriva 

    nvm install 6.6
    nvm alias default 6.6
    
Ta bort `statistik-web/node_modules` och `statistik-web/src/main/webapp/bower_components`

Följ anvisningarna i "Bygga klienten utanför gradle"

###Bygga klienten utanför gradle
Installera nodjs 6.5.0 tex med NVM, https://github.com/creationix/nvm

Installera grunt och bower

    npm install -g grunt-cli bower

Gå in i statistk-web och kör

    npm install
    bower install

För att starta en lokal grunt server  kör:

    grunt serve

Då öppnas sidan med adressen http://localhost:9095/
OBS att jetty behöver vara igång samtidigt för att sidan ska fungera.

För att köra hela bygget

    grunt build

För att testa applikationen i ett mer prodlikt läge kan man även starta med en flagga för att köra i minifierat, läge då css/js är packade och sammanslagna, genom att starta:

    gradle clean appRunWar -Pstatistik.useMinifiedJavaScript

##Protractor

Köra protractor tester lokalt. Du behöver ha firefox version 46 eller äldre för att testerna ska fungera.

 * Starta statistiktjänsten med `gradle appRun`
 * Gå till /specifications
 * Installera beroenden: `npm install`
 * Starta testerna: `npm test`

##Gradle
Vi använder Gradle för att bygga, test, installera och köra statistiktjänsten. Gradle spottar ur sig ganska mycket text, generellt sett har det gått bra om det sista som skrivs ut är något i stil med:

    BUILD SUCCESSFUL

    Total time: 1 mins 4.842 secs

|Några vanliga gradlekommandon|||
|--------------|---------|---------|
|./gradlew clean build integrationTests|bygg om hela projektet inklusive integrationstester|[projektrot]|
|./gradlew licenseFormatMain licenseFormatTest|Lägg till licens-header till alla filer som saknar header|[projektrot]|
|../gradlew appRunDebug|kör webbservern i debugläge|[projektrot]/statistik-web|
|../gradlew fitnesseWiki |starta fitnesse|[projektrot]/specifications|
|../gradlew fitnesseTest|kör fitnesse-tester|[projektrot]/specifications|

## Köra med intygstjänsten
Starta active mq

    activemq console

Starta statistiktjänsten, kommer att starta på port 9101

    ./gradlew appRun -PrunWithIntyg -PrunWithActiveMQ
    
### Intygstjänsten
Öppna jetty-web.xml  
Ändra jms/Queue till statistik.utlatande.queue  
Ändra jms/ConnectionFactory till "tcp://localhost:61616"

Starta intygstjänsten
    
    ./gradlew appRun
    
Nu kommer intygen som skickas till intygstjänsten även gå till statistik, det samma gäller för meddelanden som skickas till sendMessageToCare

## Licenser

Vi använder en gradle-plugin som kontrollerar att alla relevanta filer har en korrekt licens-header. Om den hittar en fil som inte
uppfyller detta krav så fallerar bygget. För att lägga till licens-header kör man

    gradle licenseFormatMain licenseFormatTest

##Releasebyggen

##Liquibase
Liquibase används för att skapa och underhålla underliggande databas. Alla ändringar av databasen måste reflekteras i liquibase-script. Vi använder H2 under utveckling och MySql i andra sammanhang, så scripten måste fungera för båda dessa alternativ.
###MySql
Liquibase kontrollerar databasen varje gång applikationen startas, och startar inte om databasversionen inte stämmer med applikationen.

Skapa/Uppdatera databasen görs med en separat liquibase-runner. Se DatabasUppdatering.
###H2 embedded
Liquibase kör, och modifierar vid behov databasen, varje gång applikationen startas.

## Liquibase för externa miljöer

För miljöer som utvecklingsteamet inte rår över behövs ett script som kan köra liquibase-förändringarna för en viss miljö. För att
skapa ett sådant script kör man

    gradle distZip

Då skapas en zip-fil i [projektrot]/tools/liquibase-runner/build/distributions. Denna zip-fil kan distribueras till lämplig driftsoperatör. För at sedan köra scriptet packar man upp zip-filen, går ner i den
katalog som skapats, och kör:

    ./bin/liquibase-runner --url=jdbc:mysql://localhost/statistik --username=statistik --password=statistik update
  
Självklart behöver parametrarna "url", "username" och "password" ändras för att passa den aktuella miljön.

##Köhanterare
ActiveMQ används för att ta emot sjukintyg. I koden är köhanteringen inte bunden till ActiveMQ, utan det bör gå att byta till någon annan köhanterare genom konfigurationsändringar.

##Test
Vi använder tre typer av tester, Fitnesse/Slim, JUnit, Jasmine.

###JUnit
JUnit används för enhetstester, funktionestester samt integrationstester. Normalt anses en testklass innehålla vanliga
enhetstester, Gradle kör dessa tester per default. Avslutas klassnamnet med IntegrationTest? så är det ett integrationstest, och
Gradle kör bara dessa tester om man specifikt säger till (integrationTests). Funktionella tester, dvs klasser som avslutas med FunctionalTest? körs aldrig från Gradle, utan måste körast manuellt från en IDE eller dylikt.
###Fitnesse/Slim
Det finns en separat modul som använder Fitnesse, specifications. Fitnesse kan antingen köras som automattest eller som en wiki. I wikiläge startas en webserver på http://localhost:9125/StatisticsTests , och surfar man dit kan man skapa, redigera och köra individuella tester. I automatläge körs alla tester igenom och en rapport skapas (ungefär som motsvarande för JUnit-tester).
####statistics-specification
Den här modulen testar end-to-end-scenarior, där man kontrollerar att instoppade intyg ger korrekt statistik. Innan man kör måste
man ha en webserver som snurrar.
###Jasmine/Karma
Används för Javascripttester
##Spring
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
|embedded       |använd inbäddad databas (H2), och lägg in testintyg|
|hsa-stub       |gå inte mot hsa, utan använd en stub istället|
|security-fake  |stöd enbart simulerad inloggning|
|security-both  |stöd saml-inloggning och simulerad inloggning|
|security-saml  |stöd enbart saml-inloggning|
|qm             |läs meddelanden från kön|
|active         |processa inkommande intyg|
|hsacached      |Cachar hsa-anrop för att ej överlasta hsa vid omprocessning|
|testapi        |Aktiverar REST-interface som enbart används för testning, tex möjlighet att sätta klockan eller rensa intyg |

##Deployment
Vi använder ansible för att enkelt sätta upp servrar. Följande stämmer för min lokala miljö (Mac, Homebrew), komplettera gärna med andra miljöer.

###Installera ansible

    brew install ansible

Installera ansible-plugin:er

    ansible-galaxy install geerlingguy.apache
    ansible-galaxy install geerlingguy.mysql

###Tools

Checka ut tools (och statistik härifrån, om du inte har det redan) från github, https://github.com/sklintyg/tools (och statistik härifrån, om du inte har det redan).

###Deploy

Följande beskriver hur man deployar till fitnesse-servern, https://fitnesse.inera.nordicmedtest.se
Det är den enda servern som i skrivandes stund är definierad.

Gå till .../tools/ansible och provisionera gemensamma kompnenter:

    ansible-playbook -i hosts_test provision.yml -l statistik-fitnesse

Gå till .../statistik/ansible och provisionera komponenter som ligger utanför applikationen:

    ansible-playbook -i hosts_test provision.yml -l statistik-fitnesse

Deploya själva applikationen:

    ansible-playbook -i hosts_test deploy.yml -l statistik-fitnesse


##Namngivning av klasser och metoder

I projektets inledning användes engelska vid namngivning, men vi har sedan dess gått över till svengelska (enligt nedanstående förklaring). Det kan finnas kvar rester av engelska, men det byts ut när vi upptäcker det.

Att översätta facktermer är inte lätt, subtila betydelseskiftningar påverkar tolkningen, och det är lätt hänt att man vid kommunikation översätter tillbaka till en annan benämning än den ursprungliga.

Vi försöker därför använda samma konsekventa terminologi inom ett område, för hela spektrat av intressenter. Det betyder att vi behöver använda svenska för verksamhetsspecifika termer, men också att engelska bör användas för tekniska termer. Vi har också bestämt oss för att pidginisera koden, dvs inte försöka blanda korrekt svensk och engelsk grammatik utan använda väldefinierade pluralformer (sjukfalls för flera sjukfall t ex) osv. Vi använder endast ASCII i koden, ä och å blir a, och ö blir o.
##JMX
Vi exponerar vissa klasser via JMX.

Vi använder Springs jmx-stöd, konfigurationen finns i application-context-jmx.

Det har inte gjorts något särskilt anpassningsarbete, utan bara helt rått exponerat klasser med metoder som vi vill komma åt. Om vi ska fortsätta använda JMX bör vi städa upp här och skapa särskilda klasser/interface där vi enbart exponerar de metoder vi vill, på ett format som vi vill ha.
##Web services felsvar

Det enda ställena som accepterar indata, är när man hämtar diagnosinformation (diagnoskapitel) samt när man vill se verksamhetsspecifik infromation (verksamhetsid).
###Diagnoskapitel

Skickar man in ett icke existerande diagnoskapitel får man tillbaka ett tomt dataset. Svaret har http status 200 OK.
###Verksamhetsid

Försöker man komma åt en verksamhet som man saknar behörighet till, så returneras http status 403 Access Denied. Detta oavsett om verksamhetsid:t existerar eller inte.
###Icke existerande URL:er

404 Not Found 
##Övrigt
###Söka i json-dokument i databasen
Det finns inbyggt stöd för json i PostgreSQL 9.3 och senare, och det har vi använt när vi behövt göra adhoc-analyser. D v s, för att analysera innehåll i json-objekt lagrade i en tabell så har vi exporterat tabellen till PostgreSQL. Det finns ett exempelskript incheckat under tools/dbscripts/postgresql .
