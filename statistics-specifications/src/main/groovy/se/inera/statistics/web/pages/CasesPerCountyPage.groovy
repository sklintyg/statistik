package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerCountyPage extends DetailsPage {

    static at = { title == "Län | Nationella statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
