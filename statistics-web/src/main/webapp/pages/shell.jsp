<%--
  ~ Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
  ~
  ~     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
  ~
  ~     Inera Statistics is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     (at your option) any later version.
  ~
  ~     Inera Statistics is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU LESSER GENERAL PUBLIC LICENSE for more details.
  ~
  ~     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
  ~     along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="sv" id="ng-app" data-ng-app="StatisticsApp">
<head>
    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title data-ng-bind="$root.page_title">Inera Statistics Service</title>

    <!--[if lte IE 8]>
    <script src="/js/app/json2.js"></script>
    <script>
        document.createElement('ng-include');
        document.createElement('ng-pluralize');
        document.createElement('ng-view');

        // Optionally these for CSS
        document.createElement('ng:include');
        document.createElement('ng:pluralize');
        document.createElement('ng:view');
    </script>
    <link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet">
    <link href="<c:url value='/bootstrap/3.1.1/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet">
    <![endif]-->
    <link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/helperclasses.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/print.css'/>" rel="stylesheet" media="print">
    <link href="<c:url value='/css/bootstrap-multiselect.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/filter.css'/>" rel="stylesheet" media="not print">

    <link href="<c:url value='/bootstrap/3.1.1/css/bootstrap.min.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/bootstrap/3.1.1/css/bootstrap-theme.min.css'/>" rel="stylesheet" media="not print">

    <link rel="icon" type="image/png" href="<c:url value='/img/favicon.ico'/>">
    <security:authorize access="isAuthenticated()">
        <script>var isLoggedIn = true;</script>
    </security:authorize>
    <security:authorize access="not isAuthenticated()">
        <script>var isLoggedIn = false;</script>
    </security:authorize>

    <script>
        var highchartsExportUrl = '${applicationScope.highchartsExportUrl}';
    </script>
</head>
<body data-ng-controller="PageCtrl">
<spring:eval expression='@loginVisibility.isLoginVisible()' var="loginVisible"/>

