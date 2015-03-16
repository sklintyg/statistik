package se.inera.statistics.spec

class SjukfallIRapportenLanDiagram extends LanRapport {

    public void doExecute() {
        def report = getReport()
        executeDiagram(report)
    }

}
