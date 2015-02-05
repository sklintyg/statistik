package se.inera.statistics.spec

class SjukfallIOversiktSjukskrivningsgrad extends OversiktDonutReport {

    def getData(report) {
        return report.degreeOfSickLeaveGroups
    }

}
