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
<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>
<%@ attribute name="tabid" %>
<body>     
    <div class="container-fluid">
    	<div id="page-header">
		    <div id="page-header-left">
		      <img src='<c:url value="/web/resources/img/logo_stat.png" />'/> 
		    </div>
		    <div id="page-header-right"> </div>
        	<div id="status">
        	    <spring:message code="header.loggedInAs" /><br />
        	    <strong><sec:authentication property="principal.username" /> | <a href="<c:url value="/web/security/logout" />"><spring:message code="label.logout" /></a></strong>
	        </div>
		</div>
		<div id="menu">
		
		</div>

<form id="statistics-form" class="form-inline">
        <div class="row-fluid">
			<div class="span2">
				<div id="timespan">
					<fieldset>
						<legend><spring:message code="search.criteria.period" /></legend>
						<label class="control-label" for="fromDate"><spring:message code="search.criteria.startDate" /></label>
						<div class="controls">
						<span class="input-append">
							<input name="fromDate" type="text" class="monthField input-medium" /><span class="add-on"><i class="icon-calendar"></i></span>
						</span>
						</div>
		
						<label class="control-label" for="toDate"><spring:message code="search.criteria.endDate" /></label>
						<div class="input-append">
							<div class="controls">
								<span class="input-append">
									<input name="toDate" type="text" pattern="" class="monthField input-medium" /><span class="add-on"><i class="icon-calendar"></i></span>
								</span>
							</div>
						</div>
					</fieldset>
		  		</div>
				<div id="left-tabs" class="select${tabid}">
					<ul >
					  <li onclick="javascript:window.location.replace('<c:url value="/web/start" />');">Info</li>
					  <li onclick="javascript:window.location.replace('<c:url value="/web/age" />');">Åldersgrupper</li>
					  <li onclick="javascript:window.location.replace('<c:url value="/web/duration" />');">Längd på intyget</li>
					  <li onclick="javascript:window.location.replace('<c:url value="/web/monthwise" />');">Månadsvis</li>
					  <li onclick="javascript:window.location.replace('<c:url value="/web/sicknessgroups" />');">Sjukdomsgrupper</li>
					  <li onclick="javascript:window.location.replace('<c:url value="/web/careunit" />');">Enhet</li>
					</ul>	
				</div>
			</div>
			<div class="span10">			
				<div class="box">
					<jsp:doBody />
				</div>			
			</div>
		</div>
</form>
		<div class="footer">
			<div style="height: 15px; padding-top: 7px;">
				<span style="vertical-align: middle;">Intygsstatistik</span>
			</div>
		</div>
	</div>
</body>
