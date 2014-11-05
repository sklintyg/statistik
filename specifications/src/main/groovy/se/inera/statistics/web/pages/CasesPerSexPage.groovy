/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerSexPage extends DetailsPage {

    static at = { title == "Andel sjukfall per kön | Statistiktjänsten" }

    static content = {

        chart { $("#chart1 > div > svg") }

    }
    
}
