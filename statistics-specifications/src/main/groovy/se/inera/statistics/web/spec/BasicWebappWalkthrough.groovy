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
    
}
