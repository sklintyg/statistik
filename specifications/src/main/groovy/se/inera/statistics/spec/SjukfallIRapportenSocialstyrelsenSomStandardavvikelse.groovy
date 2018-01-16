package se.inera.statistics.spec

class SjukfallIRapportenSocialstyrelsenSomStandardavvikelse extends SjukfallIRapportenSocialstyrelsenSom {

    def getReport() {
        return reportsUtil.getSocialstyrelsenStdDevReport(startår, slutår, diagnoser);
    }

}
