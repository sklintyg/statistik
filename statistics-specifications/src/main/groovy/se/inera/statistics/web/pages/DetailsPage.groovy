package se.inera.statistics.web.pages

import geb.Page
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.*

class DetailsPage extends Page {

    static content = {

        datatable(required:false, wait: false) { $("#datatable") }
        detailsOptions(required:false, wait: false) { $("#detailsOptionsDropdown") }
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
    
    def boolean isDetailsOptionsVisible() {
        return detailsOptions.displayed
    }
    
}
