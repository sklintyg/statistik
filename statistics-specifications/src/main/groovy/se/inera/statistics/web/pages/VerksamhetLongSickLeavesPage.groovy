package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetLongSickLeavesPage extends DetailsPage {

    static at = { title == "Sjukskrivningslängd mer än 90 dagar | Nationella statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }

}
