/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

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
