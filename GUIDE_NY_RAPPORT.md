# Guide för skapande av ny rapport i Intygsstatistik
Tanken med detta dokument är att sammanfatta vilka steg som behöver utföras för att skapa en ny 
rapport i Intygsstatistik.

## Se till att nödvändig data finns tillgänglig
Rapporterna beräknas utifrån data i tabellerna wideline, intygcommon samt messagewideline. Finns 
datan som ska presenteras i rapporten redan med i dessa tabeller så är detta steg redan 
avklarat, om inte så behöver datan populeras till lämplig tabell. Troligtvis behövs då någon ny
kolumn skapas vilket görs via liquibase-skript (service/src/main/resources/changelog/changelog.xml).

Om nya kolumner behövde skapas i tabellerna ovan så behöver dessa sannolikt även populeras med 
data från redan existerande intyg. Det tillvägagångssätt som då brukar användas är att helt enkelt
beräkna om dessa tabeller (a.k.a "omprocessning") vid nästa produktions-sättning. Instruktioner 
för hur detta går till finns i dokumentet [OMKORNING](OMKORNING.md).

## Beräkna och sammanställ rapporten i service-lagret
Lägg till metoder i se.inera.statistics.web.service.WarehouseService för att hantera beräkning av 
rapporten i service-lagret. Följ samma mönster som existerande rapporter, tex genom att lägga 
logiken för beräkning och sammanställning av rapporten i lämplig query-klass.

## Lägg till rest-endpoint
Endpoints för nationell statistik finns i klassen "ChartDataService", för 
verksamhetsstatistik i "ProtectedChartDataService" samt för regionsstatistik 
i "ProtectedRegionService". Lägg till metoder för de nivåer som ska implementeras. 
Rapporter på verksamhetsnivå och på regionsnivå anropar warehouse i serivce-lagret 
direkt medans alla rapporter på nationell nivå beräknas samtidigt (och sedan cachas 
resultatet) vilket är anledningen till att hanteringen här blir annorlunda.  

## Konvertera rapporten
Innan rapporten returneras av rest-endpointen behöver den konverteras för att passa 
hanteringen i frontend, bland annat behöver en detalj-rapport delas upp på en del 
för diagrammet och en del för tabellen. Använd en existerande generell converter-klass 
eller skapa en specifik konverterare för den nya rapporten.

## Lägg till support för rapporten i Fitnesse
Troligtvis finns redan en eller flera fitnesse-specar skapade från kravställaren. Lägg
till support för att fitnesse-specarna för den nya rapporten ska gå att köra. Glöm inte
att o-ignorera de existerande specarna (alltså plocka bort raden "Prune").

## Lägg till den nya rapporten i GUIt
Börja med att lägga till en route till den nya rapporten i filen "app.routes.js". Beroende på 
hur rapporten ska visas så används olika controllers (hittills finns tre olika som hanterar 
alla detaljrapporter - columnChartDetailsViewCtrl, doubleAreaChartsCtrl samt singleLineChartCtrl).
I controllern skapas en konfigurationsobjekt för rapporten (vilket även anges i routen).
Lägg till nya texter i messages.js och anrop till restservicen i statisticsData.js.

## Skapa tester
Lägg till tester för rapporten.
