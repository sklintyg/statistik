package se.inera.statistics.spec

class SjukfallIOversiktAldersgrupp extends OversiktDonutReport {

    def getData(report) {
        return report.ageGroups
    }

}
