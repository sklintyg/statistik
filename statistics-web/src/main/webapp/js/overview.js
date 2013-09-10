function OverviewCtrl($scope, $http) {
	$http.get("api/getOverview").success(function(result) {
		$scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
		$scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
		$scope.casesPerMonthAlteration = result.casesPerMonth.alteration;
	}).error(function(data, status, headers, config) {
		alert("Failed to download overview data")
	});
};
