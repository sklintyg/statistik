package se.inera.statistics.spec

class SjukfallIOversiktSjukskrivningslangd extends OversiktDiagnosDonut {

    def getData(report) {
        return report.sickLeaveLength.chartData
    }

}
