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

    git clone git@github.com:sklintyg/statistik.git

Efter att man har klonat repository navigera till den klonade katalogen och kör följande kommando:

    ./gradlew clean build

Det här kommandot kommer att bygga samtliga moduler i systemet. Om man inte vill köra testerna vid bygge använder man sig av
assemble istället för build

    ./gradlew clean assemble

Nu ska det gå att starta applikationen med:

    ./gradlew appRun

Nu går det att öppna en webbläsare och surfa till http://localhost:8080/ Observera jetty körs i gradleprocessen, så gradle "blir
inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.

För att starta applikationen i debugläge används:

    ./gradlew appRunDebug

Applikationen kommer då att starta upp med debugPort = **5011**. Det är denna port du ska använda när du sätter upp din
debug-konfiguration i din utvecklingsmiljö.

### Bygga klienten utanför gradle
Installera nodjs 10.12.0 tex med NVM, https://github.com/creationix/nvm

    nvm install 10.12
    nvm alias default 10.12

Installera grunt och bower

    npm install -g grunt-cli bower

Gå in i web och kör

    npm install
    bower install

För att starta en lokal grunt server  kör:

    grunt serve

Då öppnas sidan med adressen http://localhost:9095/
OBS att jetty behöver vara igång samtidigt för att sidan ska fungera.

För att köra hela bygget

    grunt build

För att testa applikationen i ett mer prodlikt läge kan man även starta med en flagga för att köra i minifierat, läge då css/js är packade och sammanslagna, genom att starta:

    ./gradlew clean appRunWar -PuseMinifiedJavaScript

## Protractor
Köra protractor tester lokalt. Du behöver ha chrome version 65 eller nyare för att testerna ska fungera.

 * Starta intygsstatistik med `./gradlew appRun`
 * Starta testerna med `./gradlew protractorTest`

## Gradle
Vi använder Gradle för att bygga, test, installera och köra intygsstatistik. Gradle spottar ur sig ganska mycket text, generellt sett har det gått bra om det sista som skrivs ut är något i stil med:

    BUILD SUCCESSFUL

    Total time: 1 mins 4.842 secs

|Några vanliga gradlekommandon|||
|--------------|---------|---------|
|./gradlew clean build integrationTests|bygg om hela projektet inklusive integrationstester|[projektrot]|
|./gradlew licenseFormat -PcodeQuality|Lägg till licens-header till alla java-filer som saknar header|[projektrot]|
|../gradlew appRunDebug|kör webbservern i debugläge|[projektrot]/web|
|../gradlew fitnesseWiki |starta fitnesse|[projektrot]/test|
|../gradlew fitnesseTest|kör fitnesse-tester|[projektrot]/test|

## Köra med intygstjänsten
Installera active mq
    
    brew install activemq
    
Starta active mq

    activemq console

Starta intygsstatistik, kommer att starta på port 9101

Öppna default.properties  
Ändra activemq.broker.url till "tcp://localhost:61616"  

    ./gradlew appRun -PrunWithIntyg

### Intygstjänsten
Öppna default.properties  
Ändra activemq.destination.queue.name till dev.statistik.utlatande.queue  
Ändra activemq.broker.url till "tcp://localhost:61616"  

Starta intygstjänsten

    ./gradlew appRun

Nu kommer intygen som skickas till intygstjänsten även gå till statistik, det samma gäller för meddelanden som skickas till sendMessageToCare

## Licenser

Vi använder en gradle-plugin som kontrollerar att alla java-filer har en korrekt licens-header. Om den hittar en fil som inte
uppfyller detta krav så fallerar bygget. För att lägga till licens-header kör man

    ./gradlew licenseFormat -PcodeQuality

## Releasebyggen


## Omkörning av statistiken

Beskrivning finns i [OMKORNING.md](OMKORNING.md)

## Liquibase
Liquibase används för att skapa och underhålla underliggande databas. Alla ändringar av databasen måste reflekteras i liquibase-script. Vi använder H2 under utveckling och MySql i andra sammanhang, så scripten måste fungera för båda dessa alternativ.

### MySql
Liquibase kontrollerar databasen varje gång applikationen startas, och startar inte om databasversionen inte stämmer med applikationen.

Skapa/Uppdatera databasen görs med en separat liquibase-runner. Se DatabasUppdatering.

### H2 embedded
Liquibase kör, och modifierar vid behov databasen, varje gång applikationen startas.

## Liquibase för externa miljöer

För miljöer som utvecklingsteamet inte rår över behövs ett script som kan köra liquibase-förändringarna för en viss miljö. För att
skapa ett sådant script kör man

    ./gradlew distZip

