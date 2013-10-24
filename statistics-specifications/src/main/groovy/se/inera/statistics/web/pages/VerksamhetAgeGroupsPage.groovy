package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetAgeGroupsPage extends DetailsPage {

    static at = { title == "Åldersgrupper | Statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }

    }

}
