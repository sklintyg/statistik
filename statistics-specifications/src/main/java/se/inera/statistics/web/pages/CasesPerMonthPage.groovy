package se.inera.statistics.web.pages

import geb.Page

class CasesPerMonthPage extends Page {

    static at = { title == "Sjukfall per månad | Statistiktjänsten" }

    static content = {
        
        chart { $("#container > div > svg") }
        datatable(required:false, wait: false) { $("#datatable") }
        toggleDataTableVisibilityBtn { $("#toggleDataTableVisibility") }

    }
    
    def toggleDataTableVisibility() {
        toggleDataTableVisibilityBtn.click()
    }
    
    def boolean isDatatableVisible(){
        return datatable.height != 0 && datatable.displayed;
    }

}
