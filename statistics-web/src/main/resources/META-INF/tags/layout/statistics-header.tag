<%--

    Copyright (C) 2012 Inera AB (http://www.inera.se)

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
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="ROBOTS" content="nofollow, noindex" />
	
	<title><spring:message code="application.name" /></title>
	
	<link rel="icon" href="<c:url value="/favicon.ico" />" type="image/vnd.microsoft.icon"/>
	
	<link rel="stylesheet" href="<inera-ui:resource name="ui-lightness/jquery-ui-1.8.18.custom.css" type="css" />" />
	<link rel="stylesheet" href="<inera-ui:resource name="bootstrap-2.0.2.min.css" type="css" />" />
	<link rel="stylesheet" href="<inera-ui:resource name="inera-statistics.css" type="css" />" />
	
	<script type="text/javascript" src="<inera-ui:resource name="jquery-1.7.1.min.js" type="js" />"></script>
	<script type="text/javascript" src="<inera-ui:resource name="jquery-ui-1.8.18.custom.min.js" type="js" />"></script>
	<script type="text/javascript" src="<inera-ui:resource name="bootstrap-2.0.2.min.js" type="js" />"></script>
	
	<script type="text/javascript">
		var GLOB_CTX_PATH = '<c:out value="${pageContext.request.contextPath}" />';
	</script>
	<script type="text/javascript" src="<inera-ui:resource name="inera.js" type="js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/inera-statistics.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/monthpicker.js" />"></script>
	
	<jsp:doBody />
</head>
