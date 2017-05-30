package se.inera.statistics.web.model;

public final class LandstingsData extends SimpleDetailsData {

    private String fileUploadDate;

    public static LandstingsData create(SimpleDetailsData sdd, String fileUploadDate) {
        return new LandstingsData(sdd, fileUploadDate);
    }

    private LandstingsData(SimpleDetailsData sdd, String fileUploadDate) {
        super(sdd.getTableData(), sdd.getChartData(), sdd.getPeriod(), sdd.getFilter(), sdd.getMessages());
        this.fileUploadDate = fileUploadDate;
    }

    public String getFileUploadDate() {
        return fileUploadDate;
    }

}