Då skapas en zip-fil i [projektrot]/tools/liquibase-runner/build/distributions. Denna zip-fil kan distribueras till lämplig driftsoperatör. För at sedan köra scriptet packar man upp zip-filen, går ner i den
katalog som skapats, och kör:

    ./bin/liquibase-runner --url=jdbc:mysql://localhost/statistik --username=statistik --password=statistik update

Självklart behöver parametrarna "url", "username" och "password" ändras för att passa den aktuella miljön.

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
|embedded       |skapa testintyg och starta konsol för inbäddad databas (H2). profilen fungerar även med MySQL konfiguration|
|caching-enabled|profil från infra som aktiverar redis-cache, krävs för att aktivera job schedulers och ta emot inkommande intyg etc.|
|test           |profil som använder extern redis ska användas tillsammans med dev och caching-enabled|
|hsa-stub       |gå inte mot hsa, utan använd en stub istället|
|security-fake  |stöd enbart simulerad inloggning|
|security-both  |stöd saml-inloggning och simulerad inloggning|
|security-saml  |stöd enbart saml-inloggning|
|hsacached      |Cachar hsa-anrop för att ej överlasta hsa vid omprocessning|
|testapi        |Aktiverar REST-interface som enbart används för testning, tex möjlighet att sätta klockan eller rensa intyg |
|noprocessing   |Stänger av automatisk processning av intyg (tex för fitnesse-körning) |

## Deployment
Vi använder ansible för att enkelt sätta upp servrar. Följande stämmer för min lokala miljö (Mac, Homebrew), komplettera gärna med andra miljöer.

### Installera ansible (deprecated)

    brew install ansible

Installera ansible-plugin:er

    ansible-galaxy install geerlingguy.apache
    ansible-galaxy install geerlingguy.mysql

### Tools
Checka ut tools (och statistik härifrån, om du inte har det redan) från github, https://github.com/sklintyg/tools (och statistik härifrån, om du inte har det redan).

### Deploy
Följande beskriver hur man deployar till fitnesse-servern, https://fitnesse.inera.nordicmedtest.se
Det är den enda servern som i skrivandes stund är definierad.

Gå till .../tools/ansible och provisionera gemensamma kompnenter:

    ansible-playbook -i hosts_test provision.yml -l statistik-fitnesse

Gå till .../statistik/ansible och provisionera komponenter som ligger utanför applikationen:

    ansible-playbook -i hosts_test provision.yml -l statistik-fitnesse

Deploya själva applikationen:

    ansible-playbook -i hosts_test deploy.yml -l statistik-fitnesse


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

## Övrigt
### Söka i json-dokument i databasen
Det finns inbyggt stöd för json i PostgreSQL 9.3 och senare, och det har vi använt när vi behövt göra adhoc-analyser. D v s, för att analysera innehåll i json-objekt lagrade i en tabell så har vi exporterat tabellen till PostgreSQL. Det finns ett exempelskript incheckat under tools/dbscripts/postgresql .

## Testa SAML / Sambi lokalt
Om man vill testa av så applikationen startar korrekt med security-saml/security-both profil kan man göra enligt följande:

- Ändra i web/build.gradle. Kommentera bort blocket jvmArgs och ersätt med följande:


    jvmArgs = [
                '-Dspring.profiles.active=dev,caching-enabled,testapi,embedded,hsa-stub,wc-hsa-stub,security-both,noprocessing',
                '-DbaseUrl=' + baseUrl,
                '-Dstatistics.test.max.intyg=200',
                '-Dstatistics.config.file=' + projectDir + '/../devops/openshift/dev/config/statistik.properties',
                '-Dstatistics.credentials.file=' + projectDir + '/../devops/openshift/dev/env/secret-env.properties',
                '-Dcertificate.folder=' + projectDir + '/../devops/openshift/dev/certifikat',
                '-Dstatistics.resources.folder=classpath:'
        ]

- Se till att kopiera certifikat för ändamålet till /devops/openshift/dev/certifikat
- Starta lokala instanser av mysql, redis-server och activemq. Kan behöva göras i tre separata konsolfönster.

    > mysqld
    > redis-server
    > cd $ACTIVEMQ_HOME/bin
    > ./activemq start
    
- Skapa användare och databas för 'statistik' i din lokala MySQL

    > mysql -u root
    $ CREATE DATABASE statistik;
    $ CREATE USER statistik;
    $ GRANT ALL ON statistik.* to 'statistik'@'localhost' IDENTIFIED BY 'statistik';

- Starta statistiktjänsten

    > ./gradlew build appRun
    
Nu bootas SAML-subsystemet, SP/IdP-metadata läses från /devops/openshift/dev/config, certifikaten från /devops/openshift/dev/certifikat osv används. 

Om SAMBI-metadata för Trial-miljö används så skall Sambi-inloggning mot IdPn https://trial-idp-01.sambi.se/saml2/idp/metadata.php (aka "Sambi Trial IdP") fungera även lokalt.