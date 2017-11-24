## Intygsstatistik anonymisering

Anonymiserings-applikationen anonymiserar intygsinnehåll i en statistikdatabas. Den används för att skapa anonymiserade kopior av produktions-databasen för Statistik enligt följande flöde:

    Exportera produktions-databasen till fil
    Importera den exporterade databasen till ny, temporär databas

    Köra anonymiserings-applikationen på den temporära databasen
    Exportera temporär, anonymiserad databas till fil

Anonymisering görs på
* Originalmeddelande för intyg (intyghandelse.data)
* HSA personal (hsa.personal.id, hsa.personal.tilltalsnamn och hsa.personal.efternamn)
* Originalmeddelande för ärendekommunikation (meddelandehandelse.data)

### Att köra applikationen

    Hämta zip-filen i repot: https://build-inera.nordicmedtest.se/nexus/repository/releases/se/inera/statistik/anonymisering/3.0.8/
    packa up zip-filen, gå in i katalogen:
    unzip anonymisering-3.0.8.zip
    cd anonymisering-3.0.8

    Hämta inställningsfilen: https://raw.githubusercontent.com/sklintyg/statistik/master/tools/anonymisering/dataSource.properties
    Redigera inställningsfilen
    Kör programmet: bin/anonymisering (det kan ta en bra stund innan det blir klart)

### Implementation

Källkoden ligger i statistikprojektet (https://github.com/sklintyg/statistik/tree/master/tools/anonymisering).

Applikationen itererar över intygs-id, och anonymiserar ursprungligt intygs-meddelande (data) i steg 1.
Därefter itererar applikationen över hsa och anonymiserar personid-kolumnen för att slutligen anonymisera läkare genom att anonymisera kolumnerna lakareid, tilltalsnamn och efternamn.
wideline och handelsepekare nollställs.
När statistikapplikationen startas kommer wideline populeras från början.
