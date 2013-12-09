package se.inera.statistics.web.pages

import geb.Page

class NationalOverviewPage extends Page {

    static at = { nationalOverviewHeading.isDisplayed() }

    static content = {

        casesPerMonthProportionPerSexLink { $("#casesPerMonthProportionPerSexLink") }
        casesPerMonthFemaleProportionLabel { $("#overview-distribution-female-lbl") }
        casesPerMonthMaleProportionLabel { $("#overview-distribution-male-lbl") }

        casesPerMonthAlterationLink { $("#casesPerMonthAlterationLink") }
        casesPerMonthAlterationLabel { $("#alterationChart text.highcharts-title") }

        diagnosisGroupsHeaderLink { $("#diagnosisGroupsHeaderLink") }
        ageGroupsHeaderLink { $("#ageGroupsHeaderLink") }
        degreeOfSickLeaveHeaderLink { $("#degreeOfSickLeaveHeaderLink") }
        sickLeaveLengthHeaderLink { $("#sickLeaveLengthHeaderLink") }
        perCountyHeaderLink { $("#perCountyHeaderLink") }

        nationalOverviewHeading { $("#nationalOverviewHeading") }

    }

    def clickCasesPerMonthProportionPerSexHeader() {
        casesPerMonthProportionPerSexLink.click()
    }

    def clickCasesPerMonthAlterationHeader() {
        casesPerMonthAlterationLink.click()
    }
    
    def clickDiagnosisGroupsHeader() {
        diagnosisGroupsHeaderLink.click()
    }
    
    def clickAgeGroupsHeader() {
        ageGroupsHeaderLink.click()
    }
    
    def clickDegreeOfSickLeaveHeader() {
        degreeOfSickLeaveHeaderLink.click()
    }
    
    def clickSickLeaveLengthHeader() {
        sickLeaveLengthHeaderLink.click()
    }
    
    def clickPerCountyHeader() {
        perCountyHeaderLink.click()
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
