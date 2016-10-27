/* globals browser */

'use strict';

var nationalStatisticsToggle = element(by.id('national-statistics-toggle'));
var businessStatisticsToggle = element(by.id('business-statistics-toggle'));
var aboutStatisticsToggle = element(by.id('about-statistics-toggle'));

var navOverviewLink = element(by.id('navOverviewLink'));
var navCasesPerMonthLink = element(by.id('navCasesPerMonthLink'));
var navDiagnosisGroupsLink = element(by.id('navDiagnosisGroupsLink'));
var navDiagnosisSubGroupsLink = element(by.id('navDiagnosisSubGroupsLink'));
var navAgeGroupsLink = element(by.id('navAgeGroupsLink'));
var navSickLeaveDegreeLink = element(by.id('navSickLeaveDegreeLink'));
var navSickLeaveLengthLink = element(by.id('navSickLeaveLengthLink'));
var navCountyLink = element(by.id('navCountyLink'));
var navCasesPerSexLink = element(by.id('navCasesPerSexLink'));
var navVerksamhetOversiktLink = element(by.id('navVerksamhetOversiktLink'));

var navBusinessCasesPerBusinessLink = element(by.id('navBusinessCasesPerBusinessLink'));
var navBusinessCasesPerMonthLink = element(by.id('navBusinessCasesPerMonthLink'));
var navBusinessDiagnosisGroupsLink = element(by.id('navBusinessDiagnosisGroupsLink'));
var navBusinessDiagnosisSubGroupsLink = element(by.id('navBusinessDiagnosisSubGroupsLink'));
var navBusinessCompareDiagnosisLink = element(by.id('navBusinessCompareDiagnosisLink'));
var navBusinessAgeGroupsLink = element(by.id('navBusinessAgeGroupsLink'));
var navBusinessSickLeaveDegreeLink = element(by.id('navBusinessSickLeaveDegreeLink'));
var navBusinessSickLeaveLengthLink = element(by.id('navBusinessSickLeaveLengthLink'));
var navBusinessMoreNinetyDaysSickLeaveLink = element(by.id('navBusinessMoreNinetyDaysSickLeaveLink'));
var navBusinessCasesPerLakareLink = element(by.id('navBusinessCasesPerLakareLink')); //Not visible to processledare
var navBusinessCasesPerLakaresAlderOchKonLink = element(by.id('navBusinessCasesPerLakaresAlderOchKonLink'));
var navBusinessCasesPerLakarbefattningLink = element(by.id('navBusinessCasesPerLakarbefattningLink'));
var navBusinessDifferentieratIntygandeLink = element(by.id('navBusinessDifferentieratIntygandeLink'));

var navAboutTjanstLink = element(by.id('navAboutTjanstLink'));
var navAboutInloggningLink = element(by.id('navAboutInloggningLink'));
var navAboutFaqLink = element(by.id('navAboutFaqLink'));
var navAboutContactLink = element(by.id('navAboutContactLink'));
var pohelper = require('./pohelper.js');

var verifyAt = function() {
    expect(isBusinessStatisticsToggleVisible()).toBeTruthy('Nationell statistik saknas i navigationsmenyn');
    expect(isNationalStatisticsToggleVisible()).toBeTruthy('Verksamhetsstatistik saknas i navigationsmenyn');
    expect(isAboutStatisticsToggleVisible()).toBeTruthy('Om tjänsten saknas i navigationsmenyn');
};

var clickLogin = function() {
    loginBtn.click();
};

var clickBusinessStatisticsToggle = function() {
    businessStatisticsToggle.click();
};

var expandBusinessStatisticsToggle = function() {
    return pohelper.hasClass(businessStatisticsToggle, 'collapsed').then(function(value) {
        if (value) {
            clickBusinessStatisticsToggle();
        }
    });
};

var isBusinessStatisticsToggleVisible = function() {
    businessStatisticsToggle.isDisplayed();
};

var clickNationalStatisticsToggle = function() {
    nationalStatisticsToggle.click();
};

var expandNationalStatisticsToggle = function() {
    console.log("in page.navmenu.expandNationalStatisticsToggle");
    return pohelper.hasClass(nationalStatisticsToggle, 'collapsed').then(function(value) {
        if (value) {
        clickNationalStatisticsToggle();
    }
    });
};

var isNationalStatisticsToggleVisible = function() {
    nationalStatisticsToggle.isDisplayed();
};

var clickAboutStatisticsToggle = function() {
    aboutStatisticsToggle.click();
};

