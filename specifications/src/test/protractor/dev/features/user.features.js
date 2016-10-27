/* globals browser */

'use strict';

var fakeloginPo = require('../pages/pages.js').fakeloginPo;
var headerPo = require('../pages/pages.js').headerPo;

var loginUser1 = function(processledare) {
    headerPo.clickLogin();
    fakeloginPo.login('Anna', 'Modig', 'user1', 'enhet1', 'vg1', processledare);
};

var isLoggedIn = function() {
    return headerPo.logoutLink.isPresent() && headerPo.logoutLink.isDisplayed();
};

var makeSureNotLoggedIn = function() {
    console.log("In user.feature.makeSureNotLoggedIn");
    if (isLoggedIn()) {
        headerPo.clickLogout();
    }
    expect(headerPo.loginBtn.isPresent() && headerPo.loginBtn.isDisplayed()).toBeTruthy('Loginknappen syns ej');
};

module.exports = {
    'loginUser1': loginUser1,
    'isLoggedIn': isLoggedIn,
    'makeSureNotLoggedIn': makeSureNotLoggedIn
};
