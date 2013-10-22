'use strict';

app.statisticsApp.controller('NavigationMenuCtrl', app.navigationMenuCtrl);
app.statisticsApp.controller('OverviewCtrl', app.overviewCtrl);
app.statisticsApp.controller('CasesPerMonthCtrl', app.singleLineChartCtrl);
app.statisticsApp.controller('DiagnosisGroupsCtrl', app.doubleAreaChartsCtrl);
app.statisticsApp.controller('DegreeOfSickLeaveCtrl', app.doubleAreaChartsCtrl);
app.statisticsApp.controller('AgeGroupsCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('SickLeaveLengthCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('CasesPerCountyCtrl', app.casesPerCountyCtrl);

app.statisticsApp.controller('BusinessOverviewCtrl', app.businessOverviewCtrl);
app.statisticsApp.controller('BusinessLandingPageCtrl', app.businessLandingPageCtrl);
app.statisticsApp.controller('LongSickLeavesCtrl', app.singleLineChartCtrl);
