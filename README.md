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

Vi använder Maven, för närvarande version 3.0.5, för att bygga applikationerna. OBS! Maven 3.1 fungerar inte i skrivandes stund!

Börja med att skapa en lokal klon av källkodsrepositoryt:

    git clone git@github.com:sklintyg/statistik.git

Efter att man har klonat repository navigera till den klonade katalogen och kör följande kommando:

mvn clean install

Det här kommandot kommer att bygga samtliga moduler i systemet. Om man inte vill köra testerna vid bygge använder man sig av skipTests-flaggan.

    mvn clean install -DskipTests=true

Nu ska det gå att starta applikationen med:

    mvn jetty:run -Dmaven.test.skip=true -DskipTests -Dspring.profiles.active=dev,embedded

Nu går det att öppna en webbläsare och surfa till http://localhost:8080/ Observera jetty körs i mavenprocessen, så maven "blir inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.
##Maven
Vi använder Maven för att bygga, test, installera och köra statistiktjänsten. Maven spottar ur sig ganska mycket text, generellt sett har det gått bra om det sista som skrivs ut är något i stil med:

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 54.036s
[INFO] Finished at: Wed Feb 12 11:04:43 CET 2014
[INFO] Final Memory: 50M/420M
[INFO] ------------------------------------------------------------------------

Om man vill ange standardflaggor till maven så kan man använda miljövariablen MAVEN_OPTS t ex:

    export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5007 -Xmx3192M -XX:MaxPermSize=500M"

Det är de inställningar jag använder, som gör att jag kan ansluta min IDE-debugger på port 5007, med gott om minne.

Några vanliga mavenkommandon
|mvn clean install -P integration 	|bygg om hela projektet 	|[projektrot]
|mvn verify -P wiki 	|starta fitnesse 	|[projektrot]/statistics-specification
|mvn verify -P auto 	|kör fitnesse-tester 	|[projektrot]/statistics-specification
|mvn jetty:run -Dmaven.test.skip=true -DskipTests -Dspring.profiles.active=dev,embedded 	|kör webbservern 	|[projektrot]/statistics-web
|mvn jetty:run -Dmaven.test.skip=true -DskipTests -Dspring.profiles.active=dev 	|kör webbservern mot mysql 	|[projektrot]/statistics-web

##Releasebyggen
OBS! Den incheckade pom.xml:en kräver tillgång till privata servrar och måste konfigureras om om man inte har tillgång till dem.

OBS2! Glöm inte att stoppa in de servrar som ska användas i [.m2]/settings.xml.

Kör följande rad för rad och säkerställ att eventuella problem åtgärdas innan du kör nästa rad:

    [gör vad som krävs (t ex git merge ...) för att uppdatera releasbranchen]
    git checkout inera-statistics-[huvudversion]-RB
    mvn release:prepare
    git push origin
    git push origin statistics-[version]
    git checkout statistics-[version]
    mvn clean deploy
    git checkout inera-statistics-[huvudversion]-RB

+ version = exakt version som ska byggas t ex 1.0.5
+ huvudversion = den releasbranch man vill bygga den nya verionen på
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
JUnit används för enhetstester, funktionestester samt integrationstester. Normalt anses en testklass innehålla vanliga enhetstester, Maven kör dessa tester per default. Avslutas klassnamnet med IntegrationTest? så är det ett integrationstest, och Maven kör bara dessa tester om integrationsprofilen aktiveras (-P integration). Funktionella tester, dvs klasser som avslutas med FunctionalTest? körs aldrig från Maven, utan måste körast manuellt från en IDE eller dylikt.
###Fitnesse/Slim
Det finns två separata moduler som använder Fitnesse, statistics-specification samt statistics-business-specification. Fitnesse kan antingen köras som automattest eller som en wiki. I wikiläge (-P wiki) startas en webserver på http://localhost:9125/StatisticsTests , och surfar man dit kan man skapa, redigera och köra individuella tester. I automatläge (-P auto) körs alla tester igenom och en rapport skapas (ungefär som motsvarande för JUnit-tester).
####statistics-specification
Den här modulen testar frontenden m h a Geb och Selenium. Innan man kör måste man en webserver som snurrar. Fitnesse kommer att starta en webbläsare och navigera till olika delar av applikationen. I dagsläget är det inga avancerade tester som görs, det enda som görs är att klicka runt i gränssnittet, först utan inloggning, sedan som inloggad.
####statistics-business-specification
En serie tester där olika intygssekvenser stoppas in, varefter man verifierar att man får ut förväntad statistik för de givna sekvenserna. Här kan man göra end-to-end-tester som dokumenterar och verifierar de affärsregler som ska gälla.
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
