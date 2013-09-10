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
<html ng-app>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inera Statistics Service</title>
<!-- Styles -->
<link href="/css/inera-statistics.css" rel="stylesheet">
<link href="http://getbootstrap.com/2.3.2/assets/css/bootstrap.css" rel="stylesheet">
<link href="http://getbootstrap.com/2.3.2/assets/css/bootstrap-responsive.css" rel="stylesheet">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min.js"></script>
<script type="text/javascript" src="js/overview.js"></script>

</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="#">Statistiktjänsten</a>
        </div>
      </div>
    </div>

    <div class="container">
		<div class="row-fluid">
			<div id="content-body">
				<div class="span3 bs-docs-sidebar"> <!-- Start: Overview navigation menu -->
					<ul class="nav nav-tabs nav-stacked">
						<li class="active"><a href="#">Nationell statistik</a></li>
						<li><a href="#">Sjukfall, totalt</a></li>
					</ul>
				</div>
				<!-- End: Overview navigation menu -->
				
				<div class="span9" id="overview-content" ng-controller="OverviewCtrl"> <!-- Start: Overview content -->
					<div class="row-fluid">
						<div class="span4">
							<h1 tabindex="-1">Nationell statistik</h1>
						</div>
						<div class="span4 offset2" id="overview-print-button">
							<button class="btn">Skriv ut...</button>
						</div>
					</div>
					<div class="row-fluid">
						<span class="span12" ng-bind-html-unsafe="resultValue" message="" key="overview.description" class="ng-binding">Utvecklingen i landet de senaste 3 månaderna</span>
					</div>
					<div class="row-fluid">
						<div class="span3">
							<div class="overview-box-header">
								<h2>Könsfördelning sjukfall</h2>
							</div>
							<div id="overview-distribution-per-sex-container"></div>
							<div class="overview-box-information-container">
								Män: {{casesPerMonthMaleProportion}}%<br/>
								Kvinnor: {{casesPerMonthFemaleProportion}}%
								<!-- span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span -->
							</div>
						</div>
						<div class="span3">
							<div class="overview-box-header">
								<h2>Förändring</h2>
							</div>
							<div id="overview-change-container"></div>
							<div class="overview-box-information-container">
								{{casesPerMonthAlteration}}%
								<span class="overview-small-box-information">Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
							</div>
						</div>
						<div class="span6">
							<div class="overview-box-header">
								<h2>Fördelning diagnosgrupper</h2>
							</div>
							<div id="overview-distribution-diagnostic-groups-container"></div>
							<div class="overview-box-information-container">
								<span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span6">
							<div class="overview-box-container">
								<div class="overview-box-header">
									<h2>Fördelning åldersgrupper</h2>
								</div>
								<div id="overview-distribution-of-age-groups-container"></div>
								<div class="overview-box-information-container">
									<span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="overview-box-container">
								<div class="overview-box-header">
									<h2>Fördelning sjukskrivningsgrad</h2>
								</div>
								<div id="overview-distribution-of-sick-degree-container"></div>
								<div class="overview-box-information-container">
									<span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span6">
							<div class="overview-box-container">
								<div class="overview-box-header">
									<h2>Fördelning sjukskrivningslängd</h2>
								</div>
								<div id="overview-distribution-sick-length-container"></div>
								<div class="overview-box-information-container">
									<span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="overview-box-container">
								<div class="overview-box-header">
									<h2>Fördelning per län</h2>
								</div>
								<div id="overview-distribution-by-county-container"></div>
								<div class="overview-box-information-container">
									<span>Förändring av antalet sjukfall jämfört med föregående tre månader.</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- End: Overview content -->
				
			</div>
		</div>
    </div> <!-- /container -->
	
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
</body>
</html>
