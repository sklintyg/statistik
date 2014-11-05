/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.pages

import geb.Page

class LoginPage extends Page {

    static at = { loginBtn.isDisplayed() }

    static content = {

        loginBtn { $("#login_btn") }
        
    }
    
    def login() {
        clickLogin()
    }
    
    def clickLogin() {
        loginBtn.click()
    }

}
