package se.inera.statistics.spec

import se.inera.statistics.service.hsa.HsaKon
import se.inera.statistics.web.reports.ReportsUtil
import se.inera.testsupport.Personal

class FoljandeLakareFinns {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String id
    String kön
    int ålder
    int befattning

    public void setKommentar(String kommentar) {}

    public void execute() {
        def hsaKon = HsaKon.valueOf(kön)
        def personal = new Personal(id, hsaKon, ålder, befattning)
        reportsUtil.insertPersonal(personal)
    }

}
