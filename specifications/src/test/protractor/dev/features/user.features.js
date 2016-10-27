/* globals browser */

'use strict';

var fakeloginPo = require('../pages/pages.js').fakeloginPo;
var headerPo = require('../pages/pages.js').headerPo;
var elemUtil = require('../utils/utils.js').element;

var loginUser1 = function(processledare) {
    headerPo.clickLogin().then(function() {
        fakeloginPo.login('Anna', 'Modig', 'user1', 'enhet1', 'vg1', processledare);
    });
};

var isLoggedIn = function() {
    return elemUtil.isElementPresentAndDisplayed(headerPo.logoutLink);
};

var makeSureNotLoggedIn = function() {
    console.log("In user.feature.makeSureNotLoggedIn");
    isLoggedIn().then(function(result) {
        if (result) {
            headerPo.clickLogout();
        }
    });
    elemUtil.isElementPresentAndDisplayed(headerPo.loginBtn).then(function(result) {
        expect(result).toBeTruthy('Loginknappen syns ej');
    });
};

module.exports = {
    'loginUser1': loginUser1,
    'isLoggedIn': isLoggedIn,
    'makeSureNotLoggedIn': makeSureNotLoggedIn
};
