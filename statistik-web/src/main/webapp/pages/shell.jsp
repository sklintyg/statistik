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
    <link href="<c:url value='/webjars/bootstrap/3.1.1/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet">
    <![endif]-->
    <link href="<c:url value='/css/inera-statistics.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/inera-statistics-responsive.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/helperclasses.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css'/>" rel="stylesheet" media="not print">
    <link href="<c:url value='/css/filter.css'/>" rel="stylesheet" media="not print">

    <link href="<c:url value='/webjars/bootstrap/3.1.1/css/bootstrap.min.css'/>" rel="stylesheet" media="all">
    <link href="<c:url value='/webjars/bootstrap/3.1.1/css/bootstrap-theme.min.css'/>" rel="stylesheet" media="all">
    <link href="<c:url value='/css/print.css'/>" rel="stylesheet" media="print">

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
<body data-ng-controller="pageCtrl">
<spring:eval expression='@loginVisibility.isLoginVisible()' var="loginVisible"/>

<!-- Navbar
================================================== -->
<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 navbar hidden-print">
		    <div class="navbar-inner">
		        <div class="container-fluid">
		            <div class="row" id="navigation-container">
		                <div class="col-xs-12 col-sm-3 pull-left" style="width: auto !important;">
		                    <div class="headerbox-logo">
		                        <a href="<c:url value='/'/>">
		                            <img alt="Till startsidan" src="<c:url value='/img/statistiktjansten-logotype.png'/>"/>
		                        </a>
		                    </div>
		                </div>
		                <div class="col-xs-12 col-sm-2 pull-left">
		                    <span message key="statistics.header.extra-text"></span>
		                </div>
		                <c:if test="${loginVisible}">
		                    <div class="hidden-xs col-sm-5 col-md-6 col-lg-4 pull-right">
		                        <div id="business-login-container" ng-hide="isLoggedIn">
		                            <span id="business-login-span"><span message key="lbl.for-verksamhetsstatistik"></span></span>
		                            <button class="btn" data-ng-click="loginClicked('${applicationScope.loginUrl}')"
		                                    type="button" id="business-login-btn" value="Logga in"><span message key="lbl.log-in"></span>
		                            </button>
		                        </div>
		                        <div id="business-logged-in-user-container" ng-show="isLoggedIn">
		                            <div class="header-box-user-profile pull-right">
		                                <span class="user-name pull-right" data-ng-bind="userNameWithAccess"></span>
		                                <br>
		                                <span>{{verksamhetName}}</span>
		                                <br/>
		                                <span class="user-logout pull-right">
											<a href="saml/logout"><span message key="lbl.log-out"></span></a>
										</span>
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


<div id="wrapper">
    <%-- Sidebar --%>
    <div id="sidebar-wrapper" class="" data-ng-controller="navigationMenuCtrl">
        <h1 class="hidden-header"><span message key="statistics.hidden-header.sidans-huvudnavigering"></span></h1>
        <jsp:include page="menu.jsp"/>
    </div>
    <%-- Sidebar end--%>

    <%-- The mobile menu must be outside of the sidebar-wrapper --%>
    <jsp:include page="mobilemenu.jsp"/>

    <div id="page-content-wrapper">
        <div class="container-fluid">
            <!-- Docs nav
            ================================================== -->
            <div class="row">

                <div class="col-xs-12">

                    <%-- Filter component start--%>
                    <div class="row ng-hide" ng-show="isVerksamhetShowing">
                        <div class="col-xs-12">
                            <business-filter></business-filter>
                        </div>
                    </div>

                    <div class="row ng-hide" ng-show="isLandstingShowing">
                        <div class="col-xs-12">
                            <landsting-filter></landsting-filter>
                        </div>
                    </div>
                    <%-- Filter component end --%>

                    <%-- Main outlet --%>
                    <div class="row">
                        <div class="col-xs-12">
                            <%-- data-ng-view that holds dynamic content managed by angular app --%>
                            <div id="view" data-ng-view></div>
                        </div>
                    </div>
                    <%-- Main outlet end --%>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<!-- Placed at the end of the document so the pages load faster -->
<!--[if lt IE 9]>
<script type="text/javascript" src="<c:url value='/webjars/respond/1.3.0/respond.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/html5shiv.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/css3-mediaqueries.js'/>"></script>
<![endif]-->
<script type="text/javascript" src="<c:url value='/webjars/jquery/1.10.2/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/bootstrap/3.1.1/js/bootstrap.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/underscorejs/1.7.0/underscore-min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/momentjs/2.10.3/moment.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/1.2.16/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/1.2.16/i18n/angular-locale_sv.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/1.2.16/angular-cookies.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/1.2.16/angular-route.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/1.2.16/angular-sanitize.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/0.12.1/ui-bootstrap-tpls.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/dropzone/4.0.1/dropzone.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/respond/1.3.0/respond.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/app.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/resources/constantsModule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/factories.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/printFactory.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/businessfilter/businessFilterModule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/businessfilter/businessFilterFactory.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/businessfilter/businessFilterDirective.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/diagnosisTreeFilter.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/services/messageService.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/common.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/charts/chartModule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/singleLineChartCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/doubleAreaChartsCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/overviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessOverviewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/business/businessLandingPageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/columnChartDetailsViewCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/casesPerCountyCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/landsting/landstingFileUploadCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/pageCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/loginCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/controller/navigationMenuCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/treemultiselector/treeMultiSelectorModule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/treemultiselector/treeMultiSelectorUtil.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/treemultiselector/treeMultiSelectorCtrl.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/treemultiselector/treemultiselectorDirective.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/chartseriesbuttongroup/chartSeriesButtonGroupModule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/chartseriesbuttongroup/chartSeriesButtonGroupDirective.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/shared/angular-dropzone/dropzoneDirective.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/directives.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/filters.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/app/messages.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/highcharts.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/modules/highcharts-more.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/3.0.5/modules/exporting.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/lib/highcharts/pattern-fill/pattern-fill.js'/>"></script>

<script type="text/javascript">
    $('.dropdown-toggle').dropdown();
</script>

</body>
</html>
