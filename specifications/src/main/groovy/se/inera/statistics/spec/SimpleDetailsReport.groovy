package se.inera.statistics.spec

abstract class SimpleDetailsReport extends Rapport {

    String år;
    String månad;

    public void executeDiagram(report) {
        def categoryNameMatcher = getRowNameMatcher();
        def index = report.chartData.categories.findIndexOf { item -> item == categoryNameMatcher }
        def male = report.chartData.series.find { item -> "Male".equals(item.sex) }
        män = male.data[index]
        def female = report.chartData.series.find { item -> "Female".equals(item.sex) }
        kvinnor = female.data[index]
        def total = report.chartData.series.find { item -> item.sex == null }
        totalt = total != null ? total.data[index] : -1
    }

    abstract def getRowNameMatcher()

    void executeTabell(report) {
        def rowNameMatcher = getRowNameMatcher();
        def row = report.tableData.rows.find { currentRow -> currentRow.name == rowNameMatcher }
        if (row == null) {
            totalt = -1
            kvinnor = -1
            män = -1
        } else {
            totalt = row.data[0]
            kvinnor = row.data[1]
            män = row.data[2]
        }
    }

    def getReportSjukfallTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(inloggadSom);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    def getReportLangaSjukfall() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Långa sjukfall- is not available on national level");
    }

    def getReportSjukfallPerEnhet() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhet(inloggadSom, kapitel, avsnitt, kategorier, enheter);
        }
        throw new RuntimeException("Report -Sjukfall per enhet- is not available on national level");
    }

    def getReportAldersgrupp() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppInloggad(inloggadSom);
        }
        return reportsUtil.getReportAldersgrupp();
    }

    def getReportAldersgruppPagaende() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppPagaendeInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Åldersgrupp pågående- is not available on national level");
    }

    def getReportSjukskrivningslangd() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdInloggad(inloggadSom);
        }
        return reportsUtil.getReportSjukskrivningslangd();
    }

    def getReportSjukskrivningslangdPagaende() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdPagaendeInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Sjukskrivningslängd pågående- is not available on national level");
    }

    def getReportLakareAlderOchKon() {
        if (inloggad) {
            return reportsUtil.getReportLakareAlderOchKonInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Läkare ålder och kön- is not available on national level");
    }

    def getReportLakarBefattning() {
        if (inloggad) {
            return reportsUtil.getReportLakarBefattningInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Läkarbefattning- is not available on national level");
    }

    def getReportSjukfallPerLakare() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerLakareInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Sjukfall per läkare- is not available on national level");
    }

    def getReportAndelSjukfallPerKon() {
        if (inloggad) {
            throw new RuntimeException("Report -Andel sjukfall per kön- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerSex();
    }

}