var expandAboutStatisticsToggle = function() {
    if (!isNavAboutTjanstLinkVisible()) {
        clickAboutStatisticsToggle();
    }
};

var isAboutStatisticsToggleVisible = function() {
    return aboutStatisticsToggle.isDisplayed();
};

var clickNavOverviewLink = function() {
    navOverviewLink.click();
};

var isNavOverviewLinkVisible = function() {
    navOverviewLink.isDisplayed();
};

var clickNavCasesPerMonthLink = function() {
    navCasesPerMonthLink.click();
};

var isNavCasesPerMonthLinkVisible = function() {
    return pohelper.isElementPresentAndDisplayed(navCasesPerMonthLink);
};

var clickNavDiagnosisGroupsLink = function() {
    navDiagnosisGroupsLink.click();
};

var isNavDiagnosisGroupsLinkVisible = function() {
    navDiagnosisGroupsLink.isDisplayed();
};

var clickNavDiagnosisSubGroupsLink = function() {
    navDiagnosisSubGroupsLink.click();
};

var isNavDiagnosisSubGroupsLinkVisible = function() {
    navDiagnosisSubGroupsLink.isDisplayed();
};

var clickNavAgeGroupsLink = function() {
    navAgeGroupsLink.click();
};

var isNavAgeGroupsLinkVisible = function() {
    navAgeGroupsLink.isDisplayed();
};

var clickNavSickLeaveDegreeLink = function() {
    navSickLeaveDegreeLink.click();
};

var isNavSickLeaveDegreeLinkVisible = function() {
    navSickLeaveDegreeLink.isDisplayed();
};

var clickNavSickLeaveLengthLink = function() {
    navSickLeaveLengthLink.click();
};

var isNavSickLeaveLengthLinkVisible = function() {
    navSickLeaveLengthLink.isDisplayed();
};

var clickNavCountyLink = function() {
    navCountyLink.click();
};

var isNavCountyLinkVisible = function() {
    navCountyLink.isDisplayed();
};

var clickNavCasesPerSexLink = function() {
    navCasesPerSexLink.click();
};

var isNavCasesPerSexLinkVisible = function() {
    navCasesPerSexLink.isDisplayed();
};

var clickNavVerksamhetOversiktLink = function() {
    navVerksamhetOversiktLink.click();
};

var isNavVerksamhetOversiktLinkVisible = function() {
    navVerksamhetOversiktLink.isDisplayed();
};

var clickNavBusinessCasesPerBusinessLink = function() {
    navBusinessCasesPerBusinessLink.click();
};

var isNavBusinessCasesPerBusinessLinkVisible = function() {
    navBusinessCasesPerBusinessLink.isDisplayed();
};

var clickNavBusinessCasesPerMonthLink = function() {
    navBusinessCasesPerMonthLink.click();
};

var isNavBusinessCasesPerMonthLinkVisible = function() {
    navBusinessCasesPerMonthLink.isDisplayed();
};

var clickNavBusinessDiagnosisGroupsLink = function() {
    navBusinessDiagnosisGroupsLink.click();
};

var isNavBusinessDiagnosisGroupsLinkVisible = function() {
    navBusinessDiagnosisGroupsLink.isDisplayed();
};

var clickNavBusinessDiagnosisSubGroupsLink = function() {
    navBusinessDiagnosisSubGroupsLink.click();
};

var isNavBusinessDiagnosisSubGroupsLinkVisible = function() {
    navBusinessDiagnosisSubGroupsLink.isDisplayed();
};

var clickNavBusinessCompareDiagnosisLink = function() {
    navBusinessCompareDiagnosisLink.click();
};

var isNavBusinessCompareDiagnosisLinkVisible = function() {
    navBusinessCompareDiagnosisLink.isDisplayed();
};

var clickNavBusinessAgeGroupsLink = function() {
    navBusinessAgeGroupsLink.click();
};

var isNavBusinessAgeGroupsLinkVisible = function() {
    navBusinessAgeGroupsLink.isDisplayed();
};

var clickNavBusinessSickLeaveDegreeLink = function() {
    navBusinessSickLeaveDegreeLink.click();
};

var isNavBusinessSickLeaveDegreeLinkVisible = function() {
    navBusinessSickLeaveDegreeLink.isDisplayed();
};

var clickNavBusinessSickLeaveLengthLink = function() {
    navBusinessSickLeaveLengthLink.click();
};

var isNavBusinessSickLeaveLengthLinkVisible = function() {
    navBusinessSickLeaveLengthLink.isDisplayed();
};

