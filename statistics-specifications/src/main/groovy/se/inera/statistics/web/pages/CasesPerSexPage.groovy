package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerSexPage extends DetailsPage {

    static at = { title == "Andel sjukfall per kön | Nationella statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }
    
}
