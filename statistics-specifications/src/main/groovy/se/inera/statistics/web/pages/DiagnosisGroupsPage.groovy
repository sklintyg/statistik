package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class DiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Diagnosgrupper | Statistiktjänsten" }

    static content = {

        chartFemale { $("#chart1 > div > svg") }
        chartMale { $("#chart2 > div > svg") }

    }

    
}
