package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class DiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Diagnosgrupper | Statistiktjänsten" }

    static content = {

        chartFemale { $("#container1 > div > svg") }
        chartMale { $("#container2 > div > svg") }

    }

    
}
