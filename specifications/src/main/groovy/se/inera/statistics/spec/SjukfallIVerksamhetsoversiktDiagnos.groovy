package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktDiagnos extends Rapport {

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
        println("Grupp: ${grupp}")
        def row = report.diagnosisGroups.find { item -> item.name.contains(grupp) }
        println("Row: ${row}")
        antal = row.quantity
        förändring = row.alternation
    }

    def getVerksamhetsoversikt() {
        if (inloggad) {
            return reportsUtil.getVerksamhetsoversikt(inloggadSom, filter);
        }
        throw new RuntimeException("Report -Verksamhetsöversikt- is not available on national level");
    }

}
