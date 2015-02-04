package se.inera.statistics.spec

class SjukfallIOversiktDiagnos extends OversiktDiagnosDonut {

    def getData(report) {
        return report.diagnosisGroups
    }

}
