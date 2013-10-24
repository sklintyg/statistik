package se.inera.statistics.web.pages

import geb.Page

class LoginPage extends Page {

    static at = { loginBtn.isDisplayed() }

    static content = {

        usernameInput { $("#j_username") }
        passwordInput { $("#j_password") }
        loginBtn { $("#login_btn") }
        
    }
    
    def login(username, password) {
        usernameInput.value(username)
        passwordInput.value(password)
        clickLogin()
    }
    
    def clickLogin() {
        loginBtn.click()
    }

}
