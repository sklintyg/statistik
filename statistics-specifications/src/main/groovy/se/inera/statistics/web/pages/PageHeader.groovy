package se.inera.statistics.web.pages

import geb.Page

class PageHeader extends Page {

    static at = { pageHeaderContainer.isDisplayed() }

    static content = {

        pageHeaderContainer { $("#navigation-container") }
        loginBtn { $("#business-login-btn") }
        
    }
    
    def clickLogin() {
        loginBtn.click()
    }

}
