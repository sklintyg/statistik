/* globals browser */
'use strict';

var elemUtil = require('../utils/utils.js').element;

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

var isElementPresentAndDisplayed = function(element) {
  return elemUtil.isElementPresentAndDisplayed(element);
};

var hasClass = function(element, cls) {
  return element.getAttribute('class').then(function(classes) {
    return classes.split(' ').indexOf(cls) !== -1
  });
};

module.exports = {
  'clickAndWaitForPageRedirect': clickAndWaitForPageRedirect,
  'hasClass': hasClass,
  'isElementPresentAndDisplayed': isElementPresentAndDisplayed
};
