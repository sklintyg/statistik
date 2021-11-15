# Statistik service

## Databas

### Tabeller i bokstavsordning med beskrivningar

**CountyPopulation**: Håller befolkningsstatistik från SCB för användning i län-rapporten  
**DATABASECHANGELOG**: Används av liquibase för att veta vilka ändringar som har blivit applicerade  
**DATABASECHANGELOGLOCK**: Används av liquibase för att undvika concurrency-problem  
**enhet**: Information om vårdenheter som utfärdat intyg  
**handelsepekare**: Visar hur långt vi har kommit i processandet av intyg i tabellen “intyghandelse"  
**hsa**: Cachade svar från HSA som används vid processningen av intyg.  
**intygcommon**: Innehåller processad information kring samtliga de intygstyper som används för intygsrapporterna. Detta kan alltså anses vara en minsta gemensamma nämnare mellan de intyg som hanteras i Intygsstatistik. Kan jämföras med tabellen wideline som innehåller processad information från de intyg som är grunden till sjukfallsstatistiken.  
**intyghandelse**: Samtliga intyg som har mottagits (från processloggen)  
**intygsenthandelse**: Information om vilka intyg som har skickats vidare till mottagare  
**lakare**: Information om läkare som utfärdat intyg  
**Landsting**: Innehåller alla regioner, dvs vilka vårdgivare som har tillgång till regionstatistik  
**LandstingEnhet**: Vilka enheter som är tillgängliga i regionstatistiken samt antal listningar (dvs innehållet i den fil som laddas upp för regionstatistik)  
**LandstingEnhetUpdate**: Information (vem, vad, när) om senaste uppladdning av regionstatistik  
**meddelandehandelse**: Samtliga ärendekommunikationsmeddelanden som har mottagits  
**messagewideline**: Innehåller processade ärendekommunikationsmeddelanden. Informationen i den här tabellen används till meddelande-rapporterna.  
**user_settings**: Innehåller information om inställningar per användare  
**userselection**: Innehåller filter- och diagnos-val  
**wideline**: Innehåller processade intyg (de mappar dock inte alltid "ett till ett" mot intyg i “intyghandelse”). Detta är den information som senare används för beräkning av sjukfall. (Warehouse)  


### Relationer
**handelsepekare**: Pekar på id för hur långt processningen har hunnit. eventid hänvisar då till respektive intyghandelse.id, intygsenthandelse.id, meddelandehandelse.id.  

**hsa**: id-fältet visar intygid.  
**intygcommon**: intygid-fältet visar intygid.  
**intyghandelse**: correlationid-flätet visar intygid.  
**intygsenthandelse**: correlationid-fältet visar intygid.  
**wideline**: lakarintyg-fältet refererar till intyghandelse.id. correlationid-fältet visar intygid.   
**meddelandehandelse**: correlationid-fältet visar messageid.  
**messagewideline**: meddelandeid-fältet visar messageid. intygid-fältet visar intygid. logid-fältet refererar till meddelandehandelse.id.  

**lakare**: Håller lista över läkarnamn.  

**Landsting**: Lista över de vårdgivare som anses vara region. vardgivareid visar vgid.  
**LandstingEnhet**: Lista över de enheter som visas på regionsnivå per vårdgivare. landstingid-fältet visar vgid.  
**LandstingEnhetUpdate**: landstingid-fältet refererar till landsting.id

**user_settings**: hsaid-fältet visar userid.  
**userselection**: Innehåller filter. Inga relationer.  
**CountyPopulation**: Inga relationer. Sparar data i json, en rad per år.  
**DATABASECHANGELOG**: Liquibase  
**DATABASECHANGELOGLOCK**: Liquibase  