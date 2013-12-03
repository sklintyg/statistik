package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerCountyPage extends DetailsPage {

    static at = { title == "Län | Statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
