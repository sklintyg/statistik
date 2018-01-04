/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

var AboutPage = function() {
    this.about = element(by.id('about-about-page'));
    this.contact = element(by.id('about-contact-page'));
    this.login = element(by.id('about-login-page'));
    this.faq = element(by.id('about-faq-page'));

    this.isAtAbout = function() {
        expect(this.about.isDisplayed()).toBeTruthy('Är inte på about');
    };
    this.isAtContact = function() {
        expect(this.contact.isDisplayed()).toBeTruthy('Är inte på contact');
    };
    this.isAtLogin = function() {
        expect(this.login.isDisplayed()).toBeTruthy('Är inte på login');
    };
    this.isAtFaq = function() {
        expect(this.faq.isDisplayed()).toBeTruthy('Är inte på faq');
    };
};

module.exports = new AboutPage();
