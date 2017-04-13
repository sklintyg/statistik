package se.inera.statistics.spec

abstract class OversiktDonutReport extends Rapport {

    String grupp
    String antal
    String förändring
    String färg

    void setGrupp(String grupp) {
        this.grupp = grupp
    }

    def antal() {
        return antal
    }

    def förändring() {
        return förändring
    }

    def färg() {
        return färg
    }

    @Override
    public void doExecute() {
        def report = getVerksamhetsoversikt()
        executeTabell(report)
    }

    public void executeTabell(report) {
        executeWithReport(report)
        def row = getData(report).find { item -> item.name.contains(grupp) }
        antal = row != null ? row.quantity : -1
        förändring = row != null ? row.alternation : -1
        färg = row != null ? row.color : null
    }

    abstract def getData(report);

}
