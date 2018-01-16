package se.inera.statistics.spec

import se.inera.statistics.service.hsa.HsaKon
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Personal

class FoljandeLakareFinns {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String id
    String förnamn
    String efternamn
    String kön
    int ålder
    String befattningar
    boolean skyddad

    public void reset() {
        förnamn = "Läkarförnamn"
        efternamn = "Läkarefternamn"
        kön = "UNKNOWN"
        ålder = 0
        skyddad = false
    }

    public void setKommentar(String kommentar) {}

    public void execute() {
        def hsaKon = HsaKon.valueOf(kön.toUpperCase())

        def befattningarEmpty = befattningar == null || befattningar.isEmpty()
        def befattningarList = befattningarEmpty ? Collections.emptyList() : Arrays.asList(befattningar.split(","))
        def personal = new Personal(id, förnamn, efternamn, hsaKon, ålder, befattningarList, skyddad)
        reportsUtil.insertPersonal(personal)
    }

}
