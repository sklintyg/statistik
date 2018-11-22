'use strict';

var pohelper = require('./pohelper.js');

var NavMenu = function() {
    var that = this;
    this.nationalTab = element(by.id('tab-nationell'));
    this.verksamhetTab = element(by.id('tab-verksamhet'));
    this.landstingTab = element(by.id('tab-landsting'));

    this.nationalStatisticsToggle = element(by.id('national-statistics-toggle'));
    this.landstingStatisticsToggle = element(by.id('landsting-statistics-toggle'));
    this.businessStatisticsToggle = element(by.id('business-statistics-toggle'));
    this.aboutStatisticsToggle = element(by.id('about-statistics-toggle'));

    this.navOverviewLink = element(by.id('navOverviewLink'));
    this.navCasesPerMonthLink = element(by.id('navCasesPerMonthLink'));
    this.navDiagnosisGroupsLink = element(by.id('navDiagnosisGroupsLink'));
    this.navDiagnosisSubGroupsLink = element(by.id('navDiagnosisSubGroupsLink'));
    this.navAgeGroupsLink = element(by.id('navAgeGroupsLink'));
    this.navSickLeaveDegreeLink = element(by.id('navSickLeaveDegreeLink'));
    this.navSickLeaveLengthLink = element(by.id('navSickLeaveLengthLink'));
    this.navCountyLink = element(by.id('navCountyLink'));
    this.navCasesPerSexLink = element(by.id('navCasesPerSexLink'));

    this.navNationalIntygPerTypeLink = element(by.id('navNationalIntygPerTypeLink'));
    this.navNationalAndelKompletteringarLink = element(by.id('navNationalAndelKompletteringarLink'));

    this.navMessagesLink = element(by.id('navMessagesLink'));

    this.navLandstingAbout = element(by.id('navLandstingAboutLink'));
    this.navLandstingUpload = element(by.id('navLandstingUploadLink'));


    this.navVerksamhetOversiktLink = element(by.id('navVerksamhetOversiktLink'));

    this.navBusinessCasesPerBusinessLink = element(by.id('navBusinessCasesPerBusinessLink'));
    this.navBusinessCasesPerMonthLink = element(by.id('navBusinessCasesPerMonthLink'));
    this.navBusinessDiagnosisGroupsLink = element(by.id('navBusinessDiagnosisGroupsLink'));
    this.navBusinessDiagnosisSubGroupsLink = element(by.id('navBusinessDiagnosisSubGroupsLink'));
    this.navBusinessCompareDiagnosisLink = element(by.id('navBusinessCompareDiagnosisLink'));
    this.navBusinessAgeGroupsLink = element(by.id('navBusinessAgeGroupsLink'));
    this.navBusinessSickLeaveDegreeLink = element(by.id('navBusinessSickLeaveDegreeLink'));
    this.navBusinessSickLeaveLengthLink = element(by.id('navBusinessSickLeaveLengthLink'));
    this.navBusinessMoreNinetyDaysSickLeaveLink = element(by.id('navBusinessMoreNinetyDaysSickLeaveLink'));
    this.navBusinessCasesPerLakareLink = element(by.id('navBusinessCasesPerLakareLink')); //Not visible to processledare
    this.navBusinessCasesPerLakaresAlderOchKonLink = element(by.id('navBusinessCasesPerLakaresAlderOchKonLink'));
    this.navBusinessCasesPerLakarbefattningLink = element(by.id('navBusinessCasesPerLakarbefattningLink'));
    this.navBusinessMessagesLakareLink = element(by.id('navBusinessMessagesLakareLink')); //Not visible to processledare
    this.navBusinessMessagesEnhetLink = element(by.id('navBusinessMessagesEnhetLink'));

    this.navAboutTjanstLink = element(by.id('navAboutTjanstLink'));
    this.navAboutInloggningLink = element(by.id('navAboutInloggningLink'));
    this.navAboutFaqLink = element(by.id('navAboutFaqLink'));
    this.navAboutContactLink = element(by.id('navAboutContactLink'));

    this.clickOnMenu = function(id) {
        var menu = element(by.id(id));

        menu.click();
    };

    var clickLandstingStatisticsToggle = function() {
        that.landstingStatisticsToggle.click();
    };

    this.expandLandstingStatisticsToggle = function() {
        expandMenu(this.landstingStatisticsToggle, clickLandstingStatisticsToggle);
    };

    var clickBusinessStatisticsToggle = function() {
        that.businessStatisticsToggle.click();
    };

    this.expandBusinessStatisticsToggle = function() {
        expandMenu(this.businessStatisticsToggle, clickBusinessStatisticsToggle);
    };

    var clickNationalStatisticsToggle = function() {
        that.nationalStatisticsToggle.click();
    };

    this.expandNationalStatisticsToggle = function() {
        expandMenu(this.nationalStatisticsToggle, clickNationalStatisticsToggle);
    };

    var clickAboutStatisticsToggle = function() {
        that.aboutStatisticsToggle.click();
    };

    this.expandAboutStatisticsToggle = function() {
        expandMenu(this.aboutStatisticsToggle, clickAboutStatisticsToggle);
    };

    function expandMenu(menu, openFunction) {
        var icon = menu.element(by.css('.statistict-left-menu-expand-icon'));

        pohelper.hasClass(icon, 'collapsed').then(function(value) {
            if (value) {
                openFunction();
            }
        });
    }
};

module.exports = new NavMenu();
