#statistik
##Introduktion
Systemet är logiskt uppdelat på två delsystem: Statistiktjänsten och Statistikapplikationen.
###Statistiktjänsten
Statistiktjänsten beräknar statistik utifrån given rådata och gör denna statistik tillgängligt via ett api.
###Statistikapplikationen
Statistikapplikationen hanterar GUI och inloggning. Kommunicerar med statistiktjänsten för att hämta aktuell statistik.

Det finns två olika typer av användare på statistikapplikationen:

+ Ej inloggad användare som får tillgång till statistik på nationell nivå.
+ Inloggad användare från vårdenhet som både har tillgång till statistiken för sin enhet samt den övergripande på nationell nivå. 

##Komma igång med lokal installation
Den här sektionen beskriver hur man bygger Inera Statistics för att kunna köras helt fristående.

Vi använder Gradle, för närvarande version 2.2.1, för att bygga applikationerna.

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

##Gradle
Vi använder Gradle för att bygga, test, installera och köra statistiktjänsten. Gradle spottar ur sig ganska mycket text, generellt sett har det gått bra om det sista som skrivs ut är något i stil med:

    BUILD SUCCESSFUL

    Total time: 1 mins 4.842 secs

|Några vanliga gradlekommandon|||
|--------------|---------|---------|
|./gradlew clean build integrationTests|bygg om hela projektet inklusive integrationstester|[projektrot]|
|../gradlew fitnesseWiki |starta fitnesse|[projektrot]/specifications|
|../gradlew fitnesseTest|kör fitnesse-tester|[projektrot]/specifications|
|../gradlew appRunDebug|kör webbservern i debugläge|[projektrot]/statistik-web|

##Releasebyggen

##Liquibase
Liquibase används för att skapa och underhålla underliggande databas. Alla ändringar av databasen måste reflekteras i liquibase-script. Vi använder H2 under utveckling och MySql i andra sammanhang, så scripten måste fungera för båda dessa alternativ.
###MySql
Liquibase kontrollerar databasen varje gång applikationen startas, och startar inte om databasversionen inte stämmer med applikationen.

Skapa/Uppdatera databasen görs med en separat liquibase-runner. Se DatabasUppdatering.
###H2 embedded
Liquibase kör, och modifierar vid behov databasen, varje gång applikationen startas.
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

De huvudprofiler som finns är, exakt en av dessa måste finnas:
dev	utvecklings-miljö
test	test-miljö
qa	qa-miljö
prod	prod-miljö (finns endast i release-branchen)

Sedan finns ett antal modifierare:
embedded	använd inbäddad databas (H2)
generatetestdata	skapa tesdata vid uppstart

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
