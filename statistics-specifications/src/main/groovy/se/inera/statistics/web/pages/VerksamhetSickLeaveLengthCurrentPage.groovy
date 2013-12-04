package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetSickLeaveLengthCurrentPage extends DetailsPage {

    static at = { title == "Sjukskrivningslängd | Statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
