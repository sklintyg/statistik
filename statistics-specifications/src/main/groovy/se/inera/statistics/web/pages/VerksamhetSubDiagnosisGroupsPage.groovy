package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetSubDiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Enskilt diagnoskapitel | Statistiktjänst" }

    static content = {

        chartFemale { $("#chart1 > div > svg") }
        chartMale { $("#chart2 > div > svg") }

    }

    
}
