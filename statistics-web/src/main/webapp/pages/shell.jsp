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
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html xmlns:ng="http://angularjs.org" lang="sv" id="ng-app" data-ng-app="StatisticsApp">
<head>
<!--[if lte IE 8]>
  <script src="/js/app/json2.js"></script>
<![endif]-->

<!--[if lte IE 8]>
  <script>
    document.createElement('ng-include');
    document.createElement('ng-pluralize');
    document.createElement('ng-view');

    // Optionally these for CSS
    document.createElement('ng:include');
    document.createElement('ng:pluralize');
    document.createElement('ng:view');
  </script>
<![endif]-->

<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title data-ng-bind="$root.page_title">Inera Statistics Service</title>

<!-- Styles -->
<!--[if lte IE 8]>
	<link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet">
	<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap.min.css'/>" rel="stylesheet">
	<link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet">
	<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap-responsive.css'/>" rel="stylesheet">
	<link href="<c:url value='/js/lib/pie/PIE.htc'/>" rel="stylesheet">
<![endif]-->
<link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet" media="not print">
<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap.min.css'/>" rel="stylesheet" media="not print">
<link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet" media="not print">
<link href="<c:url value='/bootstrap/2.3.2/css/bootstrap-responsive.css'/>" rel="stylesheet" media="not print">
<link href="<c:url value='/css/print.css'/>" rel="stylesheet" media="print">

<link rel="icon" type="image/png" href="<c:url value='/img/favicon.ico'/>">
<security:authorize access="isAuthenticated()">
    <script>var isLoggedIn = true;</script>
</security:authorize>
<security:authorize access="not isAuthenticated()">
    <script>var isLoggedIn = false;</script>
</security:authorize>
</head>
<body data-ng-controller="PageCtrl">
<spring:eval expression='@loginVisibility.isLoginVisible()' var="loginVisible"/>

<!-- Navbar
================================================== -->
<div class="navbar navbar-inverse navbar-fixed-top dontprint">
	<div class="navbar-inner">
		<div class="container-fluid">
			<div class="row-fluid" id="navigation-container">
				<div class="span3 pull-left" style="width: auto !important;">
					<div class="headerbox-logo">
						<a href="<c:url value='/'/>">
							<img alt="Till startsidan" src="<c:url value='/img/statistics-logotype.png'/>"/>
						</a>
					</div>
				</div>
				<div class="span2">
					<span>Statistiktjänst för ordinerad sjukskrivning</span>
				</div>
				<c:if test="${loginVisible}">
					<div class="span4 pull-right" style="width: auto !important;">
						<div id="business-login-container" style="display: none;" ng-hide="isLoggedIn" >
							<span id="business-login-span">För verksamhetsstatistik: </span>
							<button class="btn" data-ng-click="loginClicked('${applicationScope.loginUrl}')" type="button" id="business-login-btn" value="Logga in">Logga in</button>
						</div>
						<div id="business-logged-in-user-container" style="display: none; position: absolute; right: 0; margin-right: 25px;" ng-show="isLoggedIn" >
							<!-- div class="pull-right">
								<img id="business-me-icon" alt="Bild på inloggad användare" src="<c:url value='/img/avatar.png'/>"/>
							</div -->
							<div class="header-box-user-profile pull-right">
								<span class="user-logout pull-right">
									<a href="/saml/logout">Logga ut</a>
								</span>
								<span class="user-name pull-right" style="margin-right: 10px;" data-ng-bind="userName"></span>
								<br>
								<span>Välj verksamhet:</span>
								<!-- SELECT BUSINESS BUTTON GROUP -->
								<div class="btn-group">
									<a class="btn dropdown-toggle" id="business-select-business" data-toggle="dropdown" href="#" >{{verksamhetName}}<span class="caret"></span></a>
									<ul class="dropdown-menu left" style="float: right; right: 280px; position: absolute;">
										<li data-ng-repeat="business in businesses"><a data-ng-click="selectVerksamhet(business.id)" tooltip-html-unsafe="<div class=popover-content>{{business.name}}</div>" tooltip-trigger="mouseenter" tooltip-placement="left">{{business.name}}</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>

