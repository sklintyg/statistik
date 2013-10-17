package se.inera.statistics.web.pages

import geb.Page

class NavigationMenuPage extends Page {

    static at = { casesPerMonthLink.isDisplayed() }

    static content = {

        overviewLink { $("#navOverviewLink") }
        casesPerMonthLink { $("#navCasesPerMonthLink") }
        diagnosisGroupsLink { $("#navDiagnosisGroupsLink") }
        diagnosisSubGroupsLink { $("#navDiagnosisSubGroupsLink") }
        ageGroupsLink { $("#navAgeGroupsLink") }
        sickLeaveDegreeLink { $("#navSickLeaveDegreeLink") }
        sickLeaveLengthLink { $("#navSickLeaveLengthLink") }
        countyLink { $("#navCountyLink") }
        perSexLink { $("#navCasesPerSexLink") }

    }

    def goToCasesPerMonth() {
        casesPerMonthLink.click()
    }
    
    def goToOverview() {
        overviewLink.click()
    }
    
    def goToDiagnosisGroup() {
        diagnosisGroupsLink.click()
    }
    
    def goToDiagnosisSubGroup() {
        diagnosisSubGroupsLink.click()
    }
    
    def goToAgeGroup() {
        ageGroupsLink.click()
    }
    
    def goToSickLeaveDegree() {
        sickLeaveDegreeLink.click()
    }
    
    def goToSickLeaveLength() {
        sickLeaveLengthLink.click()
    }
    
    def goToCasesPerCounty() {
        countyLink.click()
    }
    
    def goToPerSex() {
        perSexLink.click()
    }

}
