<%--

    Copyright (C) 2012 Inera AB (http://www.inera.se)

    This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).

    Inera Statistics is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Inera Statistics is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html ng-app="StatisticsApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title ng-bind="$root.page_title">Inera Statistics Service</title>

<!-- Styles -->
<link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet">
<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap.min.css'/>" rel="stylesheet">
<link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet">
<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap-responsive.css'/>" rel="stylesheet">


</head>
<body data-spy="scroll">

<!-- Navbar
================================================== -->
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<div class="row-fluid" id="navigation-container">
				<div class="span2">
					<div class="headerbox-logo"><a href="<c:url value='/'/>"><img alt="Till startsidan" src="<c:url value='/img/statistics_logo.png'/>"/></a></div>
				</div>
				<div class="span2">
					<span>Statistiktjänst för ordinerad sjukskrivning</span>
				</div>
				<div class="span1 offset7">
					<div class="dropdown pull-right">
						<a class="dropdown-toggle settings" data-toggle="dropdown" href="#" role="menu"></a>
						<ul class="dropdown-menu dropdown-menu-center" role="menu" aria-labelledby="dLabel" id="settings-dropdown">
							<li><a tabindex="-1" href="#/om#about">Om tjänsten</a></li>
							<li><a tabindex="-1" href="#">Logga ut</a></li>
						</ul>
					</div>				
				</div>
			</div>
		</div>
	</div>
</div>


<div id="wrap">
	<div class="container-fluid">
	
		<!-- Docs nav
		================================================== -->
		<div class="row-fluid">
			<div class="span3 bs-docs-sidebar">
				<div id="statistics-left-menu">
					<ul class="nav nav-list bs-docs-sidenav" id="national-statistic-menu">
						<li><a ng-href="#/oversikt" navigationaware>Nationell statistik<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/sjukfallPerManad" id="navCasesPerMonthLink" navigationaware>Sjukfall, totalt<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/diagnosgrupper" id="navDiagnosisGroupsLink" navigationaware>Diagnosgrupp<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/underdiagnosgrupper" id="navDiagnosisSubGroupsLink" navigationaware>Underdiagnosgrupp<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/aldersgrupper" id="navAgeGroupsLink" navigationaware>Ålderssgrupp<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/sjukskrivningsgrad" id="navSickLeaveDegreeLink" navigationaware>Sjukskrivningsgrad<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/sjukskrivningslangd" id="navSickLeaveLengthLink" navigationaware>Sjukskrivningslängd<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/lan" id="navCountyLink" navigationaware>Län<i class="icon-chevron-right"></i></a></li>
						<li><a ng-href="#/andelSjukfallPerKon" id="navCasesPerSexLink" navigationaware>Andel sjukfall per kön<i class="icon-chevron-right"></i></a></li>
					</ul>
					<ul class="nav nav-list bs-docs-sidenav" id="business-statistics-menu">
						<li><a ng-href="#/om#about" navigationaware>Om tjänsten</a></li>
						<li><a ng-href="#/om#login" navigationaware>Inloggning och behörighet</a></li>
						<li><a ng-href="#/om#faq" navigationaware>Vanliga frågor och svar</a></li>
						<li><a ng-href="#/om#contact" navigationaware>Kontakt till support</a></li>
					</ul>
				</div>
			</div>
		      
			<div class="span9">
				<%-- ng-view that holds dynamic content managed by angular app --%>
				<div id="view" ng-view></div>
			</div>
		</div>
	
	</div>
</div>
<!-- Footer
================================================== -->
<footer class="footer">
	<div class="container-fluid">
	</div>
</footer>

<!-- Scripts -->
<!-- Placed at the end of the document so the pages load faster -->
<script type="text/javascript" src="<c:url value='/js/jquery/1.10.2/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/bootstrap/2.3.2/js/bootstrap.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/angularjs/1.0.8/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/factories.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controllers.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/directives.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/highcharts/3.0.5/highcharts.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/highcharts/3.0.5/highcharts-more.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/highcharts/3.0.5/modules/exporting.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/exportTableData.js'/>" ></script>
<script type="text/javascript">
	$('.dropdown-toggle').dropdown();
	$('#business-statistics-menu').scrollspy();
	$('#national-statistic-menu').affix();
	$('#business-statistics-menu').affix();
	$('#statistics-left-menu').affix();
</script>



</body>
</html>
