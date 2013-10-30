package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetTotalCasesPage extends DetailsPage {

    static at = { title == "Sjukfall per månad | Nationella statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }
    
}
