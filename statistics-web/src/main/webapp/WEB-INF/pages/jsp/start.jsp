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
	<inera:statistics-header>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
 	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart", "table"]});
	</script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/columnchart.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/table.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/start.js" />"></script>
	
    
	</inera:statistics-header>
	<inera:statistics_body>
		<h2><spring:message code="search.title" /></h2>
		<p><spring:message code="search.desc" /></p>
		
		Välj söktyp via flikarna till vänster eller länkarna härunder.
		
		<a href="<c:url value="/web/security/logout" />"><spring:message code="label.logout" /></a>
	</inera:statistics_body>
</inera:page> 
    
