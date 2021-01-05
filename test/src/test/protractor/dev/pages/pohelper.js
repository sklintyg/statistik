/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
