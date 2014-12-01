package se.inera.statistics.spec

abstract class SimpleDetailsReport extends Rapport {

    String år;
    String månad;

    void executeTabell(report, String rowNameMatcher) {
        def row = report.tableData.rows.find { currentRow -> currentRow.name == rowNameMatcher }
        totalt = row.data[0]
        kvinnor = row.data[1]
        män = row.data[2]
    }

    void executeTabell(report) {
        executeTabell(report, månad + " " + år)
    }

    def getReportSjukfallTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(inloggadSom);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    def getReportLangaSjukfall() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Långa sjukfall- is not available on national level");
    }

    def getReportSjukfallPerEnhet() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhet(inloggadSom);
        }
        throw new RuntimeException("Report -Sjukfall per enhet- is not available on national level");
    }
}
