package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerMonthPage extends DetailsPage {

    static at = { title == "Sjukfall per månad | Statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }
    
}
