package se.inera.statistics.web.service;

public enum Report {

    N_SJUKFALLTOTALT(StatisticsLevel.NATIONELL, "SjukfallTotalt", "Sjukfall totalt"),
    N_DIAGNOSGRUPP(StatisticsLevel.NATIONELL, "Diagnosgrupp", "Diagnosgrupp"),
    N_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.NATIONELL,
            "DiagnosgruppEnskiltDiagnoskapitel", "Diagnosgrupp enskilt diagnoskapitel"),
    N_ALDERSGRUPP(StatisticsLevel.NATIONELL, "Aldersgrupp", "Åldersgrupp"),
    N_SJUKSKRIVNINGSGRAD(StatisticsLevel.NATIONELL, "Sjukskrivningsgrad", "Sjukskrivningsgrad"),
    N_SJUKSKRIVNINGSLANGD(StatisticsLevel.NATIONELL, "Sjukskrivningslangd", "Sjukskrivningslängd"),
    N_LAN(StatisticsLevel.NATIONELL, "Lan", "Län"),
    N_LANANDELSJUKFALLPERKON(StatisticsLevel.NATIONELL, "LanAndelSjukfallPerKon", "Län andel sjukfall per kön"),
    V_SJUKFALLTOTALT(StatisticsLevel.VERKSAMHET, "SjukfallTotalt", "Sjukfall totalt"),
    V_INTYGPERMANAD(StatisticsLevel.VERKSAMHET, "IntygPerManad", "Intyg per manad"),
    V_INTYGPERTYP(StatisticsLevel.VERKSAMHET, "IntygPerTyp", "Intyg per typ"),
    V_MEDDELANDENTOTALT(StatisticsLevel.VERKSAMHET, "MeddelandenTotalt", "Meddelanden totalt"),
    V_VARDENHET(StatisticsLevel.VERKSAMHET, "Vardenhet", "Vårdenhet"),
    V_SJUKFALLPERLAKARE(StatisticsLevel.VERKSAMHET, "SjukfallPerLakare", "Sjukfall per lakare"),
    V_DIAGNOSGRUPP(StatisticsLevel.VERKSAMHET, "Diagnosgrupp", "Diagnosgrupp"),
    V_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.VERKSAMHET,
            "DiagnosgruppEnskiltDiagnoskapitel", "Diagnosgrupp enskilt diagnoskapitel"),
    V_DIAGNOSGRUPPJAMFORVALFRIADIAGNOSER(StatisticsLevel.VERKSAMHET,
            "DiagnosgruppJamforValfriaDiagnoser", "Diagnosgrupp jämför valfria diagnoser"),
    V_ALDERSGRUPP(StatisticsLevel.VERKSAMHET, "Aldersgrupp", "Åldersgrupp"),
    V_LAKARALDEROCHKON(StatisticsLevel.VERKSAMHET, "LakaralderOchKon", "Läkarålder och kön"),
    V_LAKARBEFATTNING(StatisticsLevel.VERKSAMHET, "Lakarbefattning", "Läkarbefattning"),
    V_SJUKSKRIVNINGSGRAD(StatisticsLevel.VERKSAMHET, "Sjukskrivningsgrad", "Sjukskrivningsgrad"),
    V_SJUKSKRIVNINGSLANGD(StatisticsLevel.VERKSAMHET, "Sjukskrivningslangd", "Sjukskrivningslängd"),
    V_SJUKSKRIVNINGSLANGDMERAN90DAGAR(StatisticsLevel.VERKSAMHET, "SjukskrivningslangdMerAn90Dagar", "Sjukskrivningslängd mer än 90 dagar"),
    V_DIFFERENTIERATINTYGANDE(StatisticsLevel.VERKSAMHET, "DifferentieratIntygande", "Differentierat intygande"),
    L_SJUKFALLTOTALT(StatisticsLevel.LANDSTING, "SjukfallTotalt", "Sjukfall totalt"),
    L_VARDENHET(StatisticsLevel.LANDSTING, "Vardenhet", "Vårdenhet"),
    L_VARDENHETLISTNINGAR(StatisticsLevel.LANDSTING, "VardenhetListningar", "Vårdenhet listningar");

    private final StatisticsLevel statisticsLevel;
    private final String shortName;
    private final String longName;

    Report(StatisticsLevel statisticsLevel, String shortName, String longName) {
        this.statisticsLevel = statisticsLevel;
        this.shortName = shortName;
        this.longName = longName;
    }

    public StatisticsLevel getStatisticsLevel() {
        return statisticsLevel;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

}
