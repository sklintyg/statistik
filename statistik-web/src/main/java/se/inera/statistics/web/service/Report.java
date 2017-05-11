package se.inera.statistics.web.service;

public enum Report {

    N_SJUKFALLTOTALT(StatisticsLevel.NATIONELL, "SjukfallTotalt", "Antal sjukfall"),
    N_DIAGNOSGRUPP(StatisticsLevel.NATIONELL, "Diagnosgrupp", "Antal sjukfall fördelat på diagnosgrupp"),
    N_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.NATIONELL,
            "DiagnosgruppEnskiltDiagnoskapitel", "Antal sjukfall för"),
    N_ALDERSGRUPP(StatisticsLevel.NATIONELL, "Aldersgrupp", "Antal sjukfall fördelat på åldersgrupp"),
    N_SJUKSKRIVNINGSGRAD(StatisticsLevel.NATIONELL, "Sjukskrivningsgrad", "Antal sjukfall fördelat på sjukskrivningsgrad"),
    N_SJUKSKRIVNINGSLANGD(StatisticsLevel.NATIONELL, "Sjukskrivningslangd", "Antal sjukfall fördelat på sjukskrivningslängd"),
    N_LAN(StatisticsLevel.NATIONELL, "Lan", "Antal sjukfall per 1000 invånare fördelat på län"),
    N_LANANDELSJUKFALLPERKON(StatisticsLevel.NATIONELL, "LanAndelSjukfallPerKon", "Könsfördelning per län"),
    V_SJUKFALLTOTALT(StatisticsLevel.VERKSAMHET, "SjukfallTotalt", "Antal sjukfall"),
    V_INTYGPERMANAD(StatisticsLevel.VERKSAMHET, "IntygPerManad", "Antal intyg"),
    V_INTYGPERTYP(StatisticsLevel.VERKSAMHET, "IntygPerTyp", "Antal intyg fördelat på typ"),
    V_MEDDELANDENTOTALT(StatisticsLevel.VERKSAMHET, "MeddelandenTotalt", "Antal inkomna meddelanden"),
    V_VARDENHET(StatisticsLevel.VERKSAMHET, "Vardenhet", "Antal sjukfall fördelat på vårdenhet"),
    V_SJUKFALLPERLAKARE(StatisticsLevel.VERKSAMHET, "SjukfallPerLakare", "Antal sjukfall fördelat på läkare"),
    V_DIAGNOSGRUPP(StatisticsLevel.VERKSAMHET, "Diagnosgrupp", "Antal sjukfall fördelat på diagnosgrupp"),
    V_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.VERKSAMHET,
            "DiagnosgruppEnskiltDiagnoskapitel", "Antal sjukfall för"),
    V_DIAGNOSGRUPPJAMFORVALFRIADIAGNOSER(StatisticsLevel.VERKSAMHET,
            "DiagnosgruppJamforValfriaDiagnoser", "Jämförelse av valfria diagnoser"),
    V_ALDERSGRUPP(StatisticsLevel.VERKSAMHET, "Aldersgrupp", "Antal sjukfall fördelat på åldersgrupp"),
    V_LAKARALDEROCHKON(StatisticsLevel.VERKSAMHET, "LakaralderOchKon", "Antal sjukfall fördelat på läkares ålder och kön"),
    V_LAKARBEFATTNING(StatisticsLevel.VERKSAMHET, "Lakarbefattning", "Antal sjukfall fördelat på läkarbefattning"),
    V_SJUKSKRIVNINGSGRAD(StatisticsLevel.VERKSAMHET, "Sjukskrivningsgrad", "Antal sjukfall fördelat på sjukskrivningsgrad"),
    V_SJUKSKRIVNINGSLANGD(StatisticsLevel.VERKSAMHET, "Sjukskrivningslangd", "Antal sjukfall fördelat på sjukskrivningslängd"),
    V_SJUKSKRIVNINGSLANGDMERAN90DAGAR(StatisticsLevel.VERKSAMHET,
            "SjukskrivningslangdMerAn90Dagar", "Antal sjukfall som är längre än 90 dagar"),
    V_DIFFERENTIERATINTYGANDE(StatisticsLevel.VERKSAMHET, "DifferentieratIntygande", "Antal sjukfall inom differentierat intygande"),
    L_SJUKFALLTOTALT(StatisticsLevel.LANDSTING, "SjukfallTotalt", "Antal sjukfall"),
    L_VARDENHET(StatisticsLevel.LANDSTING, "Vardenhet", "Antal sjukfall fördelat på vårdenhet"),
    L_VARDENHETLISTNINGAR(StatisticsLevel.LANDSTING, "VardenhetListningar", "Antal sjukfall per 1000 listningar fördelat på vårdenhet");

    private final StatisticsLevel statisticsLevel;
    private final String shortName; //e.g. used in file name, should therefore not contain spaces
    private final String longName; //e.g. used for title in excel export

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
