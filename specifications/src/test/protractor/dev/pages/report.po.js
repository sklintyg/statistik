/* globals browser */

'use strict';

var headerText = element(by.id('reportHeader'));

var clickLogin = function() {
    loginBtn.click();
};

module.exports = {
    'clickLogin': clickLogin
};
