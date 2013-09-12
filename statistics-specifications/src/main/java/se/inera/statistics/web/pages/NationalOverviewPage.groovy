package se.inera.statistics.web.pages

import geb.Page

class NationalOverviewPage extends Page {

    static at = { nationalOverviewHeading.isDisplayed() }

    static content = {
        
        casesPerMonthProportionPerSexLink { $("#casesPerMonthProportionPerSexLink") }
        casesPerMonthFemaleProportionLabel { $("#overview-distribution-female-lbl") }
        casesPerMonthMaleProportionLabel { $("#overview-distribution-male-lbl") }

        casesPerMonthAlterationLink { $("#casesPerMonthAlterationLink") }
        casesPerMonthAlterationLabel { $("#casesPerMonthAlterationLabel") }
        nationalOverviewHeading { $("#nationalOverviewHeading") }

    }

    def goToCasesPerMonthProportionPerSexDetailPage() {
        casesPerMonthProportionPerSexLink.click()
    }

    def goToCasesPerMonthAlterationDetailsPage() {
        casesPerMonthAlterationLink.click()
    }

    def String getCasesPerMonthMaleProportion() {
        return casesPerMonthMaleProportionLabel.text()
    }

    def String getCasesPerMonthFemaleProportion() {
        return casesPerMonthFemaleProportionLabel.text()
    }

    def String getCasesPerMonthAlteration() {
        return casesPerMonthAlterationLabel.text()
    }
    
}
