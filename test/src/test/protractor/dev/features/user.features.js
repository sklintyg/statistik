'use strict';

var fakeloginPo = require('../pages/pages.js').fakeloginPo;
var headerPo = require('../pages/pages.js').headerPo;

var User = function() {
    this.loginUser1 = function(processledare) {
        this.setCookieConsentBannerState(true);
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user1', 'vg1', processledare);
    };

    this.loginUser2 = function(processledare) {
        this.setCookieConsentBannerState(true);
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user2', 'vg1', processledare);
    };

    this.loginUser3 = function(processledare) {
        this.setCookieConsentBannerState(true);
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user3', 'vg3', processledare);
    };

    this.loginUser5 = function(processledare) {
        this.setCookieConsentBannerState(true);
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user5', 'vg1', processledare);
    };

    this.isLoggedIn = function() {
        return headerPo.logoutLink.isPresent();
    };

    this.makeSureNotLoggedIn = function() {
        this.isLoggedIn().then(function(result) {
            if (result) {
                headerPo.clickLogout();
            }
        });

        expect(headerPo.loginBtn.isDisplayed()).toBeTruthy('Loginknappen syns ej');
    };

    this.setCookieConsentBannerState = function(state) {
        //If this flag is missing or set to false in localStorage the cookiebanner will appear.
        // We pre-set this before logging in to avoid having to click on that button
        //for every test.
        return browser.executeScript('window.localStorage.setItem("ngStorage-cookieBannerShown", "' + state + '");');
    };
};


module.exports = new User();
