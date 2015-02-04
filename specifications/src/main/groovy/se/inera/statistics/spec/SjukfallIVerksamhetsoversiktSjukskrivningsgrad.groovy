package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktSjukskrivningsgrad extends VerksamhetsoversiktDiagnosDonut {

    def getData(report) {
        return report.degreeOfSickLeaveGroups
    }

}
