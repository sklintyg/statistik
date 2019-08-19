/* globals browser */

'use strict';

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

module.exports = {
  'isElementPresentAndDisplayed': isElementPresentAndDisplayed
};
