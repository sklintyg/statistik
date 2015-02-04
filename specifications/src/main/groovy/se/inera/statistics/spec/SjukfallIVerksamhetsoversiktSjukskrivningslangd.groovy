package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktSjukskrivningslangd extends VerksamhetsoversiktDiagnosDonut {

    def getData(report) {
        return report.sickLeaveLength.chartData
    }

}
