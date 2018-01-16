package se.inera.statistics.spec

class SjukfallIRapportenSocialstyrelsenSomMedian extends SjukfallIRapportenSocialstyrelsenSom {

    def getReport() {
        return reportsUtil.getSocialstyrelsenMedianReport(startår, slutår, diagnoser);
    }

}
