<%@ page contentType="text/html;charset=UTF-8" language="java"
	session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="sv">
<head>
<title>Intygsstatistik - Health Check</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="ROBOTS" content="nofollow, noindex" />

<!-- build:css(src/main/webapp) app/vendor.css -->
<!-- bower:css -->
<link rel="stylesheet" href="bower_components/bootstrap-multiselect/dist/css/bootstrap-multiselect.css" />
<link rel="stylesheet" href="bower_components/outdated-browser/outdatedbrowser/outdatedbrowser.min.css" />
<link rel="stylesheet" href="bower_components/dropzone/dist/min/dropzone.min.css" />
<!-- endbower -->
<!-- endbuild -->

<!-- build:css({build/.tmp,src/main/webapp}) app/app.css -->
<!-- injector:css -->
<link rel="stylesheet" href="/app/app.css">
<!-- endinjector -->
<!-- endbuild -->

</head>
<body>
	<div class="container">
		<div class="page-header">
			<h1>Intygsstatistik - HealthCheck</h1>
		</div>

		<c:set var="overview" value="${healthcheckUtil.overviewStatus}" />
		<c:set var="workloadStatus" value="${healthcheckUtil.workloadStatus}" />
		<c:set var="db" value="${healthcheckUtil.checkDB()}" />
		<c:set var="uptime" value="${healthcheckUtil.uptimeAsString}" />
		<c:set var="loggedInUsers" value="${healthcheckUtil.checkNbrOfLoggedInUsers()}" />
		<c:set var="users" value="${healthcheckUtil.getNumberOfUsers()}" />

		<div class="table-responsive">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th>Check</th>
						<th>Tid</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Koppling databas</td>
						<td id="dbMeasurement">${db.measurement}ms</td>
						<td id="dbStatus" class="${db.ok ? "text-success" : "text-danger"}">${db.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Hämta översikt nationell statstik</td>
						<td id="overviewMeasurement">${overview.measurement}ms</td>
						<td id="overviewStatus" class="${overview.ok ? "text-success" : "text-danger"}">${overview.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>WorkloadStatus</td>
						<td id="workloadMeasurement">${workloadStatus.measurement}ms</td>
						<td id="workloadStatus" class="${workloadStatus.ok ? "text-success" : "text-danger"}">${workloadStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Antal användare</td>
						<td colspan="2" id="users">${users.measurement}</td>
					</tr>
					<tr>
						<td>Antal inloggade användare</td>
						<td colspan="2" id="loggedInUsers">${loggedInUsers.measurement}</td>
					</tr>
					<tr>
						<td>Applikationens upptid</td>
						<td colspan="2" id="uptime">${uptime}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
