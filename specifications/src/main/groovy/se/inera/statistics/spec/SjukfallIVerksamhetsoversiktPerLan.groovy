package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktPerLan extends VerksamhetsoversiktDiagnosDonut {

    def getData(report) {
        return report.perCounty
    }

}
