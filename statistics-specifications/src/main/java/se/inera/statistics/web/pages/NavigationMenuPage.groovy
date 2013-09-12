package se.inera.statistics.web.pages

import geb.Page

class NavigationMenuPage extends Page {

    static at = { casesPerMonthLink.isDisplayed() }

    static content = {
        
        casesPerMonthLink { $("#navCasesPerMonthLink") }

    }

    def goToCasesPerMonth() {
        casesPerMonthLink.click()
    }
    
}
