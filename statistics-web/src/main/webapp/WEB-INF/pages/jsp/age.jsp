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
<%@ taglib prefix="statistics" uri="http://www.inera.se/certificate/statistics/layout/tags" %>
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>

<inera:page>
	<statistics:header>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
 	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart", "table"]});
	</script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/columnchart.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/table.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/age.js" />"></script>
	
    
	</statistics:header>
	<statistics:body tabid="2">
		<h2>Antal vs ålder</h2>
		<p>Antal intyg i varje åldersgrupp</p>
		
		<%@include file="form.inc" %>
				
		<div id="diagram"></div>

		<div id="table">
		<inera-ui:table id="resultTable">
		  <thead>
		    <tr><th>Ålder</th><th>Antal intyg/män</th><th>Antal intyg/kvinnor</th><th>Antal intyg totalt</th></tr>
		  </thead>
		  <tbody>
		  </tbody>		
		  <tfoot>
		    <tr>
		      <th>Totalt</th><td>0</td><td>0</td><td>0</td>
		    </tr>
		  </tfoot>
		</inera-ui:table>
		</div>
	</statistics:body>
</inera:page> 
    
