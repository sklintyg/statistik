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
<title ng-bind="$root.page_title">Inera Statistics Service</title>

<!-- Styles -->
<link href="/css/inera-statistics.css" rel="stylesheet">
<link href="http://getbootstrap.com/2.3.2/assets/css/bootstrap.css" rel="stylesheet">
<link href="http://getbootstrap.com/2.3.2/assets/css/bootstrap-responsive.css" rel="stylesheet">

</head>
<body>
<div id="wrap">
	<div class="navbar navbar-inverse navbar-fixed-top">
	    <div class="navbar-inner">
		    <div class="container header">
		    	<div class="row-fluid">
					<div class="span2">
						<div class="headerbox-logo"><a href="/"><img alt="Till startsidan" src="/img/statistics_logo.png"/></a></div>
					</div>
					<div class="span2">
						<span>Statistiktjänst för ordinerad sjukskrivning</span>
					</div>
					<div class="span5 headerbox-user">
						<div class="span2"><img src="/img/avatar.png"/></div>
						<div class="span10" ng-show="userName.length">
							<span ng-switch="isDoctor">
							<strong ng-switch-when="true">L‰kare</strong>
							<strong ng-switch-default>Admin</strong>
							</span>
							 - <span class="logged-in">{{userName}}</span><br>
							<span class="location">{{caregiverName}}</span>
						</div>
					</div>
					<div class="span1">
						<div class="dropdown pull-right">
							<a class="dropdown-toggle settings" data-toggle="dropdown" href="#"></a>
							<ul class="dropdown-menu dropdown-menu-center" role="menu" aria-labelledby="dLabel">
								<li><a tabindex="-1" href="#">Hj‰lp</a></li>
								<li><a tabindex="-1" href="#">Logga ut</a></li>
							</ul>
						</div>				
					</div>
				</div>
		    </div>
	    </div>
    </div>
    <div class="container">
    	<div class="row-fluid">
    		<div id="content-body">
		    	<div class="span3 bs-docs-sidebar"> <!-- Start: Views navigation menu -->
					<ul class="nav nav-tabs nav-stacked">
						<li><a ng-href="#/oversikt" navigationaware>Nationell statistik</a></li>
						<li><a ng-href="#/sjukfallPerManad" id="navCasesPerMonthLink" navigationaware>Sjukfall, totalt</a></li>
					</ul>
					<div class="row-fluid">
						<div class="span12 bs-docs-sidebar">
							<label class="login-button-label" for="login-button">Verksamhetsstatistik</label>
							<button class="btn login-button" id="login-button">Logga in</button>
						</div>
					</div>
				</div> <!-- End: Views navigation menu -->
			</div>
			<div class="span9">
				<%-- ng-view that holds dynamic content managed by angular app --%>
	    		<div id="view" ng-view></div>
			</div>
		</div>
    </div>
</div>
<div id="footer">
	<div class="container">
		<p class="footer-content">
			<a href="#">Om tjänsten</a>
		</p>
	</div>
</div>

	<!-- Scripts -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap.min.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-transition.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-alert.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-modal.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-dropdown.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-scrollspy.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-tab.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-tooltip.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-popover.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-button.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-collapse.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-carousel.js"></script>
    <script src="http://getbootstrap.com/2.3.2/assets/js/bootstrap-typeahead.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min.js"></script>
    <script type="text/javascript" src="<c:url value="/js/app/app.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/app/controllers.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/app/directives.js"/>"></script>
	<script src="http://code.highcharts.com/highcharts.js"></script>
	<script src="http://code.highcharts.com/modules/exporting.js"></script>
    <script type="text/javascript" src="js/exportTableData.js" ></script>
    
</body>
</html>
