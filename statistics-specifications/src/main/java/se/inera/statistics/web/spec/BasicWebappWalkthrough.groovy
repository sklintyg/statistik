package se.inera.statistics.web.spec

import geb.Browser
import se.inera.statistics.web.pages.NationalOverviewPage
import se.inera.statistics.web.pages.NavigationMenuPage
import se.inera.statistics.web.pages.CasesPerMonthPage

public class BasicWebappWalkthrough {

    public void öppnaFörstasidan() {
        Browser.drive {
            go "/"
        }
    }

    public boolean nationellaÖversiktssidanVisas() {
        Browser.drive {
            waitFor {
                at NationalOverviewPage
            }
        }
    }

    public boolean sidanSjukfallPerMånadVisas() {
        Browser.drive {
            waitFor {
                at CasesPerMonthPage
            }
        }
    }
    
    public boolean statistikFörKönsfördelningFörKvinnorVisas() {
        def result = false
        Browser.drive {
            waitFor {
                at NationalOverviewPage
            }
            String value = page.casesPerMonthFemaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }

    public boolean statistikFörKönsfördelningFörMänVisas() {
        def result = false
        Browser.drive {
            waitFor {
                at NationalOverviewPage
            }
            String value = page.casesPerMonthMaleProportion
            result = value.matches("[0-9]+%")
        }
        result
    }
    
    public boolean totalsummanFörKönsfördelningBlir100Procent() {
        def result = false
        Browser.drive {
            waitFor {
                at NationalOverviewPage
            }
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
            waitFor {
                at NationalOverviewPage
            }
            String alterationText = page.casesPerMonthAlteration
            int alterationNumber = (Integer.parseInt(alterationText.replace("%", "")))
            result = alterationNumber >= 0 && alterationNumber <= 100
        }
        result
    }
    
    public void gåTillSjukfallstatistiksidanViaVänsterNavigationsmeny() {
        def result = false
        Browser.drive {
            waitFor {
                at NavigationMenuPage
            }
            page.goToCasesPerMonth()
        }
    }
    
}
