package se.inera.statistics.spec

abstract class SimpleDetailsReport extends Rapport {

    String år;
    String månad;

    public void executeDiagram(report) {
        def categoryNameMatcher = getRowNameMatcher();
        def index = report.chartData.categories.findIndexOf { item -> item.contains(categoryNameMatcher) }
        def male = report.chartData.series.find { item -> "Male".equals(item.sex) }
        män = index < 0 ? -1 : male.data[index]
        def female = report.chartData.series.find { item -> "Female".equals(item.sex) }
        kvinnor = index < 0 ? -1 : female.data[index]
        def total = report.chartData.series.find { item -> item.sex == null }
        totalt = index < 0 ? -1 : (total != null ? total.data[index] : -1)
    }

    abstract def getRowNameMatcher()

    void executeTabell(report) {
        def rowNameMatcher = getRowNameMatcher();
        def row = report.tableData.rows.find { currentRow ->
            currentRow.name.contains(rowNameMatcher) }
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
            return reportsUtil.getReportAntalIntygInloggad(filter);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    def getReportLangaSjukfall() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallInloggad(filter);
        }
        throw new RuntimeException("Report -Långa sjukfall- is not available on national level");
    }

    def getReportSjukfallPerEnhet() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhet(filter);
        }
        throw new RuntimeException("Report -Sjukfall per enhet- is not available on national level");
    }

    def getReportAldersgrupp() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppInloggad(filter);
        }
        return reportsUtil.getReportAldersgrupp();
    }

    def getReportJamforDiagnoser(diagnoser) {
        def diagnosHash = reportsUtil.getFilterHash(null, null, diagnoser)
        if (inloggad) {
            return reportsUtil.getReportJamforDiagnoserInloggad(filter, diagnosHash);
        }
        throw new RuntimeException("Report -Jämför diagnoser- is not available on national level");
    }

    def getReportAldersgruppPagaende() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppPagaendeInloggad(filter);
        }
        throw new RuntimeException("Report -Åldersgrupp pågående- is not available on national level");
    }

    def getReportSjukskrivningslangd() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdInloggad(filter);
        }
        return reportsUtil.getReportSjukskrivningslangd();
    }

    def getReportSjukskrivningslangdPagaende() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdPagaendeInloggad(filter);
        }
        throw new RuntimeException("Report -Sjukskrivningslängd pågående- is not available on national level");
    }

    def getReportLakareAlderOchKon() {
        if (inloggad) {
            return reportsUtil.getReportLakareAlderOchKonInloggad(filter);
        }
        throw new RuntimeException("Report -Läkare ålder och kön- is not available on national level");
    }

    def getReportLakarBefattning() {
        if (inloggad) {
            return reportsUtil.getReportLakarBefattningInloggad(filter);
        }
        throw new RuntimeException("Report -Läkarbefattning- is not available on national level");
    }

    def getReportSjukfallPerLakare() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerLakareInloggad(filter);
        }
        throw new RuntimeException("Report -Sjukfall per läkare- is not available on national level");
    }

    def getReportAndelSjukfallPerKon() {
        if (inloggad) {
            throw new RuntimeException("Report -Andel sjukfall per kön- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerSex();
    }

    def getReportEnskiltDiagnoskapitel(kapitel) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(kapitel, filter);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(kapitel);
    }

    def getReportSjukfallTotaltTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygSomTvarsnittInloggad(filter);
        }
        throw new RuntimeException("Report -Sjukfall totalt som tvärsnitt- is not available on national level");
    }

    def getReportLangaSjukfallTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallSomTvarsnittInloggad(filter);
        }
        throw new RuntimeException("Report -Långa Sjukfall som tvärsnitt- is not available on national level");
    }

    def getReportSjukskrivningsgradTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradSomTvarsnittInloggad(filter);
        }
        throw new RuntimeException("Report -Sjukskrivningsgrad som tvärsnitt- is not available on national level");
    }

    def getReportDiagnosgruppTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportDiagnosgruppSomTvarsnittInloggad(filter);
        }
        throw new RuntimeException("Report -Diagnosgrupp som tvärsnitt- is not available on national level");
    }

}
