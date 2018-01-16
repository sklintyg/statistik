package se.inera.statistics.spec

class SjukfallIOversiktDiagnos extends OversiktDonutReport {

    def getData(report) {
        return report.diagnosisGroups
    }

}
