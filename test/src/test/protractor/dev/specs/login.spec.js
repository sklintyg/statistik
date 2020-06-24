/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

/*globals browser */
/*globals describe,it */
'use strict';

var testfw = require('../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;

describe('Tester kring inloggning: ', function() {

  beforeAll(function() {
    browser.get('/');
  });

  it('INTYG-3074: Inloggning behålls efter sidomladdning på nationell nivå', function() {
    features.user.makeSureNotLoggedIn();
    features.user.loginUser1(true);
    pages.navmenu.nationalTab.click();
    pages.navmenu.navCasesPerMonthLink.click();
    browser.refresh();
    pages.navmenu.verksamhetTab.click();
    pages.navmenu.navVerksamhetOversiktLink.click();
    pages.verksamhetOverview.isAtPage();
  });

  describe('Flera vårdgivare', function() {

    beforeAll(function() {
      features.user.makeSureNotLoggedIn();
      features.user.loginUser5(true);
    });

    it('Välj vårdgivare', function() {
      pages.selectVardgivare.isAtPage();

      expect(pages.selectVardgivare.list.count()).toBe(2);

      var link = pages.selectVardgivare.getVardgivareLink(0);
      var linkname = link.getText();

      link.click();
      pages.verksamhetOverview.isAtPage();

      expect(pages.headerPo.verksamhetsNameLabel.getText()).toEqual(linkname);
    });

    it('Byt vårdgivare', function() {
      expect(pages.headerPo.unitMenuBtn.isDisplayed()).toBeTruthy('Knapp för vårdenhetmeny syns inte');
      pages.headerPo.unitMenuBtn.click();
      expect(pages.headerPo.changeVardgivareBtn.isDisplayed()).toBeTruthy('Byt vårdgivare-knappen syns inte');
      pages.headerPo.changeVardgivareBtn.click();

      var link = pages.selectVardgivare.getVardgivareLink(1);
      var linkname = link.getText();

      link.click();

      expect(pages.headerPo.verksamhetsNameLabel.getText()).toEqual(linkname);
    });

  });

  afterAll(function() {
    features.user.makeSureNotLoggedIn();
  });

});
