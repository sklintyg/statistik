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
	</statistics:header>
	<statistics:body-plain>
		<h2>Översikt</h2>
		<p>Visa intygsstatistik för vårdgivare i Sverige. Det är möjligt att göra utsökningar som begränsar urvalet utifrån sjukskrivningsgard och / eller sjukdomskategori</p>
		<p>Flera typer av sökningar finns med gruppering på åldersgrupper, intygens längd, månad etc.
		<p>Välj Statistik i menyn ovan för att börja. Du måste välja vilken typ av mätning du vill göra samt tidspannet för denna.</p>		
		<p>Inga kvalitetsmått visas ännu.</p>		
	</statistics:body-plain>
</inera:page> 
    
