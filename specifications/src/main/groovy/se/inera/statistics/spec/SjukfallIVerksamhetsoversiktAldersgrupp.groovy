package se.inera.statistics.spec

class SjukfallIVerksamhetsoversiktAldersgrupp extends VerksamhetsoversiktDiagnosDonut {

    def getData(report) {
        return report.ageGroups
    }

}
