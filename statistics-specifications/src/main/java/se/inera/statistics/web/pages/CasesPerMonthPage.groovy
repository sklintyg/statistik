package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By

class CasesPerMonthPage extends Page {

    static at = { title == "Sjukfall per månad | Statistiktjänsten" }

    static content = {

        chart { $("#container > div > svg") }
        datatable(required:false, wait: false) { $("#datatable") }
        toggleDataTableVisibilityBtn(required:true, wait: true) { $("#toggleDataTableVisibility") }

    }

    def toggleDataTableVisibility() {
        // Could not use geb toggleDataTableVisibilityBtn.click instead using standard selenium driver click
        Thread.sleep(1000);
        driver.findElement(By.id("toggleDataTableVisibility")).click();
        
    }

    def boolean isDatatableVisible() {
        return datatable.height != 0 && datatable.displayed;
    }
    
}
