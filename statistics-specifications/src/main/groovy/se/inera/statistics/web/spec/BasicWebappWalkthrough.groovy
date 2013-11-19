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
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        return result
    }

    public boolean statistikFörKönsfördelningFörKvinnorVisas() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            String value = page.casesPerMonthFemaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }

    public boolean statistikFörKönsfördelningFörMänVisas() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
            String value = page.casesPerMonthMaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }
    
    public boolean totalsummanFörKönsfördelningBlir100Procent() {
        def result = false
        Browser.drive {
            waitFor { at NationalOverviewPage }
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
            page.goToCasesPerMonth()
        }
    }

    public void klickaPåDöljTabell() {
        Browser.drive {
            waitFor { at CasesPerMonthPage }
            page.toggleDataTableVisibility()
        }
    }

    public boolean tabellenVisasEj() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerMonthPage }
            result = !page.isDatatableVisible()
        }
        result
    }

    public void användVänsterNavigationsmenyFörAttGåTillbakaTillÖversiktssidan() {
        def result = false
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToOverview()
        }
    }
    
    public void klickaPåRubrikenFörDiagnosgrupper() {
        Browser.drive {
            waitFor { at NationalOverviewPage }
            page.clickDiagnosisGroupsHeader()
        }
    }
    
    public boolean detaljsidanFörDiagnosgrupperVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at DiagnosisGroupsPage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörUnderdiagnosgrupper() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToDiagnosisSubGroup()
        }
    }
    
    public boolean detaljsidanFörUnderdiagnosgrupperVisasMedTvåDiagramOchEnTabellOchEnDropdown() {
        def result = false
        Browser.drive {
            waitFor { at DiagnosisSubGroupsPage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörÅldersgrupp() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToAgeGroup()
        }
    }
    
    public boolean detaljsidanFörÅldersgrupperVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at AgeGroupsPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörSjukskrivningsgrad() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToSickLeaveDegree()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningsgradVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at DegreeOfSickLeavePage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }

    public void öppnaDetaljsidanFörSjukskrivningslängd() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToSickLeaveLength()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningslängdVisasMedEttDiagramOchEnTabell() {
        def result = false
                Browser.drive {
            waitFor { at SickLeaveLengthPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public void öppnaDetaljsidanFörLän() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToCasesPerCounty()
        }
    }
    
    public boolean detaljsidanFörLänVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerCountyPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörAndelSjukfallPerKön() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToCasesPerSex()
        }
    }
    
    public boolean detaljsidanFörAndelSjukfallPerKönVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at CasesPerSexPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean loggaIn() {
        Browser.drive {
            waitFor { at PageHeader }
            page.clickLogin()
        }
        Browser.drive {
            waitFor { at LoginPage }
            page.login()
        }
    }
    
    public boolean översiktssidanFörVerksamhetVisas() {
        Browser.drive {
            waitFor { at VerksamhetOverviewPage }
        }
    }
    
    public boolean öppnaDetaljsidanFörSjukfallTotaltFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetTotalCasesPage()
        }
    }
    
    public boolean sidanSjukfallTotaltFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetTotalCasesPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörDiagnosgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetDiagnosisGroupsPage()
        }
    }
    
    public boolean detaljsidanFörDiagnosgrupperFörVerksamhetVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetDiagnosisGroupsPage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörUnderdiagnosgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetSubDiagnosisGroupsPage()
        }
    }
    
    public boolean detaljsidanFörUnderdiagnosgrupperFörVerksamhetVisasMedTvåDiagramOchEnTabellOchEnDropdown() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSubDiagnosisGroupsPage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörÅldersgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetAgeGroupsPage()
        }
    }
    
    public boolean detaljsidanFörÅldersgrupperFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetAgeGroupsPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörPågåendeÅldersgruppFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetAgeGroupsCurrentPage()
        }
    }
    
    public boolean detaljsidanFörPågåendeÅldersgrupperFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetAgeGroupsCurrentPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörSjukskrivningsgradFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToVerksamhetDegreeOfSickLeavePage()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningsgradFörVerksamhetVisasMedTvåDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetDegreeOfSickLeavePage }
            result = page.chartFemale.isDisplayed() && page.chartMale.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörSjukskrivningslängdFörVerksmahet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToSickLeaveLengthPage()
        }
    }
    
    public boolean detaljsidanFörSjukskrivningslängdFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSickLeaveLengthPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörPågåendeSjukskrivningslängdFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToSickLeaveLengthCurrentPage()
        }
    }
    
    public boolean detaljsidanFörPågåendeSjukskrivningslängdFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetSickLeaveLengthCurrentPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
    
    public boolean öppnaDetaljsidanFörLångaSjukskrivningarFörVerksamhet() {
        Browser.drive {
            waitFor { at NavigationMenuPage }
            page.goToLongSickLeavesPage()
        }
    }
    
    public boolean detaljsidanFörLångaSjukskrivningarFörVerksamhetVisasMedEttDiagramOchEnTabell() {
        def result = false
        Browser.drive {
            waitFor { at VerksamhetLongSickLeavesPage }
            result = page.chart.isDisplayed() && page.isDatatableVisible() && !page.isDetailsOptionsVisible()
        }
        result
    }
            
}
