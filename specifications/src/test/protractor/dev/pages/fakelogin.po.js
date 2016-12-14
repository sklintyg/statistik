/* globals browser */

'use strict';

var jsonInput = element(by.name('userJsonDisplay'));
var loginBtn = element(by.id('login_btn'));

var login = function(firstName, lastName, userId, enhetId, vgId, isProcessledare) {
    jsonInput.clear().then(function() {
        jsonInput.sendKeys('{' +
            '"fornamn":"' + firstName + '",' +
            '"efternamn":"' + lastName + '",' +
            '"hsaId":"' + userId + '",' +
            '"vardgivarIdSomProcessLedare":["' + vgId + '"],' +
            '"vardgivarniva":"' + isProcessledare + '"}');
    }).then(function() {
        loginBtn.click();
    });
};

var verifyAt = function() {
    expect(jsonInput.isDisplayed()).toBeTruthy('jsoninput-fältet saknas på fakeinloggningssidan');
    expect(loginBtn.isDisplayed()).toBeTruthy('inloggningsknappen saknas på fakeinloggningssidan');
};

module.exports = {
    'login': login,
    'verifyAt': verifyAt
};
