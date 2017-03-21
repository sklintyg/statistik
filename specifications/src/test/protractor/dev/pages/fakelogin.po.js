'use strict';

var FakeLoginPage = function() {
    this.jsonInput = element(by.name('userJsonDisplay'));
    this.loginBtn = element(by.id('login_btn'));

    this.login = function(firstName, lastName, userId, vgId, isProcessledare) {
        this.jsonInput.clear();
        this.jsonInput.sendKeys('{' +
                '"fornamn":"' + firstName + '",' +
                '"efternamn":"' + lastName + '",' +
                '"hsaId":"' + userId + '",' +
                '"vardgivarIdSomProcessLedare":["' + vgId + '"],' +
                '"vardgivarniva":"' + isProcessledare + '"}');

        this.loginBtn.click();
    };

    this.isAtPage = function() {
        expect(this.jsonInput.isDisplayed()).toBeTruthy('jsoninput-fältet saknas på fakeinloggningssidan');
        expect(this.loginBtn.isDisplayed()).toBeTruthy('inloggningsknappen saknas på fakeinloggningssidan');
    };
};

module.exports = new FakeLoginPage();
