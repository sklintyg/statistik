<%--
  ~ Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
  ~
  ~     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
  ~
  ~     Inera Statistics is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     (at your option) any later version.
  ~
  ~     Inera Statistics is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU LESSER GENERAL PUBLIC LICENSE for more details.
  ~
  ~     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
  ~     along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>
<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         trimDirectiveWhitespaces="true" session="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="ROBOTS" content="nofollow, noindex"/>

    <title>Intygsstatistik</title>

    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">

    <!-- build:css(src/main/webapp) app/vendor.css -->
    <!-- bower:css -->
    <link rel="stylesheet" href="bower_components/bootstrap-multiselect/dist/css/bootstrap-multiselect.css" />
    <link rel="stylesheet" href="bower_components/outdated-browser/outdatedbrowser/outdatedbrowser.min.css" />
    <link rel="stylesheet" href="bower_components/dropzone/dist/min/dropzone.min.css" />
    <!-- endbower -->
    <!-- endbuild -->

    <!-- build:css({build/.tmp,src/main/webapp}) app/app.css -->
    <!-- injector:css -->
    <link rel="stylesheet" href="/app/app.css">
    <!-- endinjector -->
    <!-- endbuild -->
</head>

<body class="start">

<div class="container-fluid">

    <div class="row-fluid">
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

    </div>
</div>

</body>
</html>
