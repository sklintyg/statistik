package se.inera.statistics.spec

class SjukfallIRapportenSocialstyrelsen extends Rapport {

    def startår
    def slutår
    def diagnoser

    def diagnos
    def kön
    def län
    def sjukskrivningslängd

    void setStartår(startår) {
        this.startår = Integer.parseInt(startår)
    }

    void setSlutår(slutår) {
        this.slutår = Integer.parseInt(slutår)
    }

    void setDiagnoser(diagnoser) {
        if (diagnoser != null && !diagnoser.trim().isEmpty()) {
            this.diagnoser = diagnoser.split(",")*.trim().collect{ it }
        }
    }

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
        return reportsUtil.getSocialstyrelsenReport(startår, slutår, diagnoser);
    }

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