var clickNavBusinessMoreNinetyDaysSickLeaveLink = function() {
    navBusinessMoreNinetyDaysSickLeaveLink.click();
};

var isNavBusinessMoreNinetyDaysSickLeaveLinkVisible = function() {
    navBusinessMoreNinetyDaysSickLeaveLink.isDisplayed();
};

var clickNavBusinessCasesPerLakareLink = function() {
    navBusinessCasesPerLakareLink.click();
};

var isNavBusinessCasesPerLakareLinkVisible = function() {
    navBusinessCasesPerLakareLink.isDisplayed();
};

var clickNavBusinessCasesPerLakaresAlderOchKonLink = function() {
    navBusinessCasesPerLakaresAlderOchKonLink.click();
};

var isNavBusinessCasesPerLakaresAlderOchKonLinkVisible = function() {
    navBusinessCasesPerLakaresAlderOchKonLink.isDisplayed();
};

var clickNavBusinessCasesPerLakarbefattningLink = function() {
    navBusinessCasesPerLakarbefattningLink.click();
};

var isNavBusinessCasesPerLakarbefattningLinkVisible = function() {
    navBusinessCasesPerLakarbefattningLink.isDisplayed();
};

var clickNavBusinessDifferentieratIntygandeLink = function() {
    navBusinessDifferentieratIntygandeLink.click();
};

var isNavBusinessDifferentieratIntygandeLinkVisible = function() {
    navBusinessDifferentieratIntygandeLink.isDisplayed();
};

var clickNavAboutTjanstLink = function() {
    navAboutTjanstLink.click();
};

var isNavAboutTjanstLinkVisible = function() {
    navAboutTjanstLink.isDisplayed();
};

var clickNavAboutInloggningLink = function() {
    navAboutInloggningLink.click();
};

var isNavAboutInloggningLinkVisible = function() {
    navAboutInloggningLink.isDisplayed();
};

var clickNavAboutFaqLink = function() {
    navAboutFaqLink.click();
};

var isNavAboutFaqLinkVisible = function() {
    navAboutFaqLink.isDisplayed();
};

var clickNavAboutContactLink = function() {
    navAboutContactLink.click();
};

var isNavAboutContactLinkVisible = function() {
    navAboutContactLink.isDisplayed();
};

