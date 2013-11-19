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
