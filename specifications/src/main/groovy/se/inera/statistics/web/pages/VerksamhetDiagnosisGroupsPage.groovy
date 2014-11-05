/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class VerksamhetDiagnosisGroupsPage extends DetailsPage {

    static at = { title == "Diagnosgrupper | Statistiktjänsten" }

    static content = {

        chartFemale { $("#chart1 > div > svg") }
        chartMale { $("#chart2 > div > svg") }

    }

    
}
