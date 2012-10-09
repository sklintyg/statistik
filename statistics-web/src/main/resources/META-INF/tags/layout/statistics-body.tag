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

        <div class="row-fluid">
			<div id="left-tabs">
				<ul >
				  <li onclick="javascript:window.location.replace('<c:url value="/web/start" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Info</li>
				  <li onclick="javascript:window.location.replace('<c:url value="/web/age" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Åldersgrupper</li>
				  <li onclick="javascript:window.location.replace('<c:url value="/web/duration" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Längd på intyget</li>
				  <li onclick="javascript:window.location.replace('<c:url value="/web/monthwise" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Månadsvis</li>
				  <li onclick="javascript:window.location.replace('<c:url value="/web/sicknessgroups" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Sjukdomsgrupper</li>
				  <li onclick="javascript:window.location.replace('<c:url value="/web/careunit" />');"><img src="<c:url value="/web/resources/img/inkorg.png" />" />Enhet</li>
				</ul>	
			</div>
			
			<div class="row-fluid box">
				<div class="span12">
					<jsp:doBody />
				</div>
			</div>			
		</div>
		<div class="footer">
			<div style="height: 15px; padding-top: 7px;">
				<span style="vertical-align: middle;">Intygsstatistik</span>
			</div>
		</div>
	</div>
</body>
