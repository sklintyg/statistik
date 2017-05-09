package se.inera.statistics.web.service;

public enum StatisticsLevel {

    NATIONELL("Nationell statistik"),
    VERKSAMHET("Verksamhetsstatistik"),
    LANDSTING("Landstingsstatistik");

    private final String text;

    StatisticsLevel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
