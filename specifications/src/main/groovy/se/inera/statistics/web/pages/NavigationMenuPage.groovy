/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.pages

import geb.Page

class NavigationMenuPage extends Page {

    static at = { navigationMenu.isDisplayed() }

    static content = {

        navigationMenu { $("#statistics-menu-accordion") }
        overviewLink { $("#navOverviewLink") }
        casesPerMonthLink { $("#navCasesPerMonthLink") }
        diagnosisGroupsLink { $("#navDiagnosisGroupsLink") }
        diagnosisSubGroupsLink { $("#navDiagnosisSubGroupsLink") }
        ageGroupsLink { $("#navAgeGroupsLink") }
        sickLeaveDegreeLink { $("#navSickLeaveDegreeLink") }
        sickLeaveLengthLink { $("#navSickLeaveLengthLink") }
        countyLink { $("#navCountyLink") }
        perSexLink { $("#navCasesPerSexLink") }
        verksamhetTotalCasesLink { $("#navBusinessCasesPerMonthLink") }
        verksamhetDiagnosisGroupsLink { $("#navBusinessDiagnosisGroupsLink") }
        verksamhetSubDiagnosisGroupsLink { $("#navBusinessDiagnosisSubGroupsLink") }
        verksamhetAgeGroupsLink { $("#navBusinessAgeGroupsLink") }
        verksamhetAgeGroupsCurrentLink { $("#navBusinessOngoingAndCompletedLink") }
        verksamhetDegreeOfSickLeaveLink { $("#navBusinessSickLeaveDegreeLink") }
        verksamhetSickLeaveLenghtLink { $("#navBusinessSickLeaveLengthLink") }
        verksamhetSickLeaveLenghtCurrentLink { $("#navBusinessOngoingAndCompletedSickLeaveLink") }
        verksamhetLongSickLeaveLink { $("#navBusinessMoreNinetyDaysSickLeaveLink") }
        
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
    
    def goToCasesPerSex() {
        perSexLink.click()
    }

    def goToVerksamhetTotalCasesPage() {
        verksamhetTotalCasesLink.click()
    }
    
    def goToVerksamhetDiagnosisGroupsPage() {
        verksamhetDiagnosisGroupsLink.click()
    }
    
    def goToVerksamhetSubDiagnosisGroupsPage() {
        verksamhetSubDiagnosisGroupsLink.click()
    }
    
    def goToVerksamhetAgeGroupsPage() {
        verksamhetAgeGroupsLink.click()
    }
    
    def goToVerksamhetAgeGroupsCurrentPage() {
        verksamhetAgeGroupsCurrentLink.click()
    }
    
    def goToVerksamhetDegreeOfSickLeavePage() {
        verksamhetDegreeOfSickLeaveLink.click()
    }
    
    def goToSickLeaveLengthPage() {
        verksamhetSickLeaveLenghtLink.click()
    }
    
    def goToSickLeaveLengthCurrentPage() {
        verksamhetSickLeaveLenghtCurrentLink.click()
    }
    
    def goToLongSickLeavesPage() {
        verksamhetLongSickLeaveLink.click()
    }

}
