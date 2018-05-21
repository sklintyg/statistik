package se.inera.statistics.web.service;

public class ReportInfo {

    private Report report;
    private ReportType reportType;

    public ReportInfo(Report report, ReportType reportType) {
        this.report = report;
        this.reportType = reportType;
    }

    public Report getReport() {
        return report;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public boolean isDualChartReport() {
        if (ReportType.TIDSSERIE.equals(reportType)) {
            switch (report) {
                case V_SJUKFALLTOTALT: //Fall through
                case L_SJUKFALLTOTALT: //Fall through
                case N_SJUKFALLTOTALT: //Fall through
                case V_SJUKSKRIVNINGSLANGDMERAN90DAGAR: //Fall through
                case V_VARDENHET: //Fall through
                case L_VARDENHET: //Fall through
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

}
