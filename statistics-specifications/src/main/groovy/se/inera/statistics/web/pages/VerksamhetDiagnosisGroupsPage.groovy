package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetDiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Diagnosgrupper | Nationella statistiktjänsten" }

    static content = {

        chartFemale { $("#container1 > div > svg") }
        chartMale { $("#container2 > div > svg") }

    }

    
}
