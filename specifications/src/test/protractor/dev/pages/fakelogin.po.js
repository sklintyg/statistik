/* globals browser */

'use strict';

var jsonInput = element(by.name('userJsonDisplay'));
var loginBtn = element(by.id('login_btn'));

var login = function(userId, enhetId, vgId, isProcessledare) {
    jsonInput.sendKeys('{' +
        '"fornamn":"Anna",' +
        '"efternamn":"Modig",' +
        '"hsaId":"' + userId + '",' +
        '"enhetId":"' + enhetId + '",' +
        '"vardgivarId":"' + vgId + '",' +
        '"vardgivarniva":"' + isProcessledare + '"}');
    loginBtn.click();
};

var loginUser1 = function() {
    login('user1', 'enhet1', 'vg1', true);
};

var verifyAt = function() {
    expect(jsonInput.isDisplayed()).toBeTruthy('jsoninput-fältet saknas på fakeinloggningssidan');
    expect(loginBtn.isDisplayed()).toBeTruthy('inloggningsknappen saknas på fakeinloggningssidan');
};

module.exports = {
    'login': login,
    'loginUser1': loginUser1,
    'verifyAt': verifyAt
};