<div id="wrap">
	<div class="container-fluid">
		<!-- Docs nav
		================================================== -->
		<div class="row-fluid">
			<div class="span3 bs-docs-sidebar dontprint" data-ng-controller="NavigationMenuCtrl">
				<h1 class="hidden-header">Sidans huvudnavigering</h1>
				<div class="statistics accordion" id="statistics-menu-accordion">
					<div class="accordion-group" id="national-statistics-menu-group">
					<h2 class="hidden-header">Navigering för nationell statistik</h2>
					<!-- NATIONAL STATISTIC MENU -->
					  <div class="accordion-heading statistics-menu">
					    <div class="accordion-toggle first-level-menu" id="national-statistics-toggle" data-parent="#statistics-menu-accordion" data-ng-class="{active: showNational, collapsed: !showNational}" data-ng-click="toggleNationalAccordion()">
					      Nationell statistik<i class="statistict-left-menu-expand-icon"></i>
					    </div>
					  </div>
					  <div id="national-statistics-collapse" class="accordion-body collapse navigation-group" data-ng-class="{in: showNational}">
					    <div class="accordion-inner">
					    	<ul id="national-statistic-menu-content" class="nav nav-list">
								<li><a data-ng-href="#/nationell/oversikt" id="navOverviewLink" ctrlname="NationalOverviewCtrl" navigationaware>Översikt</a></li>
							 	<li><a data-ng-href="#/nationell/sjukfallPerManad" id="navCasesPerMonthLink" ctrlname="NationalCasesPerMonthCtrl" navigationaware>Sjukfall, totalt</a></li>
								<li>
									<a class="menu-item-has-childs" class="has-collapse" data-ng-href="#/nationell/diagnosgrupper" id="navDiagnosisGroupsLink" ctrlname="NationalDiagnosisGroupsCtrl" navigationaware>Diagnosgrupp</a>
									<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-diagnostics"></i>
								</li>
								<ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a data-ng-href="#/nationell/diagnoskapitel" id="navDiagnosisSubGroupsLink" ctrlname="NationalDiagnosisSubGroupsCtrl" navigationaware>Enskilt diagnoskapitel</a></li>
								</ul>
								<li><a data-ng-href="#/nationell/aldersgrupper" id="navAgeGroupsLink" ctrlname="NationalAgeGroupCtrl" navigationaware>Åldersgrupp</a></li>
								<li><a data-ng-href="#/nationell/sjukskrivningsgrad" id="navSickLeaveDegreeLink" ctrlname="NationalDegreeOfSickLeaveCtrl" navigationaware>Sjukskrivningsgrad</a></li>
								<li><a data-ng-href="#/nationell/sjukskrivningslangd" id="navSickLeaveLengthLink" ctrlname="NationalSickLeaveLengthCtrl" navigationaware>Sjukskrivningslängd</a></li>
								<li><a data-ng-href="#/nationell/lan" id="navCountyLink" class="has-collapse" ctrlname="NationalCasesPerCountyCtrl" navigationaware>Län</a><i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i></li>
								<ul id="sub-menu-cases-per-county" class="nav nav-list sub-nav-list accordion-body in collapse">
									<li><a class="last-item-in-menu rounded-bottom" data-ng-href="#/nationell/andelSjukfallPerKon" id="navCasesPerSexLink" ctrlname="NationalCasesPerSexCtrl" navigationaware>Andel sjukfall per kön</a></li>
								</ul>
							</ul>
					    </div>
					  </div>
					</div>
					<c:if test="${loginVisible}">
						<div class="accordion-group" id="business-statistics-menu-group">
						<h2 class="hidden-header">Navigering för verksamhetsstatistik</h2>
						<!-- BUSINESS STATISTIC MENU -->
						  <div class="accordion-heading statistics-menu">
						    <div class="accordion-toggle first-level-menu" id="business-statistics-toggle" data-parent="#statistics-menu-accordion" data-ng-class="{active: showOperation, collapsed: !showOperation, disabled: !isLoggedIn}" data-ng-click="toggleOperationAccordion()">
						      <span data-ng-bind="organisationMenuLabel"></span><i class="statistict-left-menu-expand-icon"></i> <!-- Inloggad: Enbart "Verksamhetsstatistik" -->
						    </div>
						  </div>
						  <div id="business-statistics-collapse" class="accordion-body collapse navigation-group" data-ng-class="{in: showOperation}">
						    <div class="accordion-inner">
						      <ul id="business-statistic-menu-content" class="nav nav-list">
									<li><a data-ng-href="#/verksamhet/{{businessId}}/oversikt" ctrlname="BusinessOverviewCtrl" navigationaware>Översikt</a></li>
								 	<li><a data-ng-href="#/verksamhet/{{businessId}}/sjukfallPerManad" id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerMonthCtrl" navigationaware>Sjukfall, totalt</a></li>
									<li>
										<a class="menu-item-has-childs" data-ng-href="#/verksamhet/{{businessId}}/diagnosgrupper" id="navBusinessDiagnosisGroupsLink" ctrlname="VerksamhetDiagnosisGroupsCtrl" navigationaware>Diagnosgrupp</a>
										<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-diagnostics"></i>
									</li>
									<ul id="sub-menu-business-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
										<li><a data-ng-href="#/verksamhet/{{businessId}}/diagnoskapitel" id="navBusinessDiagnosisSubGroupsLink" ctrlname="VerksamhetDiagnosisSubGroupsCtrl" navigationaware>Enskilt diagnoskapitel</a></li>
									</ul>
									<li>
										<a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupper" id="navBusinessAgeGroupsLink" ctrlname="VerksamhetAgeGroupCtrl" navigationaware>Åldersgrupp</a>
										<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-age-group"></i>
									</li>
									<ul id="sub-menu-business-age-group" class="nav nav-list sub-nav-list accordion-body in collapse">
										<li><a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupperpagaende" id="navBusinessOngoingAndCompletedLink" ctrlname="VerksamhetAgeGroupCurrentCtrl" navigationaware>Pågående</a></li>
									</ul>
									<li><a data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningsgrad" id="navBusinessSickLeaveDegreeLink" ctrlname="VerksamhetDegreeOfSickLeaveCtrl" navigationaware>Sjukskrivningsgrad</a></li>
									<li>
										<a class="menu-item-has-childs has-collapse" data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangd" id="navBusinessSickLeaveLengthLink" ctrlname="VerksamhetSickLeaveLengthCtrl" navigationaware>Sjukskrivningslängd</a>
										<i class="statistict-left-menu-expand-icon" class="accordion-toggle" data-toggle="collapse" href="#sub-menu-business-sick-leave-length"></i>
									</li>
									<ul id="sub-menu-business-sick-leave-length" class="nav nav-list sub-nav-list accordion-body in collapse">
										<li><a class="border-top no-border-bottom" data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangdpagaende" id="navBusinessOngoingAndCompletedSickLeaveLink" ctrlname="VerksamhetSickLeaveLengthCurrentCtrl" navigationaware>Pågående</a></li>
										<li><a class="last-item-in-menu rounded-bottom" data-ng-href="#/verksamhet/{{businessId}}/langasjukskrivningar" id="navBusinessMoreNinetyDaysSickLeaveLink" ctrlname="VerksamhetLongSickLeavesCtrl" navigationaware>Mer än 90 dagar</a></li>
									</ul>
								</ul>
						    </div>
						  </div>
						</div>
					</c:if>
    				<div class="accordion-group" id="about-statistics-menu-group">
						<h2 class="hidden-header">Navigering för information om tjänsten</h2>
						<!-- ABOUT STATISTIC MENU -->
						  <div class="accordion-heading statistics-menu">
						    <div class="accordion-toggle first-level-menu" data-ng-class="{active: showAbout, collapsed: !showAbout}" data-ng-click="toggleAboutAccordion()">
						      Om tjänsten<i class="statistict-left-menu-expand-icon"></i>
						    </div>
						  </div>
						  <div id="about-statistics-collapse" class="accordion-body collapse navigation-group" data-ng-class="{in: showAbout}">
						    <div class="accordion-inner">
						    	<ul id="about-statistic-menu-content" class="nav nav-list">
									<li><a class="first-item-in-menu" data-ng-href="#/om/tjansten" ctrlname="AboutServiceCtrl" navigationaware>Allmänt om tjänsten</a></li>
									<li><a data-ng-href="#/om/inloggning" ctrlname="AboutLoginCtrl" navigationaware>Inloggning och behörighet</a></li>
									<li><a data-ng-href="#/om/vanligafragor" ctrlname="AboutFaqCtrl" navigationaware>Vanliga frågor och svar</a></li>
									<li><a class="last-item-in-menu no-border-top rounded-bottom" data-ng-href="#/om/kontakt" ctrlname="AboutContactCtrl" navigationaware>Kontakt till support</a></li>
								</ul>
						    </div>
						  </div>
				    </div>
                </div>
		    </div>
			<div class="span9">
				<%-- data-ng-view that holds dynamic content managed by angular app --%>
				<div id="view" data-ng-view></div>
			</div>
	    </div>  
    </div>
