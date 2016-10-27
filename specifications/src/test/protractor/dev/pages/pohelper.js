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

var isElementPresentAndDisplayed = function(elememt) {
    return elememt.isPresent().then(function() {
        return elememt.isDisplayed().then(function() {
            return true;
        }, function() {
            return false;
        });
    }, function() {
        return false;
    });
};

var hasClass = function (element, cls) {
    return browser.wait(element.getAttribute('class').then(function(classes) {
        return classes.split(' ').indexOf(cls) !== -1;
    }), 500);
};

module.exports = {
    'clickAndWaitForPageRedirect': clickAndWaitForPageRedirect,
    'hasClass': hasClass,
    'isElementPresentAndDisplayed': isElementPresentAndDisplayed
};
