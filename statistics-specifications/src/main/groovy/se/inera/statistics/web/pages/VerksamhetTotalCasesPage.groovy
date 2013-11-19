package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetTotalCasesPage extends DetailsPage {

    static at = { title == "Sjukfall per månad | Statistiktjänst" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }
    
}
