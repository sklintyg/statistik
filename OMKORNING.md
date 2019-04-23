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

* Omkörningen startar genom att rulla ut Intygsstatistik med en Replica (Pod) utan auto-skalning
* Under hela omkörningen visas "Processed batch with 100 entries" med några sekunders mellanrum
* Omprocessningen är färdig när loggen slutar visa "Processed batch with 100 entries” med några sekunders mellanrum.
* Verifiera att data i rapporterna ser rätt ut (tänk på att nationell statistik bara beräknas om en gång per dygn, när beräkningen är klar visas "National cache populated" i loggen)

