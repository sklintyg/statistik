package se.inera.statistics.spec

class RapportenForsakringskassan extends Rapport {

    def diagnos
    def kön
    def län
    def antalsjukfall
    def medellängd
    def medianlängd

    def diagnos() {
        return diagnos
    }

    def kön() {
        return kön
    }

    def län() {
        return län
    }

    def antalsjukfall() {
        return antalsjukfall
    }

    def medellängd() {
        return medellängd
    }

    def medianlängd() {
        return medianlängd
    }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.diagnos == diagnos && currentRow.lanId == län && currentRow.kon == kön }
        diagnos = row.diagnos
        län = row.lanId
        kön = row.kon
        antalsjukfall = row == null ? 0 : row.antal
        medellängd = row == null ? 0 : row.medel
        medianlängd = row == null ? 0 : row.median
    }

    def getReport() {
        return reportsUtil.getFkReport();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
