package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class SickLeaveLengthPage extends DetailsPage {

    static at = { title == "Sjukskrivningslängd | Statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }

}
