package se.inera.statistics.spec

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.joda.time.DateTimeUtils
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil

class EnbartFoljandeIntygFinns {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    private static int intygIdCounter = 1;

    private def personnr
    private def diagnoskod
    private def start
    private def slut
    private def enhet
    private def vardgivare

    public void setKommentar(String kommentar) {}

    public void beginTable() {
        reportsUtil.clearDatabase()
    }

    public void reset() {
        personnr = "19790407-1295"
        diagnoskod = "A01"
        vardgivare = ReportsUtil.VARDGIVARE
    }

    public void execute() {
        def slurper = new JsonSlurper()
        String intygString = getClass().getResource('/intyg1.json').getText('UTF-8')
        String observationKodString = getClass().getResource('/observationMedKod.json').getText('UTF-8')
        def result = slurper.parseText(intygString)

        result.patient.id.extension = personnr;

        def observation = slurper.parseText(observationKodString)
        observation.observationskod.code = diagnoskod
        result.observationer.add(observation)

        String observationFormagaString = getClass().getResource('/observationMedArbetsformaga.json').getText('UTF-8')
        def observationFormaga = slurper.parseText(observationFormagaString)
        observationFormaga.observationsperiod.from = start
        observationFormaga.observationsperiod.tom = slut

        result.observationer.add(observationFormaga)

        result.skapadAv.vardenhet.id.extension = enhet
        result.skapadAv.vardenhet.vardgivare.id.extension = vardgivare

        def builder = new JsonBuilder(result)
        def finalIntygDataString = builder.toString()

        Intyg intyg = new Intyg(EventType.CREATED, finalIntygDataString, String.valueOf(intygIdCounter++), DateTimeUtils.currentTimeMillis())

        reportsUtil.insertIntyg(intyg)
    }

    public void endTable() {
        reportsUtil.processIntyg()
    }

    void setPersonnr(personnr) {
        this.personnr = personnr
    }

    void setDiagnoskod(diagnoskod) {
        this.diagnoskod = diagnoskod
    }

    void setStart(start) {
        this.start = start
    }

    void setSlut(slut) {
        this.slut = slut
    }

    void setEnhet(enhet) {
        this.enhet = enhet
    }

    void setVardgivare(vardgivare) {
        this.vardgivare = vardgivare
    }

}
