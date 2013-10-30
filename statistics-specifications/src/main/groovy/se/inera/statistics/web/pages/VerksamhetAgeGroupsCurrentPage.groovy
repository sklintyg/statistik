package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetAgeGroupsCurrentPage extends DetailsPage {

    static at = { title == "Åldersgrupper | Nationella statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }

}
