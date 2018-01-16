package se.inera.statistics.spec

class SjukfallIOversiktPerLan extends OversiktDonutReport {

    def getData(report) {
        return report.perCounty
    }

}
