package se.inera.statistics.spec

class SjukfallIRapportenSocialstyrelsen extends Rapport {

    def diagnos
    def kön
    def län
    def sjukskrivningslängd

    def diagnos() {
        return diagnos
    }

    def kön() {
        return kön
    }

    def län() {
        return län
    }

    def sjukskrivningslängd() {
        return sjukskrivningslängd
    }

    public void executeTabell(report) {
        def row = report.find { currentRow -> currentRow.diagnos == diagnos  }
        kön = row == null ? -1 : row.kon
        län = row == null ? -1 : row.lanId
        sjukskrivningslängd = row == null ? -1 : row.length
    }

    def getReport() {
        return reportsUtil.getSocialstyrelsenReport();
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
