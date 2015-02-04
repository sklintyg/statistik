package se.inera.statistics.spec

class SjukfallIOversiktPerLan extends OversiktDiagnosDonut {

    def getData(report) {
        return report.perCounty
    }

}
