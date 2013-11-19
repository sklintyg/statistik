package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class SickLeaveLengthPage extends DetailsPage {

    static at = { title == "Sjukskrivningslängd | Statistiktjänst" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
