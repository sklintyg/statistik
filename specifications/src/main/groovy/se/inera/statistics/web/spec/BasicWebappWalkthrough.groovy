/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.spec

import geb.Browser
import se.inera.statistics.web.pages.*

public class BasicWebappWalkthrough {

    public void öppnaFörstasidan() {
        Browser.drive { go "/" }
    }

    public boolean nationellaÖversiktssidanVisas() {
        Browser.drive {
            waitFor { at NationalOverviewPage }
        }
    }
    
    public boolean sidanSjukfallPerMånadVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerMonthPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        return result
    }

    public boolean statistikFörKönsfördelningFörKvinnorVisas() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            waitDefault()
            String value = page.casesPerMonthFemaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }

    public boolean statistikFörKönsfördelningFörMänVisas() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            waitDefault()
            String value = page.casesPerMonthMaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }
    
    public boolean totalsummanFörKönsfördelningBlir100Procent() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            waitDefault()
            String femaleText = page.casesPerMonthFemaleProportion
            String maleText = page.casesPerMonthMaleProportion
            int femaleNumber = (Integer.parseInt(femaleText.replace("%", "")))
            int maleNumber = (Integer.parseInt(maleText.replace("%", "")))
            result = femaleNumber + maleNumber == 100
        }
        result
    }

    public boolean förändringAvAntalSjukfallÄrMellan0Och100Procent() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            waitDefault()
            String alterationText = page.casesPerMonthAlteration
            int alterationNumber = (Integer.parseInt(alterationText.replace("%", "")))
            result = alterationNumber >= 0 && alterationNumber <= 100
        }
        result
    }

    public void gåTillSjukfallstatistiksidanViaVänsterNavigationsmeny() {
        def result = false
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToCasesPerMonth()
        }
    }

    public void klickaPåDöljTabell() {
        Browser.drive {
            waitFor { at CasesPerMonthPage }
            waitDefault()
            page.toggleDataTableVisibility()
        }
    }

    public boolean tabellenVisasEj() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerMonthPage }
            waitDefault()
            result = !page.isDatatableVisible()
        }
        result
    }

    public void användVänsterNavigationsmenyFörAttGåTillbakaTillÖversiktssidan() {
        def result = false
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToOverview()
        }
    }
    
    public void klickaPåRubrikenFörDiagnosgrupper() {
        Browser.drive {
            waitFor { at NationalOverviewPage }
            waitDefault()
            page.clickDiagnosisGroupsHeader()
        }
    }
    
    public boolean detaljsidanFörDiagnosgrupperVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at DiagnosisGroupsPage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörUnderdiagnosgrupper() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToDiagnosisSubGroup()
        }
    }
    
    public boolean detaljsidanFörUnderdiagnosgrupperVisasMedTvåDiagramOchEnTabellOchEnDropdown() {
        def result = false
        Browser.drive {
            waitFor { at DiagnosisSubGroupsPage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörÅldersgrupp() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToAgeGroup()
        }
    }
    
    public boolean detaljsidanFörÅldersgrupperVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at AgeGroupsPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörSjukskrivningsgrad() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToSickLeaveDegree()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningsgradVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at DegreeOfSickLeavePage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }

    public void öppnaDetaljsidanFörSjukskrivningslängd() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToSickLeaveLength()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningslängdVisasMedEttDiagramOchEnTabell() {
        def result = false
                Browser.drive {
            waitFor { at SickLeaveLengthPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörLän() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToCasesPerCounty()
        }
    }
    
    public boolean detaljsidanFörLänVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerCountyPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörAndelSjukfallPerKön() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToCasesPerSex()
        }
    }
    
    public boolean detaljsidanFörAndelSjukfallPerKönVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerSexPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean loggaIn() {
        Browser.drive {
            waitFor { at PageHeader }
            waitDefault()
            page.clickLogin()
        }
        Browser.drive {
            waitFor { at LoginPage }
            waitDefault()
            page.login()
        }
    }
    
    public boolean översiktssidanFörVerksamhetVisas() {
        Browser.drive {
            waitFor(20) { at VerksamhetOverviewPage }
        }
    }
    
    public boolean öppnaDetaljsidanFörSjukfallTotaltFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetTotalCasesPage()
        }
    }
    
    public boolean sidanSjukfallTotaltFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetTotalCasesPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörDiagnosgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetDiagnosisGroupsPage()
        }
    }
    
    public boolean detaljsidanFörDiagnosgrupperFörVerksamhetVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetDiagnosisGroupsPage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörUnderdiagnosgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetSubDiagnosisGroupsPage()
        }
    }
    
    public boolean detaljsidanFörUnderdiagnosgrupperFörVerksamhetVisasMedTvåDiagramOchEnTabellOchEnDropdown() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSubDiagnosisGroupsPage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörÅldersgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetAgeGroupsPage()
        }
    }
    
    public boolean detaljsidanFörÅldersgrupperFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetAgeGroupsPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörPågåendeÅldersgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetAgeGroupsCurrentPage()
        }
    }
    
    public boolean detaljsidanFörPågåendeÅldersgrupperFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetAgeGroupsCurrentPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörSjukskrivningsgradFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToVerksamhetDegreeOfSickLeavePage()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningsgradFörVerksamhetVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetDegreeOfSickLeavePage }
            waitDefault()
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörSjukskrivningslängdFörVerksmahet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToSickLeaveLengthPage()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningslängdFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSickLeaveLengthPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörPågåendeSjukskrivningslängdFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToSickLeaveLengthCurrentPage()
        }
    }
    
    public boolean detaljsidanFörPågåendeSjukskrivningslängdFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSickLeaveLengthCurrentPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörLångaSjukskrivningarFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            waitDefault()
            page.goToLongSickLeavesPage()
        }
    }
    
    public boolean detaljsidanFörLångaSjukskrivningarFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetLongSickLeavesPage }
            waitDefault()
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }

    private void waitDefault() {
        wait(1000);
    }
        
    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
            
}