</div>
<!-- Footer
================================================== -->
<footer class="footer dontprint">
	<div class="container-fluid">
	</div>
</footer>

<!-- Scripts -->
<!-- Placed at the end of the document so the pages load faster -->
<!--[if lte IE 8]>
	<script type="text/javascript" src="<c:url value='/js/lib/respond/1.3.0/respond.min.js'/>"></script>
<![endif]-->
<!--[if lt IE 9]>
	<script type="text/javascript" src="<c:url value='/js/app/html5shiv.js'/>"></script>
<![endif]-->
<script type="text/javascript" src="<c:url value='/js/lib/jquery/1.10.2/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/bootstrap/2.3.2/js/bootstrap.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.0.8/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.0.8/angular-cookies.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/ui-bootstrap/0.6.0/ui-bootstrap-tpls-0.6.0.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/factories.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/common.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/html5shiv.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/css3-mediaqueries.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/respond/1.3.0/respond.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/singleLineChartCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/doubleAreaChartsCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/overviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessOverviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessLandingPageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/columnChartDetailsViewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/casesPerCountyCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/pageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/navigationMenuCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controllers.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/directives.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/filters.js'/>"></script>
<!--script type="text/javascript" src="<c:url value='/js/app/inera-statistics-style.js'/>" ></script -->
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/highcharts.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/modules/highcharts-more.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/3.0.5/modules/exporting.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/lib/highcharts/pattern-fill/pattern-fill.js'/>"></script>

<script type="text/javascript">
	$('.dropdown-toggle').dropdown();
	$('#pageHelpToolTip').popover();
</script>

</body>
</html>
