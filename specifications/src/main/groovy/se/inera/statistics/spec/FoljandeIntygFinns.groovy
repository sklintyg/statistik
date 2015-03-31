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
    def vardgivare
    def arbetsförmåga
    String arbetsförmåga2
    def start2
    def slut2
    def läkare
    def län
    def exaktintygid
    String intygstyp
    String enhetsnamn

    public void setKommentar(String kommentar) {}

    void setEnhet(enhet) {
        this.enhet = "UTANENHETSID".equalsIgnoreCase(enhet) ? null : enhet
        this.vardgivare = reportsUtil.getVardgivareForEnhet(enhet, ReportsUtil.VARDGIVARE)
    }

    public void reset() {
        personnr = "19790407-1295"
        diagnoskod = "A01"
        vardgivare = ReportsUtil.VARDGIVARE
        arbetsförmåga = 0
        arbetsförmåga2 = ""
        läkare = "Personal HSA-ID"
        län = null
        intygstyp = EventType.CREATED.name();
        exaktintygid = intygIdCounter++;
        enhetsnamn = null;
    }

    public void execute() {
        def slurper = new JsonSlurper()
        String intygString = getClass().getResource('/maximalt-fk7263-internal.json').getText('UTF-8')
        def result = slurper.parseText(intygString)

        result.grundData.patient.personId = personnr;

        result.grundData.skapadAv.personId = läkare
        result.grundData.skapadAv.vardenhet.enhetsid = enhet
        result.grundData.skapadAv.vardenhet.vardgivare.vardgivarid = vardgivare

        result.diagnosKod = diagnoskod

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
        def finalIntygDataString = builder.toString()

        Intyg intyg = new Intyg(EventType.valueOf(intygstyp), finalIntygDataString, String.valueOf(exaktintygid), DateTimeUtils.currentTimeMillis(), län, enhetsnamn)
        reportsUtil.insertIntyg(intyg)
    }

    public void endTable() {
        reportsUtil.processIntyg()
    }

}
