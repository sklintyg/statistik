package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetAgeGroupsPage extends DetailsPage {

    static at = { title == "Åldersgrupper | Nationella statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }

}
