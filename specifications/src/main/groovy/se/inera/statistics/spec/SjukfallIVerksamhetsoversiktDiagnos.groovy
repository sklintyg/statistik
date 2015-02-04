package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktDiagnos extends VerksamhetsoversiktDiagnosDonut {

    def getData(report) {
        return report.diagnosisGroups
    }

}
