/* globals browser */
'use strict';

var clickAndWaitForPageRedirect = function(element) {
    browser.ignoreSynchronization = true;
    browser.actions().mouseMove(element).click().perform().then(function() {
        return browser.driver.wait(function() {
            return browser.driver.getCurrentUrl().then(function(url) {
                return url.includes("#");
            });
        }, 10000);
    });
    browser.ignoreSynchronization = false;
};

module.exports = {
    'clickAndWaitForPageRedirect': clickAndWaitForPageRedirect
};
