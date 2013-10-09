package se.inera.statistics.web.model;

public class TableHeader {

    private final String text;
    private final int colspan;

    public TableHeader(String text, int colspan) {
        this.text = text;
        this.colspan = colspan;
    }

    public TableHeader(String text) {
        this(text, 1);
    }

    public String getText() {
        return text;
    }

    public int getColspan() {
        return colspan;
    }

    @Override
    public String toString() {
        return text + ";" + colspan;
    }
}
