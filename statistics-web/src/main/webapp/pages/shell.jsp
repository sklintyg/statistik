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
<div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" ng-href="/">Statistiktjänsten</a>
        </div>
      </div>
    </div>
    <div class="container">
    	<div class="row-fluid">
    		<div id="content-body">
		    	<div class="span3 bs-docs-sidebar"> <!-- Start: Views navigation menu -->
					<ul class="nav nav-tabs nav-stacked">
						<li class="active"><a ng-href="#/oversikt">Nationell statistik</a></li>
						<li><a ng-href="#/sjukfallPerManad" id="navCasesPerMonthLink">Sjukfall, totalt</a></li>
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
		
    

	<div id="footer">
		<div class="container">
			<p>Footer</p>
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
	<script src="http://code.highcharts.com/highcharts.js"></script>
	<script src="http://code.highcharts.com/modules/exporting.js"></script>
    <script type="text/javascript" src="js/exportTableData.js" ></script>
    
    	<!-- MOVE THESE SCRIPTS TO FILE -->
	<script type="text/javascript">
		$(document).ready(function() {
			$.ajax({
				url : "api/getNumberOfCasesPerMonth",
				context : document.body,
				success : function(result) {
					$(function() {
						$('#overview-distribution-by-county-container').highcharts({
							title : {
								text : result.title,
								x : -20
							//center
							},
							xAxis : {
								categories : result.categories
							},
							yAxis : {
								title : {
									text : ''
								},
								plotLines : [ {
									value : 0,
									width : 1,
									color : '#808080'
								} ]
							},
							legend : {
								layout : 'vertical',
								align : 'right',
								verticalAlign : 'middle',
								borderWidth : 0
							},
							series : result.dataSeries
						});
					});
				}
			});
		});
	</script>
	<script type="text/javascript">
		var c=document.getElementById("myCanvas");
		var ctx=c.getContext("2d");
		ctx.beginPath();
		ctx.arc(95,50,40,0,2*Math.PI);
		ctx.stroke("#11b73c");
		ctx.fillStyle = '#11b73c';
		ctx.fill();
	</script>
    
    
</body>
</html>
