'use strict';

app.statisticsApp.controller('PageCtrl', app.pageCtrl);
app.statisticsApp.controller('LoginCtrl', app.loginCtrl);
app.statisticsApp.controller('NavigationMenuCtrl', app.navigationMenuCtrl);
app.statisticsApp.controller('OverviewCtrl', app.overviewCtrl);
app.statisticsApp.controller('CasesPerMonthCtrl', app.singleLineChartCtrl);
app.statisticsApp.controller('DiagnosCtrl', app.doubleAreaChartsCtrl);
app.statisticsApp.controller('DegreeOfSickLeaveCtrl', app.doubleAreaChartsCtrl);
app.statisticsApp.controller('AgeGroupsCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('SickLeaveLengthCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('CasesPerCountyCtrl', app.casesPerCountyCtrl);
app.statisticsApp.controller('CasesPerSexCtrl', app.columnChartDetailsViewCtrl);

app.statisticsApp.controller('BusinessOverviewCtrl', app.businessOverviewCtrl);
app.statisticsApp.controller('BusinessLandingPageCtrl', app.businessLandingPageCtrl);
app.statisticsApp.controller('LongSickLeavesCtrl', app.singleLineChartCtrl);
