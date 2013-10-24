package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetSubDiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Underdiagnosgrupper | Statistiktjänsten" }

    static content = {

        chartFemale { $("#container1 > div > svg") }
        chartMale { $("#container2 > div > svg") }

    }

    
}
