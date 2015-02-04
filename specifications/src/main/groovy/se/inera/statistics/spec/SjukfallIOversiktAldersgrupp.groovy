package se.inera.statistics.spec

class SjukfallIOversiktAldersgrupp extends OversiktDiagnosDonut {

    def getData(report) {
        return report.ageGroups
    }

}
