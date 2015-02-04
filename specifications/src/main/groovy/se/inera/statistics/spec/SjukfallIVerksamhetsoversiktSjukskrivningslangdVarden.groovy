package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktSjukskrivningslangdVarden extends Rapport {

    String field
    String value

    void setField(String field) {
        this.field = field
    }

    def value() {
        return value
    }

    @Override
    public void doExecute() {
        def report = getVerksamhetsoversikt()
        value = report.sickLeaveLength[field]
    }

}
