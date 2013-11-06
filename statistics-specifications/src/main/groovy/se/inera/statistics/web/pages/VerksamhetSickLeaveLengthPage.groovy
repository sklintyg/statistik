package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetSickLeaveLengthPage extends DetailsPage {

    static at = { title == "Sjukskrivningslängd | Nationella statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
