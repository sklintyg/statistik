<%--

    Copyright (C) 2013 Inera AB (http://www.inera.se)

    This file is part of Inera Certificate Web (http://code.google.com/p/inera-certificate-web).

    Inera Certificate Web is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    Inera Certificate Web is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="ROBOTS" content="nofollow, noindex"/>

    <title>Inera Statistics Servic</title>

    <link rel="icon" href="<c:url value="/favicon.ico" />" type="image/vnd.microsoft.icon"/>

    <link rel="stylesheet" href="<c:url value="/bootstrap/2.3.2/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/bootstrap/2.3.2/css/bootstrap-responsive.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/inera-statistics.css"/>">
</head>

<body class="start">

<div class="container-fluid">

    <div class="row-fluid">
        <div class="span6">
            <img class="pull-right" src="/img/404.png"/>
        </div>
        <div class="span6">

            <c:choose>

                <c:when test="${param.reason eq \"medarbetaruppdrag\"}">
                    <h1>
                        Inget medarbetaruppdrag
                    </h1>

                    <div id="noAuth" class="alert alert-error">
                        Medarbetaruppdrag saknas
                    </div>
                    
                </c:when>

                <c:when test="${param.reason eq \"badcredentials\"}">
                    <h1>
                        Inloggningen misslyckades
                    </h1>

                    <div id="noAuth" class="alert alert-error">
                        Inloggningen misslyckades
                    </div>
                    
                </c:when>

                <c:when test="${param.reason eq \"notfound\"}">
                    <h1>
                        not found
                    </h1>

                    <div id="notFound" class="alert alert-error">
                        not found text
                    </div>
                </c:when>

                <c:otherwise>
                    <h1>
                        Tekniskt fel
                    </h1>

                    <div id="genericTechProblem" class="alert alert-error">
                        Ett tekniskt fel har inträffat. Prova igen eller kontakta support om problemet kvarstår.
                    </div>
                    
                    <!-- reason: generic -->
                </c:otherwise>
            </c:choose>

            <a href="/" class="btn btn-success" id="loginBtn">Till förstasidan</a>


            <!--
            Error:
            <c:catch>
             <c:out value="${pageContext.errorData.throwable.message}" />,

             Stacktrace:
            <c:forEach items="${pageContext.errorData.throwable.stackTrace}" var="element">
              <c:out value="${element}" />,
              </c:forEach>
             </c:catch>
          -->

        </div>
    </div>
</div>

</body>
</html>
