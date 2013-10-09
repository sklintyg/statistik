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

<link rel="icon" type="image/png" href="../img/favicon.ico">
</head>
<body data-spy="scroll" data-target=".bs-docs-sidebar">

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
				
				<div class="span3 offset5">
					<div id="business-login-container">
						<button class="btn" type="button" id="business-login-btn" value="Logga in">Logga in</button>
					</div>
					<div id="business-logged-in-user-container" style="display: none; position: absolute; right: 0; margin-right: 25px;">
						<span class="business-me" style="right: 0; height: 40px; display: inline-block;">
							<span class="business-me-link-span">
								Inloggad som: 
								<a class="business-me-link" style="cursor: pointer;">
									<span class="business-me-name">Johan Lindholm
										<div id="business-me-icon">
											<img alt="Bild på inloggad användare" src="<c:url value='/img/statistic-user-icon-test.png'/>"/>
										</div>
									</span>
								</a>
							</span>
						</span>
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
				<h1 class="hidden-header">Sidans huvudnavigering</h1>
				<div class="statistics accordion" id="statistics-menu-accordion">
					<div class="accordion-group" id="national-statistics-menu-group">
					<h2 class="hidden-header">Navigering för nationell statistik</h2>
					<!-- NATIONAL STATISTIC MENU -->
					  <div class="accordion-heading statistics-menu">
					    <a class="accordion-toggle first-level-menu active" id="national-statistics-toggle" data-toggle="collapse" data-parent="#statistics-menu-accordion" href="#national-statistics-collapse">
					      Nationell statistik<i class="statistict-left-menu-expand-icon"></i>
					    </a>
					  </div>
					  <div id="national-statistics-collapse" class="accordion-body collapse in">
					    <div class="accordion-inner">
					    	<ul id="national-statistic-menu-content" class="nav nav-list">
								<li><a ng-href="#/oversikt" navigationaware>Översikt</a></li>
							 	<li><a ng-href="#/sjukfallPerManad" id="navCasesPerMonthLink" navigationaware>Sjukfall, totalt</a></li>
								<li>
									<a class="menu-item-has-childs" ng-href="#/diagnosgrupper" id="navDiagnosisGroupsLink" navigationaware>Diagnosgrupp</a>
									<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-diagnostics"></i>
								</li>
								<ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#/underdiagnosgrupper" id="navDiagnosisSubGroupsLink" navigationaware>Underdiagnosgrupp</a></li>
								</ul>
								<li><a ng-href="#/aldersgrupper" id="navAgeGroupsLink" navigationaware>Ålderssgrupp</a></li>
								<li><a ng-href="#/sjukskrivningsgrad" id="navSickLeaveDegreeLink" navigationaware>Sjukskrivningsgrad</a></li>
								<li><a ng-href="#/sjukskrivningslangd" id="navSickLeaveLengthLink" navigationaware>Sjukskrivningslängd</a></li>
								<li><a ng-href="#/lan" id="navCountyLink" navigationaware>Län</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i></li>
								<ul id="sub-menu-cases-per-county" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a class="last-item-in-menu" ng-href="#/andelSjukfallPerKon" id="navCasesPerSexLink" navigationaware>Andel sjukfall per kön</a></li>
								</ul>
							</ul>
					    </div>
					  </div>
					</div>
					
					<div class="accordion-group" id="business-statistics-menu-group">
					<h2 class="hidden-header">Navigering för verksamhetsstatistik</h2>
					<!-- BUSINESS STATISTIC MENU -->
					  <div class="accordion-heading statistics-menu">
					    <a class="accordion-toggle first-level-menu" id="business-statistics-toggle" data-toggle="collapse" data-parent="#statistics-menu-accordion" href="#business-statistics-collapse">
					      Logga in för Verksamhetsstatistik <i class="statistict-left-menu-expand-icon"></i> <!-- Inloggad: Enbart "Verksamhetsstatistik" -->
					    </a>
					  </div>
					  <div id="business-statistics-collapse" class="accordion-body collapse">
					    <div class="accordion-inner">
					      <ul id="business-statistic-menu-content" class="nav nav-list">
								<li><a ng-href="#/verksamhetOversikt" navigationaware>Översikt</a></li>
							 	<li><a ng-href="#/verksamhetsjukfallPerManad" id="navBusinessCasesPerMonthLink" navigationaware>Sjukfall, totalt</a></li>
								<li>
									<a class="menu-item-has-childs" ng-href="#/verksamhetdiagnosgrupp" id="navBusinessDiagnosisGroupsLink" navigationaware>Diagnosgrupp</a>
									<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-diagnostics"></i>
								</li>
								<ul id="sub-menu-business-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#/verksamhetunderdiagnosgrupp" id="navBusinessDiagnosisSubGroupsLink" navigationaware>Underdiagnosgrupp</a></li>
								</ul>
								<li>
									<a ng-href="#/verksamhetsaldersgrupper" id="navBusinessAgeGroupsLink" navigationaware>Ålderssgrupp</a>
									<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-age-group"></i>
								</li>
								<ul id="sub-menu-business-age-group" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#/verksamhetpagaendeavslutadealdersgrupper" id="navBusinessOngoingAndCompletedLink" navigationaware>Pågående och avslutade</a></li>
								</ul>
								<li><a ng-href="#/verksamhetsjukskrivningsgrad" id="navBusinessSickLeaveDegreeLink" navigationaware>Sjukskrivningsgrad</a></li>
								<li>
									<a class="menu-item-has-childs" ng-href="#/verksamhetsjukskrivningslangd" id="navBusinessSickLeaveLengthLink" navigationaware>Sjukskrivningslängd</a>
									<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-sick-leave-length"></i>
								</li>
								<ul id="sub-menu-business-sick-leave-length" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#/pagaendeavslutadesjukskrivningslangd" id="navBusinessOngoingAndCompletedSickLeaveLink" navigationaware>Pågående och avslutade</a></li>
									<li><a class="last-item-in-menu" ng-href="#/merannittiodagarsjukskrivningslangd" id="navBusinessMoreNinetyDaysSickLeaveLink" navigationaware>Mer än 90 dagar</a></li>
								</ul>
							</ul>
					    </div>
					  </div>
					</div>
				</div>
				
				<div class="about accordion" id="statistics-about-menu-accordion">
					<h2 class="hidden-header">Navigering för information om tjänsten</h2>
					<!-- ABOUT STATISTIC MENU -->
					  <div class="accordion-heading statistics-menu">
					    <a class="accordion-toggle first-level-menu" data-toggle="collapse" href="#about-statistics-collapse">
					      Om tjänsten<i class="statistict-left-menu-expand-icon"></i>
					    </a>
					  </div>
					  <div id="about-statistics-collapse" class="accordion-body collapse">
					    <div class="accordion-inner">
					    	<ul id="about-statistic-menu-content" class="nav nav-list">
								<li><a class="first-item-in-menu" ng-href="#/om/tjansten" navigationaware>Allmänt om tjänsten</a></li>
								<li><a ng-href="#/om/inloggning" navigationaware>Inloggning och behörighet</a></li>
								<li><a ng-href="#/om/vanligafragor" navigationaware>Vanliga frågor och svar</a></li>
								<li><a class="last-item-in-menu" ng-href="#/om/kontakt" navigationaware>Kontakt till support</a></li>
							</ul>
					    </div>
					  </div>
				</div>
				<!-- 
				<div id="statistics-left-menu">
					<div class="accordion" id="left-menu-accordion">
						
						<div class="nav nav-list bs-docs-sidenav accordion-group" id="national-statistic-menu">
							<div class="statistics-left-menu-header accordion-heading">
								<span class="accordion-toggle" data-toggle="collapse" data-parent="#left-menu-accordion" href="#national-statistic-menu-content">Nationell statistik<i class="statistict-left-menu-expand-icon"></i></span>
							</div>
							<ul id="national-statistic-menu-content" class="nav nav-list accordion-body collapse in">
								<li><a ng-href="#/oversikt" navigationaware>Översikt</a></li>
							 	<li><a ng-href="#/sjukfallPerManad" id="navCasesPerMonthLink" navigationaware>Sjukfall, totalt</a></li>
								<li><a ng-href="#/diagnosgrupper" id="navDiagnosisGroupsLink" navigationaware>Diagnosgrupp</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-diagnostics"></i></li>
								<ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#/underdiagnosgrupper" id="navDiagnosisSubGroupsLink" navigationaware>Underdiagnosgrupp</a></li>
								</ul>
								<li><a ng-href="#/aldersgrupper" id="navAgeGroupsLink" navigationaware>Ålderssgrupp</a></li>
								<li><a ng-href="#/sjukskrivningsgrad" id="navSickLeaveDegreeLink" navigationaware>Sjukskrivningsgrad</a></li>
								<li><a ng-href="#/sjukskrivningslangd" id="navSickLeaveLengthLink" navigationaware>Sjukskrivningslängd</a></li>
								<li><a ng-href="#/lan" id="navCountyLink" navigationaware>Län</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i></li>
								<ul id="sub-menu-cases-per-county" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a class="last-item-in-menu" ng-href="#/andelSjukfallPerKon" id="navCasesPerSexLink" navigationaware>Andel sjukfall per kön</a></li>
								</ul>
							</ul>
						</div>	
						
						
						<div class="nav nav-list bs-docs-sidenav accordion-group" id="business-statistic-menu">
							<div class="statistics-left-menu-header accordion-heading">
								<span class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#left-menu-accordion" href="#business-statistic-menu-content">Verksamhetsstatistik<i class="statistict-left-menu-expand-icon collapsed"></i></span>
							</div>
							<ul id="business-statistic-menu-content" class="nav nav-list accordion-body collapse">
								<li><a ng-href="#" navigationaware>Översikt</a></li>
							 	<li><a ng-href="#" id="" navigationaware>Sjukskrivn. totalt</a></li>
								<li><a ng-href="#" id="" navigationaware>Diagnosgrupp</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-diagnostics"></i></li>
								<ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a ng-href="#" id="navDiagnosisSubGroupsLink" navigationaware>Underdiagnosgrupp</a></li>
								</ul>
								<li><a ng-href="#" id="" navigationaware>Ålderssgrupp</a></li>
								<li><a ng-href="#" id="" navigationaware>Sjukskrivningsgrad</a></li>
								<li><a ng-href="#" id="" navigationaware>Sjukskrivningslängd</a></li>
								<li><a ng-href="#" id="" navigationaware>Län</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i></li>
								<ul id="" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a class="last-item-in-menu" ng-href="#" id="" navigationaware>Andel sjukfall per kön</a></li>
								</ul>
							</ul>
						</div>
						

						<div class="nav nav-list bs-docs-sidenav" id="about-statistic-menu">
							<div class="statistics-left-menu-header">
								<span class="accordion-toggle collapsed" data-toggle="collapse" href="#about-statistics-menu">Om tjänsten<i class="statistict-left-menu-expand-icon "></i></span>
							</div>
							<ul id="about-statistics-menu" class="nav nav-list accordion-body collapse" >
								<li><a class="first-item-in-menu" ng-href="#/om/tjansten" navigationaware>Allmänt om tjänsten</a></li>
								<li><a ng-href="#/om/inloggning" navigationaware>Inloggning och behörighet</a></li>
								<li><a ng-href="#/om/vanligafragor" navigationaware>Vanliga frågor och svar</a></li>
								<li><a class="last-item-in-menu" ng-href="#/om/kontakt" navigationaware>Kontakt till support</a></li>
							</ul>
						</div>
					</div>
				</div>
				 -->
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
<script type="text/javascript" src="<c:url value='/js/lib/jquery/1.10.2/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/bootstrap/2.3.2/js/bootstrap.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.0.8/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/factories.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/common.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/casesPerMonthCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/doubleAreaChartsCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/overviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/ageGroupsCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controllers.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/directives.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/inera-statistics-style.js'/>" ></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/highcharts.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/modules/highcharts-more.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/modules/exporting.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/exportTableData.js'/>" ></script>

<script type="text/javascript">
	$('.dropdown-toggle').dropdown();
</script>



</body>
</html>
