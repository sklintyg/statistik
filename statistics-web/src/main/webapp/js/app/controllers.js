'use strict';

app.statisticsApp.controller('DiagnosCtrl', app.doubleAreaChartsCtrl);
app.statisticsApp.controller('DegreeOfSickLeaveCtrl', app.doubleAreaChartsCtrl);

app.statisticsApp.controller('AgeGroupsCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('SickLeaveLengthCtrl', app.columnChartDetailsViewCtrl);
app.statisticsApp.controller('CasesPerSexCtrl', app.columnChartDetailsViewCtrl);
