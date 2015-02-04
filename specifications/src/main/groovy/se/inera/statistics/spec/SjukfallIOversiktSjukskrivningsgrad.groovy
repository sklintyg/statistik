package se.inera.statistics.spec

class SjukfallIOversiktSjukskrivningsgrad extends OversiktDiagnosDonut {

    def getData(report) {
        return report.degreeOfSickLeaveGroups
    }

}
