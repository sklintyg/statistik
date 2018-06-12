'use strict';

var fakeloginPo = require('../pages/pages.js').fakeloginPo;
var headerPo = require('../pages/pages.js').headerPo;

var User = function() {
    this.loginUser1 = function(processledare) {
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user1', 'vg1', processledare);
    };

    this.loginUser3 = function(processledare) {
        headerPo.clickLogin();
        fakeloginPo.login('Anna', 'Modig', 'user3', 'vg3', processledare);
    };

    this.loginUser5 = function(processledare) {
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
};


module.exports = new User();
