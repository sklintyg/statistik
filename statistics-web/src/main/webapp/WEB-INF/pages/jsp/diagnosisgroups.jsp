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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="inera" uri="http://www.inera.se/certificates/layout/tags" %>
<%@ taglib prefix="statistics" uri="http://www.inera.se/certificate/statistics/layout/tags" %>
<%@ taglib prefix="form" uri="http://www.inera.se/certificate/statistics/form/tags" %>
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>

<inera:page>
	<statistics:header>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
 	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["corechart"]});
	</script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/columnchart.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/table.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/diagnosisgroups.js" />"></script>
    
	</statistics:header>
	<statistics:body tabid="4">
		<h2><spring:message code="diagnosisgroups.title" /></h2>
		<p><spring:message code="diagnosisgroups.desc" /></p>
		
        <fieldset>
            <inera:row>
            <div class="controls controls-row">
            </div>
            </inera:row>
        </fieldset>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary"><spring:message code="search.criteria.submitSearch" /></button>
        </div>
		<div id="diagram"></div>

		<div id="table">
			<inera-ui:table id="resultTable">			
			</inera-ui:table>
		</div>
	</statistics:body>
</inera:page> 
    
