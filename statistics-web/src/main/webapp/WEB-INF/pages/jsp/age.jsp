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
	<script type="text/javascript" src="<c:url value="/resources/javascript/age.js" />"></script>
	
    
	</inera:statistics-header>
	<inera:statistics_body>
		<h2><spring:message code="search.title" /></h2>
		<p><spring:message code="search.desc" /></p>
		
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
				<div class="controls controls-row">
					    <select id="disability" name="disability" class="span4">
					        <option value="all">Alla grader av sjukskrivning (25% - 100%)</option> 
					        <option value="100">Helt nedsatt (100%)</option> 
					        <option value="75">Nedsatt med 3/4 (75%)</option> 
					        <option value="50">Nedsatt med 1/2 (50%)</option> 
					        <option value="25">Nedsatt med 1/4 (25%)</option> 
					    </select>
					
					    <select id="group" name="group" class="span4">
					        <option value="all">Alla sjukdomsgrupper</option> 
					        <option value="I">I	A00-B99	Vissa infektionssjukdomar och parasitsjukdomar</option>
					        <option value="II">II	C00-D48	Tumörer</option>
					        <option value="III">III	D50-D89	Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet</option>
					        <option value="IV">IV	E00-E90	Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar</option>
					        <option value="V">V	F00-F99	Psykiska sjukdomar och syndrom samt beteendestörningar</option>
					        <option value="VI">VI	G00-G99	Sjukdomar i nervsystemet</option>
					        <option value="VII">VII	H00-H59	Sjukdomar i ögat och närliggande organ</option>
					        <option value="VIII">VIII	H60-H95	Sjukdomar i örat och mastoidutskottet</option>
					        <option value="IX">IX	I00-I99	Cirkulationsorganens sjukdomar</option>
					        <option value="X">X	J00-J99	Andningsorganens sjukdomar</option>
					        <option value="XI">XI	K00-K93	Matsmältningsorganens sjukdomar</option>
					        <option value="XII">XII	L00-L99	Hudens och underhudens sjukdomar</option>
					        <option value="XIII">XIII	M00-M99	Sjukdomar i muskuloskeletala systemet och bindväven</option>
					        <option value="XIV">XIV	N00-N99	Sjukdomar i urin- och könsorganen</option>
					        <option value="XV">XV	O00-O99	Graviditet, förlossning och barnsängstid</option>
					        <option value="XVI">XVI	P00-P96	Vissa perinatala tillstånd</option>
					        <option value="XVII">XVII	Q00-Q99	Medfödda missbildningar, deformiteter och kromosomavvikelser</option>
					        <option value="XVIII">XVIII	R00-R99	Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes</option>
					        <option value="XIX">XIX	S00-T98	Skador, förgiftningar och vissa andra följder av yttre orsaker</option>
					        <option value="XX">XX	V01-Y98	Yttre orsaker till sjukdom och död</option>
					        <option value="XXI">XXI	Z00-Z99	Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården</option>
					        <option value="XXII">XXII	U00-U99	Koder för särskilda ändamål</option>
						</select>
				</div>
				</inera:row>
			</fieldset>
			
			
			<div class="form-actions">
				<button type="submit" class="btn btn-primary"><spring:message code="search.criteria.submitSearch" /></button>
			</div>
		</form>
		
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
		
		<a href="<c:url value="/web/security/logout" />"><spring:message code="label.logout" /></a>
	</inera:statistics_body>
</inera:page> 
    
