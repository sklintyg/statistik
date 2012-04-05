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

<%@ taglib prefix="inera" uri="http://www.inera.se/certificates/layout/tags" %>
<%@ taglib prefix="inera-ui" uri="http://www.inera.se/certificates/component/tags" %>
 
<inera:page>
	<inera:header>
		<script type="text/javascript">
			$(function() {
				$('#loginModal').modal('show');
				$('input[name="j_username"]').focus();
			});
		</script>
	</inera:header>
	<inera:body>
		<form method="post" action="<c:url value="/j_spring_security_check" />">
			<inera-ui:modal id="loginModal" titleCode="label.login">
				<inera-ui:field name="j_username" labelCode="label.hsa">
					<input name="j_username" type="text" class="xlarge" />
				</inera-ui:field>
				
				<input type="hidden" name="j_password" value="0000" />
			</inera-ui:modal>
		</form>
	</inera:body>
</inera:page> 
    