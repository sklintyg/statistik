package se.inera.statistics.spec

import se.inera.statistics.web.reports.ReportsUtil

class SjukfallIRapportenSjukfallTotalt {

    private String år;
    private String månad;
    private int män;
    private int kvinnor;
    private String inloggadSom;
    private boolean inloggad;

    private ReportsUtil reportsUtil = new ReportsUtil()

    public void reset() {
        inloggad = false
    }

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

    void setInloggadSom(String inloggadSom) {
        if (inloggadSom != null && !inloggadSom.isEmpty()) {
            inloggad = true
            if (this.inloggadSom != inloggadSom) {
                reportsUtil.login(inloggadSom)
                this.inloggadSom = inloggadSom
            }
        }
    }

    public void execute() {
        def report = getReport()
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år) }
        kvinnor = row.data[1]
        män = row.data[2]
    }

    private Object getReport() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad();
        }
        return reportsUtil.getReportAntalIntyg();
    }

}
