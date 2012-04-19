<%--

    Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="inera" uri="http://www.inera.se/certificates/layout/tags" %>
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>

<inera:page>
	<inera:header>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart"]});

			$(function() {
				
				var ajax = new INERA.Ajax();
				
				$('#statistics-form').submit(function(e) {
					e.preventDefault();
					
					var criterias = new Object();
					criterias.startDate = $('input[name="fromDate"]').val();
					criterias.endDate = $('input[name="toDate"]').val();
					
					criterias.basedOnExamination = $('input[name="basedOnExamination"]:checked').val() == 1 ? true : false;
					criterias.basedOnTelephoneContact = $('input[name="basedOnTelephoneContact"]:checked').val() == 1 ? true : false;
					
					criterias.viewRange = $('select option:selected').prop('value');
					
					ajax.post('/statistics/search', criterias, function(data) {
				        var drawdata = new google.visualization.DataTable();
				        drawdata.addColumn('date', 'Year');
				        drawdata.addColumn('number', 'Totalt');
				        drawdata.addColumn('number', 'Urval');
				        var totals = data.data.totals;
				        var matched = data.data.matches;
				        var max = 0;
				        var urval_i = 0;
				        for (var i = 0; i < totals.length; i++) {
				        	var v1 = totals[i].start;
				        	var v2 = totals[i].value;

				        	if (v2 > max) {
				        		max = v2;
				        	}
				        	
				        	var v3 = 0;
				        	if (urval_i < matched.length && matched[urval_i].end <= totals[i].end) {
				        		v3 = matched[urval_i].value;
				        		urval_i++;
				        	}
					        drawdata.addRow(new Array(new Date(v1), v2, v3));				        	
				        }

                           // Create and draw the visualization.
                           new google.visualization.LineChart(document.getElementById('diagram')).
                               draw(drawdata, {curveType: "none",
                                           width: 500, height: 400,
                                           vAxis: {maxValue: max, minValue: 0}}
                                   );
                           
                           var data2 = new google.visualization.DataTable();
                           data2.addColumn('string', 'Ålder');
                           data2.addColumn('number', 'Total');
                           data2.addColumn('number', 'Urval');
                           data2.addRows([
                             ['0-29', 124, 37],
                             ['30-39', 55, 12],
                             ['40-49', 100, 100],
                             ['50-59', 77, 60],
                             ['60-', 80, 99]
                           ]);

                           var options = {
                             hAxis: {title: 'Ålder', titleTextStyle: {color: 'red'}}
                           };

                           var chart = new google.visualization.ColumnChart(document.getElementById('diagram2'));
                           chart.draw(data2, options);

						INERA.log("Success");
					});
				});
			});
		</script>
	</inera:header>
	<inera:body>
		<h2><spring:message code="search.title" /></h2>
		<p><spring:message code="search.desc" /></p>
		
		<form id="statistics-form">
			<fieldset>
				<legend><spring:message code="search.criteria.period" /></legend>
				
				<inera:row>
					<inera:col span="3">
						<inera-ui:field labelCode="search.criteria.startDate" name="fromDate">
							<inera-ui:dateField name="fromDate" />
						</inera-ui:field>
					</inera:col>
					<inera:col span="3">
						<inera-ui:field labelCode="search.criteria.endDate" name="toDate">
							<inera-ui:dateField name="toDate" />
						</inera-ui:field>
					</inera:col>
				</inera:row>
			</fieldset>
			
			<fieldset>
				<legend><spring:message code="search.criteria.basedOn" /></legend>
				<inera:row>
					<inera:col span="2">
						<inera-ui:field name="basedOnExamination" labelCode="search.criteria.basedOn.examination">
							<input name="basedOnExamination" type="checkbox" value="1"/>
						</inera-ui:field>
					</inera:col>
					
					<inera:col span="2">
						<inera-ui:field name="basedOnTelephoneContact" labelCode="search.criteria.basedOn.telephone">
							<input name="basedOnTelephoneContact" type="checkbox" value="1"/>
						</inera-ui:field>
					</inera:col>
				</inera:row>
			</fieldset>
			
			<fieldset>
				<legend>Visa statistik</legend>
				<inera:row>
					<inera:col span="2">
						<inera-ui:field name="viewRange" labelCode="search.criteria.range">
							<select name="viewRange">
								<option value="0">Dagsvis</option>
								<option value="1">Veckovis</option>
								<option value="2">Månadsvis</option>
								<option value="3">Årsvis</option>
							</select>
						</inera-ui:field>
					</inera:col>
				</inera:row>
			</fieldset>
			
			
			<div class="form-actions">
				<button type="submit" class="btn btn-primary"><spring:message code="search.criteria.submitSearch" /></button>
			</div>
		</form>
		
		<div id="diagram"></div>
		<div id="diagram2"></div>
		
		<a href="<c:url value="/web/security/logout" />"><spring:message code="label.logout" /></a>
	</inera:body>
</inera:page> 
    