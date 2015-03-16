package se.inera.statistics.spec

class SjukfallIOversiktSjukskrivningslangd extends OversiktDonutReport {

    def getData(report) {
        return report.sickLeaveLength.chartData
    }

}
