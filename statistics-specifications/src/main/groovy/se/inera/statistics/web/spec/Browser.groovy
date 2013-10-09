package se.inera.statistics.web.spec;

public class Browser {

    public void stäng() {
        geb.Browser.drive {
        }.quit()
    }
}
