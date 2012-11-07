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
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>

<inera:page>
	<statistics:header>
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/table.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/careunit.js" />"></script>
    
	</statistics:header>
	<statistics:body tabid="5">
		<h2><spring:message code="careunit.title" /></h2>
		<p><spring:message code="duration.desc" /></p>
		
		<form id="statistics-form" class="form-inline">
			<fieldset>
				<legend><spring:message code="search.criteria.period" /></legend>
				
				<inera:row>
					<inera:col span="3">
						<inera-ui:field labelCode="search.criteria.startDate" name="fromDate">
							<inera-ui:monthYearField name="fromDate" />
						</inera-ui:field>
					</inera:col>
					<inera:col span="3">
						<inera-ui:field labelCode="search.criteria.endDate" name="toDate">
							<inera-ui:monthYearField name="toDate" />
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
			
			<div class="form-actions">
				<button type="submit" class="btn btn-primary"><spring:message code="search.criteria.submitSearch" /></button>
			</div>
		</form>
		
		<div id="table">
		<inera-ui:table id="resultTable">
			
		</inera-ui:table>
		</div>
	</statistics:body>
</inera:page> 
    
