package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class DegreeOfSickLeavePage extends DetailsPage {

    static at = { title == "Sjukskrivningsgrad | Statistiktjänsten" }

    static content = {

        chartFemale { $("#chart1 > div > svg") }
        chartMale { $("#chart2 > div > svg") }
    }

}
