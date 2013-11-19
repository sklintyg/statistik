package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetAgeGroupsCurrentPage extends DetailsPage {

    static at = { title == "Åldersgrupper | Statistiktjänst" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
