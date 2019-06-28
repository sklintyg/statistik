# Omkörning av statistiken

I fall något har ändrats i hur vi räknar ut statistiken så behöver tjänsten gå igenom alla intyg och meddelanden igen.


## Städa databasen

    TRUNCATE TABLE handelsepekare;
    TRUNCATE TABLE wideline;
    TRUNCATE TABLE messagewideline;
    TRUNCATE TABLE intygcommon;
    UPDATE meddelandehandelse SET tries = 0, processed = 0


## Statistikomkörning
För omkörning av statstik förutsätts en existerande och komplett OCP Deployment med tillhörande inställningar för Intygsstatistik.

Specifika krav för omkörning är att endast 1 replica (Pod) ska vara aktiv och där följande konfiguration ska säkerställas:

* Aktivera spring-profilerna **prod,caching-enabled,security-saml,redis-sentinel,hsacached** 
    * **hsacached** reducerar antal anrop mot HSA-tjänsten och är inte tvingande men rekommenderas. 
* Säkerställ att inkommande intyg inte bearbetas, dvs, variabeln **activemq.broker.url** ska inte konfigureras
* Exponera inte Intygsstatistik på internet
* Inaktivera auto-skalning om sådan finns

### Genomför omkörning:

Rekommenderas att detta körs på en fredag då det tar flera timmar att köra

* Uppdatera Intygsstatistik till senaste versionen.
* Peka om urlen så att Intygsstatistik inte exponeras på internet
* Stäng ner alla intygsstatistik-poddar
* Ta backup av intygsstatistik-databasen
* Kör städa sql-frågorna under **Städa databasen**


    TRUNCATE TABLE handelsepekare;
    TRUNCATE TABLE wideline;
    TRUNCATE TABLE messagewideline;
    TRUNCATE TABLE intygcommon;
    UPDATE meddelandehandelse SET tries = 0, processed = 0

* Lägg till profilen **hsacached** till variabeln spring.profiles.active (SPRING_PROFILES_ACTIVE) i configmap/secret, den reducerar antal anrop mot HSA-tjänsten och är inte tvingande men rekommenderas.
* Ändra **activemq.broker.url** till "vm://localhost?broker.persistent=false" (anteckna tidigare värde ska återställas längre ner) för att säkerställ att inkommande intyg inte bearbetas. (för att kunna rulla tillbaka databasen vid fel)
* Starta upp en pod, då startar omkörningen automatiskt.
* Under hela omkörningen visas "Processed batch with 100 entries" med några sekunders mellanrum
* Omprocessningen är färdig när loggen slutar visa "Processed batch with 100 entries” med några sekunders mellanrum.
* Verifiera att data i rapporterna ser rätt ut (tänk på att nationell statistik bara beräknas om en gång per dygn, när beräkningen är klar visas "National cache populated" i loggen)
* Stäng ner poden
* Ställ tillbaka **activemq.broker.url** och ta bort profilen **hsacached**
* Starta upp alla intygsstatistik-poddar som ska finnas aktiva i miljön
* Peka tillbaka urlen, så Intygsstatistik exponeras på internet

