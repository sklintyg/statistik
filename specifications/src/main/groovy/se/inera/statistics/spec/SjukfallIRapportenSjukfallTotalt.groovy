package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class SjukfallIRapportenSjukfallTotalt {

    private String år;
    private String månad;
    private int män;
    private int kvinnor;

    private ReportsUtil reportsUtil = new ReportsUtil()


    int män() {
        return män
    }

    int kvinnor() {
        return kvinnor
    }

    void setÅr(String år) {
        this.år = år
    }

    void setMånad(String månad) {
        this.månad = månad
    }

    void setKommentar(String kommentar) {
    }

    public void execute() {
        def report = reportsUtil.getReportAntalIntyg();
        //TODO Hämta ut data för män och kvinnor för rätt år och månad och sätt respektive fält.
    }

}
