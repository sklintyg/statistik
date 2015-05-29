Statistiktjänsten anonymisering

Anonymiserings-applikationen anonymiserar intygsinnehåll i en statistikdatabas. Den används för att skapa anonymiserade kopior av produktions-databasen för Statistik enligt följande flöde:

    Exportera produktions-databasen till fil
    Importera den exporterade databasen till ny, temporär databas

    Lägg på ett index för att snabba upp: CREATE INDEX correlationId_index ON intyghandelse (correlationId(40));
    Köra migrerings-applikationen på den temporära databasen
    Köra anonymiserings-applikationen på den temporära databasen
    Exportera temporär, anonymiserad databas till fil

Migrering

För att kunna köra anonymiseringsapplikationen för Statistik måste intygen vara migrerade till samma interna format som används av intygstjänsten. Det finns en applikation som gör den migreringen.
Att köra applikationen

    Hämta zip-filen i repot: https://repository-callistasoftware.forge.cloudbees.com/snapshot/se/inera/statistics/tools/statistics-MigreringIntyg/3.0.0/
    packa up zip-filen, gå in i katalogen: 
    unzip migrering-3.0.0-SNAPSHOT.zip 
    cd migrering-3.0.0-SNAPSHOT    
    Hämta inställningsfilen: https://raw.githubusercontent.com/sklintyg/statistik/develop/tools/migrering/dataSource.properties
    Redigera inställningsfilen
    Kör programmet: bin/migrering (det kan ta en bra stund innan det blir klart)

Anonymisering
Att köra applikationen

    Hämta zip-filen i repot: http://repository-callistasoftware.forge.cloudbees.com/snapshot/se/inera/statistik/statistik-anonymisering/3.0.0-SNAPSHOT/
    packa up zip-filen, gå in i katalogen: 
    unzip anonymisering-3.0.0-SNAPSHOT.zip
    cd anonymisering-3.0.0-SNAPSHOT
    
    Hämta inställningsfilen: https://raw.githubusercontent.com/sklintyg/statistik/master/tools/anonymisering/dataSource.properties
    Redigera inställningsfilen
    Kör programmet: bin/anonymisering (det kan ta en bra stund innan det blir klart)

Anonymisering görs på original-meddelandet (intyghandelse.data) samt för sjukfall (sjukfall.personId).
Implementation

Källkoden ligger i statistikprojektet (https://github.com/sklintyg/statistik/tree/master/tools/anonymisering). Applikationen itererar över intygs-id, och anonymiserar ursprungligt intygs-meddelande (data) i steg 1. Därefter itererar applikationen över hsa och anonymiserar personid-kolumnen.
wideline och handelsepekare nollställs. När statistikapplikationen startas kommer wideline populeras från början.
