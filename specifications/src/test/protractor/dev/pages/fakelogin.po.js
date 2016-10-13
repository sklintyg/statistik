/* globals browser */

'use strict';

var jsonInput = element(by.name('userJsonDisplay'));
var loginBtn = element(by.id('login_btn'));

var login = function(firstName, lastName, userId, enhetId, vgId, isProcessledare) {
    jsonInput.sendKeys('{' +
        '"fornamn":"' + firstName + '",' +
        '"efternamn":"' + lastName + '",' +
        '"hsaId":"' + userId + '",' +
        '"enhetId":"' + enhetId + '",' +
        '"vardgivarId":"' + vgId + '",' +
        '"vardgivarniva":"' + isProcessledare + '"}');
    loginBtn.click();
};

var verifyAt = function() {
    expect(jsonInput.isDisplayed()).toBeTruthy('jsoninput-fältet saknas på fakeinloggningssidan');
    expect(loginBtn.isDisplayed()).toBeTruthy('inloggningsknappen saknas på fakeinloggningssidan');
};

module.exports = {
    'login': login,
    'verifyAt': verifyAt
};
