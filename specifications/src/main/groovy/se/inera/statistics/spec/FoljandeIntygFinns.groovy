package se.inera.statistics.spec

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.joda.time.DateTimeUtils
import se.inera.statistics.service.processlog.EventType
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Intyg

class FoljandeIntygFinns {

    protected final ReportsUtil reportsUtil = new ReportsUtil()

    private static int intygIdCounter = 1;

    def personnr
    def diagnoskod
    def start
    def slut
    def enhet
    String enhetsnamn
    def huvudenhet
    def vardgivare
    def arbetsförmåga
    String arbetsförmåga2
    def start2
    def slut2
    def läkare
    def län
    def exaktintygid
    String händelsetyp
    String jsonformat
    String intygstyp
    String funktionsnedsättning
    String aktivitetsbegränsning

    public void setKommentar(String kommentar) {}

    void setEnhet(enhet) {
        this.enhet = "UTANENHETSID".equalsIgnoreCase(enhet) ? null : enhet
        this.vardgivare = reportsUtil.getVardgivareForEnhet(enhet, ReportsUtil.VARDGIVARE)
    }

    void setDiagnoskod(kod) {
        this.diagnoskod = "NULLDIAGNOSKOD".equalsIgnoreCase(kod) ? null : kod
    }

    public void reset() {
        personnr = "19790407-1295"
        diagnoskod = "A01"
        vardgivare = ReportsUtil.VARDGIVARE
        arbetsförmåga = 0
        arbetsförmåga2 = ""
        läkare = "Personal HSA-ID"
        län = null
        händelsetyp = EventType.CREATED.name()
        exaktintygid = intygIdCounter++
        jsonformat = "nytt"
        huvudenhet = null
        intygstyp = "fk7263"
        enhetsnamn = null
        funktionsnedsättning = ""
        aktivitetsbegränsning = ""
    }

    public void execute() {
        if (huvudenhet == null || huvudenhet.trim().isEmpty()) {
            huvudenhet = enhet;
        }
        def finalIntygDataString = getIntygDataString()
        Intyg intyg = new Intyg(EventType.valueOf(händelsetyp), finalIntygDataString, String.valueOf(exaktintygid), DateTimeUtils.currentTimeMillis(), län, huvudenhet, enhetsnamn, vardgivare, enhet, läkare)
        reportsUtil.insertIntyg(intyg)
    }

    Object getIntygDataString() {
        if ("gammalt".equalsIgnoreCase(jsonformat)) {
            return executeForOldJsonFormat();
        } else if ("felaktigt".equalsIgnoreCase(jsonformat)) {
            return executeForIllegalJsonFormat();
        } else {
            return executeForNewJsonFormat();
        }
    }

    private String executeForIllegalJsonFormat() {
        return "This intyg will not be possible to parse as json"
    }

    private String executeForNewJsonFormat() {
        def slurper = new JsonSlurper()
        String intygString = getClass().getResource('/maximalt-fk7263-internal.json').getText('UTF-8')
        def result = slurper.parseText(intygString)

        result.grundData.patient.personId = personnr
        result.typ = intygstyp
        result.grundData.skapadAv.personId = läkare
        result.grundData.skapadAv.vardenhet.enhetsid = enhet
        result.grundData.skapadAv.vardenhet.vardgivare.vardgivarid = vardgivare

        if ("UTANDIAGNOSKOD".equalsIgnoreCase(diagnoskod)) {
            result.remove("diagnosKod")
        } else {
            result.diagnosKod = diagnoskod
        }

        result.funktionsnedsattning = funktionsnedsättning
        result.aktivitetsbegransning = aktivitetsbegränsning

        result["nedsattMed" + (100 - Integer.valueOf(arbetsförmåga))] = [
            from: start,
            tom: slut
        ]

        if (!arbetsförmåga2.isEmpty()) {
            result["nedsattMed" + (100 - Integer.valueOf(arbetsförmåga2))] = [
                from: start2,
                tom: slut2
            ]
        }

        def builder = new JsonBuilder(result)
        return builder.toString()
    }

    private String executeForOldJsonFormat() {
        def slurper = new JsonSlurper()
        String intygString = getClass().getResource('/intyg1.json').getText('UTF-8')
        String observationKodString = getClass().getResource('/observationMedKod.json').getText('UTF-8')
        def result = slurper.parseText(intygString)

        result.patient.id.extension = personnr
        result.typ.code = intygstyp
        result.skapadAv.id.extension = läkare

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
        return builder.toString()
    }

    public void endTable() {
        reportsUtil.processIntyg()
    }

}