module.exports = {
    'verifyAt' : verifyAt,
    'clickBusinessStatisticsToggle' : clickBusinessStatisticsToggle,
    'isBusinessStatisticsToggleVisible' : isBusinessStatisticsToggleVisible,
    'clickNationalStatisticsToggle' : clickNationalStatisticsToggle,
    'expandNationalStatisticsToggle' : expandNationalStatisticsToggle,
    'expandBusinessStatisticsToggle' : expandBusinessStatisticsToggle,
    'expandAboutStatisticsToggle' : expandAboutStatisticsToggle,
    'isNationalStatisticsToggleVisible' : isNationalStatisticsToggleVisible,
    'clickAboutStatisticsToggle' : clickAboutStatisticsToggle,
    'isAboutStatisticsToggleVisible' : isAboutStatisticsToggleVisible,
    'clickNavOverviewLink' : clickNavOverviewLink,
    'isNavOverviewLinkVisible' : isNavOverviewLinkVisible,
    'clickNavCasesPerMonthLink' : clickNavCasesPerMonthLink,
    'isNavCasesPerMonthLinkVisible' : isNavCasesPerMonthLinkVisible,
    'clickNavDiagnosisGroupsLink' : clickNavDiagnosisGroupsLink,
    'isNavDiagnosisGroupsLinkVisible' : isNavDiagnosisGroupsLinkVisible,
    'clickNavDiagnosisSubGroupsLink' : clickNavDiagnosisSubGroupsLink,
    'isNavDiagnosisSubGroupsLinkVisible' : isNavDiagnosisSubGroupsLinkVisible,
    'clickNavAgeGroupsLink' : clickNavAgeGroupsLink,
    'isNavAgeGroupsLinkVisible' : isNavAgeGroupsLinkVisible,
    'clickNavSickLeaveDegreeLink' : clickNavSickLeaveDegreeLink,
    'isNavSickLeaveDegreeLinkVisible' : isNavSickLeaveDegreeLinkVisible,
    'clickNavSickLeaveLengthLink' : clickNavSickLeaveLengthLink,
    'isNavSickLeaveLengthLinkVisible' : isNavSickLeaveLengthLinkVisible,
    'clickNavCountyLink' : clickNavCountyLink,
    'isNavCountyLinkVisible' : isNavCountyLinkVisible,
    'clickNavCasesPerSexLink' : clickNavCasesPerSexLink,
    'isNavCasesPerSexLinkVisible' : isNavCasesPerSexLinkVisible,
    'clickNavVerksamhetOversiktLink' : clickNavVerksamhetOversiktLink,
    'isNavVerksamhetOversiktLinkVisible' : isNavVerksamhetOversiktLinkVisible,
    'clickNavBusinessCasesPerBusinessLink' : clickNavBusinessCasesPerBusinessLink,
    'isNavBusinessCasesPerBusinessLinkVisible' : isNavBusinessCasesPerBusinessLinkVisible,
    'clickNavBusinessCasesPerMonthLink' : clickNavBusinessCasesPerMonthLink,
    'isNavBusinessCasesPerMonthLinkVisible' : isNavBusinessCasesPerMonthLinkVisible,
    'clickNavBusinessDiagnosisGroupsLink' : clickNavBusinessDiagnosisGroupsLink,
    'isNavBusinessDiagnosisGroupsLinkVisible' : isNavBusinessDiagnosisGroupsLinkVisible,
    'clickNavBusinessDiagnosisSubGroupsLink' : clickNavBusinessDiagnosisSubGroupsLink,
    'isNavBusinessDiagnosisSubGroupsLinkVisible' : isNavBusinessDiagnosisSubGroupsLinkVisible,
    'clickNavBusinessCompareDiagnosisLink' : clickNavBusinessCompareDiagnosisLink,
    'isNavBusinessCompareDiagnosisLinkVisible' : isNavBusinessCompareDiagnosisLinkVisible,
    'clickNavBusinessAgeGroupsLink' : clickNavBusinessAgeGroupsLink,
    'isNavBusinessAgeGroupsLinkVisible' : isNavBusinessAgeGroupsLinkVisible,
    'clickNavBusinessSickLeaveDegreeLink' : clickNavBusinessSickLeaveDegreeLink,
    'isNavBusinessSickLeaveDegreeLinkVisible' : isNavBusinessSickLeaveDegreeLinkVisible,
    'clickNavBusinessSickLeaveLengthLink' : clickNavBusinessSickLeaveLengthLink,
    'isNavBusinessSickLeaveLengthLinkVisible' : isNavBusinessSickLeaveLengthLinkVisible,
    'clickNavBusinessMoreNinetyDaysSickLeaveLink' : clickNavBusinessMoreNinetyDaysSickLeaveLink,
    'isNavBusinessMoreNinetyDaysSickLeaveLinkVisible' : isNavBusinessMoreNinetyDaysSickLeaveLinkVisible,
    'clickNavBusinessCasesPerLakareLink' : clickNavBusinessCasesPerLakareLink,
    'isNavBusinessCasesPerLakareLinkVisible' : isNavBusinessCasesPerLakareLinkVisible,
    'clickNavBusinessCasesPerLakaresAlderOchKonLink' : clickNavBusinessCasesPerLakaresAlderOchKonLink,
    'isNavBusinessCasesPerLakaresAlderOchKonLinkVisible' : isNavBusinessCasesPerLakaresAlderOchKonLinkVisible,
    'clickNavBusinessCasesPerLakarbefattningLink' : clickNavBusinessCasesPerLakarbefattningLink,
    'isNavBusinessCasesPerLakarbefattningLinkVisible' : isNavBusinessCasesPerLakarbefattningLinkVisible,
    'clickNavBusinessDifferentieratIntygandeLink' : clickNavBusinessDifferentieratIntygandeLink,
    'isNavBusinessDifferentieratIntygandeLinkVisible' : isNavBusinessDifferentieratIntygandeLinkVisible,
    'clickNavAboutTjanstLink' : clickNavAboutTjanstLink,
    'isNavAboutTjanstLinkVisible' : isNavAboutTjanstLinkVisible,
    'clickNavAboutInloggningLink' : clickNavAboutInloggningLink,
    'isNavAboutInloggningLinkVisible' : isNavAboutInloggningLinkVisible,
    'clickNavAboutFaqLink' : clickNavAboutFaqLink,
    'isNavAboutFaqLinkVisible' : isNavAboutFaqLinkVisible,
    'clickNavAboutContactLink' : clickNavAboutContactLink,
    'isNavAboutContactLinkVisible' : isNavAboutContactLinkVisible
};
