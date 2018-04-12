## Intygsstatistik anonymisering

Anonymiserings-applikationen anonymiserar intygsinnehåll i en statistikdatabas. Den används för att skapa anonymiserade kopior av produktions-databasen för Statistik enligt följande flöde:

1. Exportera produktions-databasen till fil
2. Importera den exporterade databasen till ny, temporär databas
3. Köra anonymiserings-applikationen på den temporära databasen
4. Exportera temporär, anonymiserad databas till fil

Anonymisering görs på
* Originalmeddelande för intyg (intyghandelse.data)
* HSA personal (hsa.personal.id, hsa.personal.tilltalsnamn och hsa.personal.efternamn)
* Originalmeddelande för ärendekommunikation (meddelandehandelse.data)

### Att köra applikationen (exempel)

Tänk på att använda rätt version av skriptet. Det bör stå i beställningen.

1. Hämta zip-filen i repot, exempelvis:
https://build-inera.nordicmedtest.se/nexus/#browse/search=keyword%3Danonymisering%20AND%20version%3D6.0.0.539
2. packa up zip-filen, gå in i katalogen: 
```
unzip anonymisering-6.0.0.539.zip
cd anonymisering-6.0.0.539
```
3. Hämta inställningsfilen: https://raw.githubusercontent.com/sklintyg/statistik/master/tools/anonymisering/dataSource.properties
4. Redigera inställningsfilen så att den stämmer överens med konfigurationen för den temporära databasen.
5. Kör programmet: `bin/anonymisering` (det kan ta en bra stund innan det blir klart)

### Implementation

Källkoden ligger i statistikprojektet (https://github.com/sklintyg/statistik/tree/master/tools/anonymisering).

Applikationen itererar över intygs-id, och anonymiserar ursprungligt intygs-meddelande (data) i steg 1.
Därefter itererar applikationen över hsa och anonymiserar personid-kolumnen för att slutligen anonymisera läkare genom att anonymisera kolumnerna lakareid, tilltalsnamn och efternamn.
wideline och handelsepekare nollställs.
När statistikapplikationen startas kommer wideline populeras från början.

### Versioner av skriptet

Ibland följer inte anonymiserings-skriptet releaserna "rakt av". Det händer ibland att ändringar i skriptet krävs mellan två releaser på grund av uppdatering av databas-version, prestanda. Glöm inte att uppdatera länken till aktuell version i Installationsanvisningarna för relevant release.
