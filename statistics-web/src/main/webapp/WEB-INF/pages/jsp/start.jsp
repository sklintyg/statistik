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
		<script type="text/javascript">
			$(function() {
				var inera = new INERA.Cert();
				
				inera.list(function(data) {
					if (data.data.length > 0) {
						$.each(data.data, function(i, v) {
							
							var tr = $('<tr>');
							tr.append(
								$('<td>').html(v.sentDate)
							);
							
							tr.append(
								$('<td>').html(v.type)
							);
							
							tr.append(
								$('<td>').html(v.caregiverName)
							);
							
							tr.append(
								$('<td>').html(v.careunitName)
							);
							
							tr.append(
								$('<td>').html(v.status)
							);
							
							$('#certTable').append(tr);
						});
					}
				});
				
			});
		</script>
	</inera:header>
	<inera:body>
		<h2><spring:message code="certificates.header" /></h2>
		<p><spring:message code="certificates.desc" /></p>
		
		<inera-ui:table id="certTable">
			<thead>
				<tr>
					<th><spring:message code="certificates.date" /></th>
					<th><spring:message code="certificates.type" /></th>
					<th><spring:message code="certificates.issuedBy" /></th>
					<th><spring:message code="certificates.careUnit" /></th>
					<th><spring:message code="certificates.status" /></th>
				</tr>
			</thead>
			<tbody></tbody>
		</inera-ui:table>
		
		
		<a href="<c:url value="/web/security/logout" />"><spring:message code="label.logout" /></a>
	</inera:body>
</inera:page> 
    