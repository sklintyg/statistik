package se.inera.statistics.spec

abstract class OversiktDiagnosDonut extends Rapport {

    String grupp
    String antal
    String förändring

    void setGrupp(String grupp) {
        this.grupp = grupp
    }

    def antal() {
        return antal
    }

    def förändring() {
        return förändring
    }

    @Override
    public void doExecute() {
        def report = getVerksamhetsoversikt()
        executeTabell(report)
    }

    public void executeTabell(report) {
        def row = getData(report).find { item -> item.name.contains(grupp) }
        antal = row.quantity
        förändring = row.alternation
    }

    abstract def getData(report);

}