<!-- Navbar
================================================== -->
<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 navbar dontprint">
		    <div class="navbar-inner">
		        <div class="container-fluid">
		            <div class="row" id="navigation-container">
		                <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 pull-left" style="width: auto !important;">
		                    <div class="headerbox-logo">
		                        <a href="<c:url value='/'/>">
		                            <img alt="Till startsidan" src="<c:url value='/img/statistiktjansten-logotype.png'/>"/>
		                        </a>
		                    </div>
		                </div>
		                <div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 pull-left" style="margin-top: 3px;">
		                    <span>Statistiktjänst för ordinerad sjukskrivning</span>
		                </div>
		                <c:if test="${loginVisible}">
		                    <div class="hidden-xs col-sm-5 col-md-6 col-lg-4 pull-right">
		                        <div id="business-login-container" ng-hide="isLoggedIn">
		                            <span id="business-login-span">För verksamhetsstatistik: </span>
		                            <button class="btn" data-ng-click="loginClicked('${applicationScope.loginUrl}')"
		                                    type="button" id="business-login-btn" value="Logga in">Logga in
		                            </button>
		                        </div>
		                        <div id="business-logged-in-user-container" style="position: absolute; right: 0; margin-right: 25px;" ng-show="isLoggedIn">
		                            <div class="header-box-user-profile pull-right">
										<span class="user-logout pull-right">
											<a href="/saml/logout">Logga ut</a>
										</span>
		                                <span class="user-name pull-right" style="margin-right: 10px;"
		                                      data-ng-bind="userNameWithAccess"></span>
		                                <br>
		                                <span>{{verksamhetName}}</span>
		                            </div>
		                        </div>
		                    </div>
		                </c:if>
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
        <div class="row">
            <div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 bs-docs-sidebar dontprint" data-ng-controller="NavigationMenuCtrl">
                <h1 class="hidden-header">Sidans huvudnavigering</h1>
				<!-- MOBILE NAVIGATION START -->
				<nav class="navbar navbar-default hidden-sm hidden-md hidden-lg" role="navigation">
				  <div class="container-fluid">
				    <!-- Brand and toggle get grouped for better mobile display -->
				    <div class="navbar-header navbar-header-margins">
				      <span class="navbar-brand pull-left">Meny</span>
				      <button type="button" class="navbar-toggle collapsed pull-left" data-toggle="collapse in" data-target="#navbar-mobile-menu-national" ng-click="isCollapsed = !isCollapsed">
				        <span class="icon-bar"></span>
				        <span class="icon-bar"></span>
				        <span class="icon-bar"></span>
				      </button>
				      <c:if test="${loginVisible}">
		                    <div class="col-xs-6 hidden-sm hidden-md hidden-lg pull-right">
		                        <div id="business-login-container" class="pull-right" ng-hide="isLoggedIn" style="position: absolute; right: 0; margin-right: 10px; top: 5px;">
		                            <a class="btn" data-ng-click="loginClicked('${applicationScope.loginUrl}')"
		                                    type="button" id="business-login-btn" value="Logga in">Logga in
		                            </a>
		                        </div>
		                        <div id="business-logged-in-user-container" style="position: absolute; right: 0; margin-right: 10px; top: 5px;" ng-show="isLoggedIn">
		                            <div class="header-box-user-profile pull-right">
		                                <span class="user-name pull-right" data-ng-bind="userNameWithAccess"></span>
		                                <br/>
		                                <span class="user-logout pull-right">
											<a href="/saml/logout">Logga ut</a>
										</span>
		                            </div>
		                        </div>
		                    </div>
		              </c:if>
				    </div>
					<!-- Start mobile navigation menu -->
				    <div class="collapse navbar-collapse-navigation" id="navbar-mobile-menu-national" collapse="!isCollapsed">
				      <ul class="nav navbar-nav">
				      	<li class="divider"></li>
				      	<!-- National mobile menu -->
					    <li class="dropdown-national">
			          		<a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#national-menu" ng-click="isNationalCollapsed = !isNationalCollapsed">Nationell statistik<span class="caret pull-right mobile-menu-caret"></span></a>
			          		<ul class="collapse" id="national-menu" collapse="!isNationalCollapsed">  	
								<li class="subMenuItem"><a data-ng-href="#/nationell/oversikt" id="navOverviewLink" ctrlname="NationalOverviewCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Översikt</a></li>
						        <li class="subMenuItem"><a data-ng-href="#/nationell/sjukfallPerManad" id="navCasesPerMonthLink" ctrlname="NationalCasesPerMonthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukfall, totalt</a></li>
						        <li class="subMenuItem">
						          <a data-toggle="collapse in" data-target="#national-dia-chapter-menu" ng-click="isNationalDiaChapterCollapsed = !isNationalDiaChapterCollapsed">Diagnosgrupp och enskilt diagnoskapitel<span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
						          <ul class="collapse" id="national-dia-chapter-menu" collapse="isNationalDiaChapterCollapsed">
						            <li class="subMenuItem"><a data-ng-href="#/nationell/diagnosgrupp" id="navDiagnosisGroupsLink" ctrlname="NationalDiagnosgruppCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Diagnosgrupp</a></li>
						            <li class="subMenuItem"><a data-ng-href="#/nationell/diagnosavsnitt" id="navDiagnosisSubGroupsLink" ctrlname="NationalDiagnosavsnittCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Enskilt diagnoskapitel</a></li>
						          </ul>
						        </li>
						        <li class="subMenuItem"><a data-ng-href="#/nationell/aldersgrupper" id="navAgeGroupsLink" ctrlname="NationalAgeGroupCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Åldersgrupp</a></li>
						        <li class="subMenuItem"><a data-ng-href="#/nationell/sjukskrivningsgrad" id="navSickLeaveDegreeLink" ctrlname="NationalDegreeOfSickLeaveCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukskrivningsgrad</a></li>
		                        <li class="subMenuItem"><a data-ng-href="#/nationell/sjukskrivningslangd" id="navSickLeaveLengthLink" ctrlname="NationalSickLeaveLengthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukskrivningslängd</a></li>
						      	<li class="subMenuItem">
						          <a data-toggle="collapse in" data-target="#national-lan-kon-menu" ng-click="isNationalLanKonCollapsed = !isNationalLanKonCollapsed">Län och andel sjukfall per kön<span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
						          <ul class="collapse" id="national-lan-kon-menu" collapse="isNationalLanKonCollapsed">
						            <li><a data-ng-href="#/nationell/diagnosgrupp" id="navDiagnosisGroupsLink" ctrlname="NationalDiagnosgruppCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Diagnosgrupp</a></li>
						            <li><a data-ng-href="#/nationell/diagnosavsnitt" id="navDiagnosisSubGroupsLink" ctrlname="NationalDiagnosavsnittCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Enskilt diagnoskapitel</a></li>
						          </ul>
						        </li>
						     </ul>
						</li>
						<li class="divider"></li>
	
						<!-- Business mobile menu -->
					    <li class="dropdown-business">
			          		<a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#business-menu" ng-click="isBusinessCollapsed = !isBusinessCollapsed">Verksamhetsstatistik<span class="caret pull-right mobile-menu-caret"></span></a>
			          		<ul class="collapse" id="business-menu" collapse="!isBusinessCollapsed">   
				        		<li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/oversikt" ctrlname="BusinessOverviewCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Översikt</a></li>
		                        <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/sjukfallPerManad" id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerMonthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukfall, totalt</a></li>
								<li class="subMenuItem">
						          <a class="dropdown-toggle subMenuItem" data-toggle="collapse in" data-target="#business-dia-chapter" ng-click="isBusinessDiaChapterCollapsed = !isBusinessDiaChapterCollapsed">Diagnosgrupp och enskilt diagnoskapitel <span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
						          <ul class="collapse" id="business-dia-chapter" collapse="isBusinessDiaChapterCollapsed">
						            <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/diagnosgrupp" id="navBusinessDiagnosisGroupsLink" ctrlname="VerksamhetDiagnosgruppCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Diagnosgrupp</a></li>
						            <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/diagnosavsnitt" id="navBusinessDiagnosisSubGroupsLink" ctrlname="VerksamhetDiagnosavsnittCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Enskilt diagnoskapitel</a></li>
						          </ul>
						        </li>
						        <li class="subMenuItem">
						          <a class="dropdown-toggle subMenuItem" data-toggle="collapse in" data-target="#business-agegroup-ongoing" ng-click="isBusinessAgeOngoingCollapsed = !isBusinessAgeOngoingCollapsed">Per åldersgrupp eller pågående sjukfall<span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
						          <ul class="collapse" id="business-agegroup-ongoing" collapse="isBusinessAgeOngoingCollapsed">
						            <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupper" id="navBusinessAgeGroupsLink" ctrlname="VerksamhetAgeGroupCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Åldersgrupp</a></li>
						          	<li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupperpagaende" id="navBusinessOngoingAndCompletedLink" ctrlname="VerksamhetAgeGroupCurrentCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Pågående</a></li>
						          </ul>
						        </li>
						        <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningsgrad" id="navBusinessSickLeaveDegreeLink" ctrlname="VerksamhetDegreeOfSickLeaveCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukskrivningsgrad</a></li>
						        <li class="subMenuItem">
						          <a class="dropdown-toggle subMenuItem" data-toggle="collapse in" data-target="#business-sicklength-ongoing-morethan90" ng-click="isBusinessSickOn90Collapsed = !isBusinessSickOn90Collapsed">Sjukskrivningslängd, pågående, över 90 dagar<span class="caret pull-right mobile-menu-caret mobile-sub-caret"></span></a>
						          <ul class="collapse" id="business-sicklength-ongoing-morethan90" collapse="isBusinessSickOn90Collapsed">
						          	<li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangd" id="navBusinessSickLeaveLengthLink" ctrlname="VerksamhetSickLeaveLengthCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Sjukskrivningslängd</a></li>
						          	<li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangdpagaende" id="navBusinessOngoingAndCompletedSickLeaveLink" ctrlname="VerksamhetSickLeaveLengthCurrentCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Pågående</a></li>
		                            <li class="subMenuItem"><a data-ng-href="#/verksamhet/{{businessId}}/langasjukskrivningar" id="navBusinessMoreNinetyDaysSickLeaveLink" ctrlname="VerksamhetLongSickLeavesCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Mer än 90 dagar</a></li>
						          </ul>
						        </li>
						     </ul>
						  </li>
						  <li class="divider"></li>
						  <!-- About mobile menu -->
					    <li class="dropdown-about-statistic">
			          		<a class="mobileMenuHeaderItem" data-toggle="collapse in" data-target="#about-menu" ng-click="isAboutCollapsed = !isAboutCollapsed">Om tjänsten<span class="caret pull-right mobile-menu-caret"></span></a>
			          		<ul class="collapse" id="about-menu" collapse="!isAboutCollapsed">   
				        		<li class="subMenuItem"><a class="first-item-in-menu" data-ng-href="#/om/tjansten" ctrlname="AboutServiceCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Allmänt om tjänsten</a></li>
                                <li class="subMenuItem"><a data-ng-href="#/om/inloggning" ctrlname="AboutLoginCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Inloggning och behörighet</a></li>
                                <li class="subMenuItem"><a data-ng-href="#/om/vanligafragor" ctrlname="AboutFaqCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Vanliga frågor och svar</a></li>
                                <li class="subMenuItem"><a data-ng-href="#/om/kontakt" ctrlname="AboutContactCtrl" role="menuitem" ng-click="isCollapsed = !isCollapsed" navigationaware>Kontakt till support</a></li>    
						    </ul>
						</li>
				      </ul>
				    </div><!-- /.navbar-collapse -->
				  </div><!-- /.container-fluid -->
				</nav>
				<!-- MOBILE NAVIGATION END -->
                <div class="statistics accordion hidden-xs" id="statistics-menu-accordion">
                    <div class="accordion-group" id="national-statistics-menu-group">
                        <h2 class="hidden-header">Navigering för nationell statistik</h2>
                        <!-- NATIONAL STATISTIC MENU -->
                        <div class="accordion-heading statistics-menu"> 
                            <div class="accordion-toggle first-level-menu" id="national-statistics-toggle"
                                 data-parent="#statistics-menu-accordion"
                                 data-ng-class="{active: showNational, collapsed: !showNational}"
                                 data-ng-click="toggleNationalAccordion()">
                                 <span class="statistics-menu-heading">Nationell statistik</span><i class="statistict-left-menu-expand-icon"></i>
                            </div>
                        </div>
                        <div id="national-statistics-collapse" class="accordion-body collapse navigation-group"
                             data-ng-class="{in: showNational}">
                            <div class="accordion-inner">
                                <ul id="national-statistic-menu-content" class="nav nav-list">
                                    <li><a data-ng-href="#/nationell/oversikt" id="navOverviewLink"
                                           ctrlname="NationalOverviewCtrl" navigationaware>Översikt</a></li>
                                    <li><a data-ng-href="#/nationell/sjukfallPerManad" id="navCasesPerMonthLink"
                                           ctrlname="NationalCasesPerMonthCtrl" navigationaware>Sjukfall, totalt</a>
                                    </li>
                                    <li>
                                        <a class="menu-item-has-childs"
                                           data-ng-href="#/nationell/diagnosgrupp" id="navDiagnosisGroupsLink"
                                           ctrlname="NationalDiagnosgruppCtrl" navigationaware>Diagnosgrupp
                                        	<i class="statistict-left-menu-expand-icon" data-toggle="collapse" href="#sub-menu-diagnostics"></i>
                                        </a>
                                        
                                    </li>
                                    <ul id="sub-menu-diagnostics" class="nav nav-list sub-nav-list accordion-body in collapse">
                                        <li><a data-ng-href="#/nationell/diagnosavsnitt" id="navDiagnosisSubGroupsLink"
                                               ctrlname="NationalDiagnosavsnittCtrl" navigationaware>Enskilt diagnoskapitel</a></li>
                                    </ul>
                                    <li><a data-ng-href="#/nationell/aldersgrupper" id="navAgeGroupsLink"
                                           ctrlname="NationalAgeGroupCtrl" navigationaware>Åldersgrupp</a></li>
                                    <li><a data-ng-href="#/nationell/sjukskrivningsgrad" id="navSickLeaveDegreeLink"
                                           ctrlname="NationalDegreeOfSickLeaveCtrl"
                                           navigationaware>Sjukskrivningsgrad</a></li>
                                    <li><a data-ng-href="#/nationell/sjukskrivningslangd" id="navSickLeaveLengthLink"
                                           ctrlname="NationalSickLeaveLengthCtrl"
                                           navigationaware>Sjukskrivningslängd</a></li>
                                    <li>
                                    	<a data-ng-href="#/nationell/lan" id="navCountyLink" class="has-collapse"
                                           ctrlname="NationalCasesPerCountyCtrl" navigationaware>Län
                                        	<i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-cases-per-county"></i> 
                                        </a>
                                    </li>
                                    <ul id="sub-menu-cases-per-county"
                                        class="nav nav-list sub-nav-list accordion-body in collapse">
                                        <li>
                                        	<a class="last-item-in-menu rounded-bottom"
                                               data-ng-href="#/nationell/andelSjukfallPerKon" id="navCasesPerSexLink"
                                               ctrlname="NationalCasesPerSexCtrl" navigationaware>Andel sjukfall per kön
                                        	</a>
                                    	</li>
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
                                <div class="accordion-toggle first-level-menu" id="business-statistics-toggle"
                                     data-parent="#statistics-menu-accordion"
                                     data-ng-class="{active: showOperation, collapsed: !showOperation, disabled: !isLoggedIn}"
                                     data-ng-click="toggleOperationAccordion()">
                                     <span class="statistics-menu-heading" data-ng-bind="organisationMenuLabel"></span><i 
                                        class="statistict-left-menu-expand-icon"></i>
                                     <!-- Inloggad: Enbart "Verksamhetsstatistik" -->
                                </div>
                            </div>
                            <div id="business-statistics-collapse" class="accordion-body collapse navigation-group"
                                 data-ng-class="{in: showOperation}">
                                <div class="accordion-inner">
                                    <ul id="business-statistic-menu-content" class="nav nav-list">
                                        <li><a data-ng-href="#/verksamhet/{{businessId}}/oversikt"
                                               ctrlname="BusinessOverviewCtrl" navigationaware>Översikt</a></li>
                                        <li><a data-ng-href="#/verksamhet/{{businessId}}/sjukfallPerManad"
                                               id="navBusinessCasesPerMonthLink" ctrlname="VerksamhetCasesPerMonthCtrl"
                                               navigationaware>Sjukfall, totalt</a></li>
                                        <li>
                                            <a class="menu-item-has-childs"
                                               data-ng-href="#/verksamhet/{{businessId}}/diagnosgrupp"
                                               id="navBusinessDiagnosisGroupsLink" ctrlname="VerksamhetDiagnosgruppCtrl"
                                               navigationaware>Diagnosgrupp
                                               <i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-business-diagnostics"></i>
                                            </a>
                                        </li>
                                        <ul id="sub-menu-business-diagnostics"
                                            class="nav nav-list sub-nav-list accordion-body in collapse">
                                            <li><a data-ng-href="#/verksamhet/{{businessId}}/diagnosavsnitt"
                                                   id="navBusinessDiagnosisSubGroupsLink"
                                                   ctrlname="VerksamhetDiagnosavsnittCtrl" navigationaware>Enskilt diagnoskapitel
                                                </a>
                                            </li>
                                        </ul>
                                        <li>
                                            <a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupper"
                                               id="navBusinessAgeGroupsLink" ctrlname="VerksamhetAgeGroupCtrl"
                                               navigationaware>Åldersgrupp
                                               <i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-business-age-group"></i>
                                            </a>
                                        </li>
                                        <ul id="sub-menu-business-age-group"
                                            class="nav nav-list sub-nav-list accordion-body in collapse">
                                            <li><a data-ng-href="#/verksamhet/{{businessId}}/aldersgrupperpagaende"
                                                   id="navBusinessOngoingAndCompletedLink"
                                                   ctrlname="VerksamhetAgeGroupCurrentCtrl" navigationaware>Pågående</a>
                                            </li>
                                        </ul>
                                        <li><a data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningsgrad"
                                               id="navBusinessSickLeaveDegreeLink"
                                               ctrlname="VerksamhetDegreeOfSickLeaveCtrl" navigationaware>Sjukskrivningsgrad</a>
                                        </li>
                                        <li>
                                            <a class="menu-item-has-childs has-collapse"
                                               data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangd"
                                               id="navBusinessSickLeaveLengthLink"
                                               ctrlname="VerksamhetSickLeaveLengthCtrl" navigationaware>Sjukskrivningslängd
                                            	<i class="statistict-left-menu-expand-icon accordion-toggle" data-toggle="collapse" href="#sub-menu-business-sick-leave-length"></i>
                                            </a>
                                        </li>
                                        <ul id="sub-menu-business-sick-leave-length"
                                            class="nav nav-list sub-nav-list accordion-body in collapse">
                                            <li><a class="border-top no-border-bottom"
                                                   data-ng-href="#/verksamhet/{{businessId}}/sjukskrivningslangdpagaende"
                                                   id="navBusinessOngoingAndCompletedSickLeaveLink"
                                                   ctrlname="VerksamhetSickLeaveLengthCurrentCtrl" navigationaware>Pågående</a>
                                            </li>
                                            <li><a class="last-item-in-menu rounded-bottom"
                                                   data-ng-href="#/verksamhet/{{businessId}}/langasjukskrivningar"
                                                   id="navBusinessMoreNinetyDaysSickLeaveLink"
                                                   ctrlname="VerksamhetLongSickLeavesCtrl" navigationaware>Mer än 90
                                                dagar</a></li>
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
                            <div class="accordion-toggle first-level-menu"
                                 data-ng-class="{active: showAbout, collapsed: !showAbout}"
                                 data-ng-click="toggleAboutAccordion()">
                                 <span class="statistics-menu-heading">Om tjänsten</span><i class="statistict-left-menu-expand-icon"></i>
                            </div>
                        </div>
                        <div id="about-statistics-collapse" class="accordion-body collapse navigation-group"
                             data-ng-class="{in: showAbout}">
                            <div class="accordion-inner">
                                <ul id="about-statistic-menu-content" class="nav nav-list">
                                    <li><a class="first-item-in-menu" data-ng-href="#/om/tjansten"
                                           ctrlname="AboutServiceCtrl" navigationaware>Allmänt om tjänsten</a></li>
                                    <li><a data-ng-href="#/om/inloggning" ctrlname="AboutLoginCtrl" navigationaware>Inloggning
                                        och behörighet</a></li>
                                    <li><a data-ng-href="#/om/vanligafragor" ctrlname="AboutFaqCtrl" navigationaware>Vanliga
                                        frågor och svar</a></li>
                                    <li><a class="last-item-in-menu no-border-top rounded-bottom"
                                           data-ng-href="#/om/kontakt" ctrlname="AboutContactCtrl" navigationaware>Kontakt
                                        till support</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-xs-12 col-sm-9 col-md-9 col-lg-9">
                <div class="row" ng-show="verksamhetIdParam" data-ng-controller="FilterCtrl">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div id="statistics-filter-container" class="collapse" collapse="!isFilterCollapsed">
                        	<div class="row">
				                <div class="filter-level" id="first-level-filter">
                                    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-3 clearfix">
                                        <label for="select-unit">Typ av verksamhet:</label><br/>
                                        <select ng-model="businessFilter.verksamhetsTypIds" multiple="multiple"
                                                ng-options="verksamhet.id as verksamhet.name for verksamhet in businessFilter.verksamhetsTyper" multiselect-dropdown id="select-verksamhet">
                                        </select>
                                    </div>
                                    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-3 clearfix" data-ng-if="businessFilter.useSmallGUI()">
                                        <label for="select-unit">Val av enhet:</label><br/>
                                        <select ng-model="businessFilter.geographyBusinessIds" multiple="multiple"
                                                ng-options="business.id as business.name for business in businessFilter.businesses" multiselect-dropdown id="select-unit">
                                        </select>
                                    </div>
	                                <div class="col-xs-6 col-sm-6 col-md-6 col-lg-3" data-ng-if="!businessFilter.useSmallGUI()">
	                                	<label for="select-geo-unit">Val av enheter:</label><br/>
	                                    <button class="btn btn-default" data-toggle="modal" data-target="#myModal" id="select-geo-unit" >
	                                        Välj enhet
	                                    </button>
						                <label>{{selectedLeavesCount(businessFilter.geography)}} valda enheter</label>
	                                    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	                                        <div class="modal-dialog">
	                                            <div class="modal-content">
	                                                <div class="modal-header">
	                                                    <span id="myModalLabel">Val av enheter</span>
	                                                </div>
	                                                <div class="modal-body">
	                                                    <ul class="modal-list">
	                                                        <li class="search-all-items input-group">
	                                                        	<span class="input-group-addon glyphicon glyphicon-search"></span>
	                                                            <input type="search" ng-model="multiMenuFilter" class="multiMenuFilterSearch form-control" ng-change="filterMenuItems(businessFilter.geography.subs, multiMenuFilter)" placeholder="Sök efter enhet"/>
	                                                        </li>
	                                                        <li class="select-all-items">
	                                                            <input type="checkbox" ng-checked="businessFilter.geography.allSelected" id="select-all-units" class="multiMenuSelectAll" ng-click="itemClicked(businessFilter.geography, businessFilter.geography)"></input>
	                                                            <label for="select-all-units">Markera alla</label>
	                                                        </li>
	                                                        <li data-ng-repeat="item in businessFilter.geography.subs" class="multiMenuSelectKapitel" ng-init="itemRoot=businessFilter.geography; depth=0">
	                                                            <submenu item="item" itemroot="itemRoot" depth="depth" recursionhelper="recursionhelper" ng-hide="item.hide" class="depth0" />
	                                                        </li>
	                                                    </ul>
	                                                </div>
	                                                <div class="modal-footer">
	                                                    <label class="pull-left">Län: {{selectedTertiaryCount(businessFilter.geography)}} Kommuner: {{selectedSecondaryCount(businessFilter.geography)}} Enheter: {{selectedLeavesCount(businessFilter.geography)}}</label>
	                                                    <button class="btn btn-success" data-dismiss="modal" aria-hidden="true">Spara och stäng</button>
	                                                </div>
	                                            </div>
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="filter-level no-padding">
	                            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
	                            		<div class="divider"></div>
	                            	</div>
	                            </div>
	                            <div class="filter-level" id="actions-for-filter">
	                            	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 pull-right">
	                            		<div class="row">
	                            			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 pull-right">
	                            				<button type="button" class="btn btn-default pull-right">Återställ</button>
	                                        	<button class="btn btn-success pull-right" data-ng-click="makeUnitSelection()" data-toggle="collapse" data-target="#statistics-filter-container" ng-click="isCollapsed = !isCollapsed">Sök</button>
	                                        	<div class="pull-right">
		                            				<input type="checkbox" id="check-permanent-filter" ng-model="businessFilter.permanentFilter">
		                                        		<label for="check-permanent-filter">Val ska slå på alla rapporter</label>
		                                        	</input>
		                                        </div>
	                                        </div>
			                            </div>
                                    </div>
                                </div>
				        	</div>
	                    </div>
                        <button id="show-hide-filter-btn" type="button" class="btn btn-small pull-right" data-toggle="collapse" data-target="#statistics-filter-container" ng-click="isCollapsed = !isCollapsed">
                            Gör urval
                        </button>
                    </div>
                </div>
				<div class="row">
	                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
	                    <%-- data-ng-view that holds dynamic content managed by angular app --%>
	                    <div id="view" data-ng-view></div>
	                </div>
	        	</div>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<!-- Placed at the end of the document so the pages load faster -->
<!--[if lt IE 9]>
<script type="text/javascript" src="<c:url value='/js/lib/respond/1.3.0/respond.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/html5shiv.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/css3-mediaqueries.js'/>"></script>
<![endif]-->
<script type="text/javascript" src="<c:url value='/js/lib/jquery/1.10.2/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/bootstrap/3.1.1/js/bootstrap.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/underscore-1.7.0/underscore-min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.2.14/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.2.14/angular-cookies.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/angularjs/1.2.14/angular-route.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/ui-bootstrap/0.10.0/ui-bootstrap-tpls-0.10.0.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/respond/1.3.0/respond.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/bootstrap-multiselect/bootstrap-multiselect.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/factories.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/businessFilter.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/common.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/singleLineChartCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/doubleAreaChartsCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/overviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessOverviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessLandingPageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/columnChartDetailsViewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/casesPerCountyCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/pageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/loginCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/navigationMenuCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/filterCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controllers.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/directives.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/filters.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/highcharts.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/modules/highcharts-more.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/modules/exporting.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/pattern-fill/pattern-fill.js'/>"></script>

<script type="text/javascript">
    $('.dropdown-toggle').dropdown();
</script>

</body>
</html>
