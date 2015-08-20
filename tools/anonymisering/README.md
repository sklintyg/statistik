Statistiktjänsten anonymisering

Anonymiserings-applikationen anonymiserar intygsinnehåll i en statistikdatabas. Den används för att skapa anonymiserade kopior av produktions-databasen för Statistik enligt följande flöde:

    Exportera produktions-databasen till fil
    Importera den exporterade databasen till ny, temporär databas

    Köra anonymiserings-applikationen på den temporära databasen
    Exportera temporär, anonymiserad databas till fil

Anonymisering
Att köra applikationen

    Hämta zip-filen i repot: http://repository-callistasoftware.forge.cloudbees.com/release/se/inera/statistik/anonymisering/3.0.5/
    packa up zip-filen, gå in i katalogen: 
    unzip anonymisering-3.0.5.zip
    cd anonymisering-3.0.5
    
    Hämta inställningsfilen: https://raw.githubusercontent.com/sklintyg/statistik/master/tools/anonymisering/dataSource.properties
    Redigera inställningsfilen
    Kör programmet: bin/anonymisering (det kan ta en bra stund innan det blir klart)

Anonymisering görs på original-meddelandet (intyghandelse.data) samt för (hsa.personal.id).
Implementation

Källkoden ligger i statistikprojektet (https://github.com/sklintyg/statistik/tree/master/tools/anonymisering). Applikationen itererar över intygs-id, och anonymiserar ursprungligt intygs-meddelande (data) i steg 1. Därefter itererar applikationen över hsa och anonymiserar personid-kolumnen.
wideline och handelsepekare nollställs. När statistikapplikationen startas kommer wideline populeras från början.
