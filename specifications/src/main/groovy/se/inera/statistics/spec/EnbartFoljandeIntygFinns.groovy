package se.inera.statistics.spec

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.joda.time.DateTimeUtils
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil

class EnbartFoljandeIntygFinns {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    private static int intygIdCounter = 1;

    def personnr
    def diagnoskod
    def start
    def slut
    def enhet
    def vardgivare
    def arbetsförmåga
    String arbetsförmåga2
    def start2
    def slut2

    public void setKommentar(String kommentar) {}

    void setEnhet(enhet) {
        this.enhet = enhet
        this.vardgivare = reportsUtil.getVardgivareForEnhet(enhet, ReportsUtil.VARDGIVARE)
    }

    public void beginTable() {
        reportsUtil.clearDatabase()
    }

    public void reset() {
        personnr = "19790407-1295"
        diagnoskod = "A01"
        vardgivare = ReportsUtil.VARDGIVARE
        arbetsförmåga = 0
        arbetsförmåga2 = ""
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
        observationFormaga.varde[0].quantity = arbetsförmåga
        result.observationer.add(observationFormaga)

        if (!arbetsförmåga2.isEmpty()) {
            def observationFormaga2 = slurper.parseText(observationFormagaString)
            observationFormaga2.observationsperiod.from = start2
            observationFormaga2.observationsperiod.tom = slut2
            observationFormaga2.varde[0].quantity = arbetsförmåga2
            result.observationer.add(observationFormaga2)
        }

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

}
