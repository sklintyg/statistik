<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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

<!DOCTYPE html>
<html>
<head>
  <title>Application Version</title>
  <link rel="stylesheet" type="text/css" href="bootstrap/2.3.2/css/bootstrap.min.css">
</head>
<body>
<div style="padding-left:20px">
  <div class="page-header">
    <h3>Statistiktjänsten</h3>
  </div>
  <div class="alert alert-block alert-info" style="width:50%">
    <h4 style="padding-bottom:5px;">Configuration info</h4>

    <div>Application version: <span class="label label-info"><spring:message code="project.version"/></span></div>
    <div>Application build time: <span class="label label-info"><spring:message code="project.buildtime"/></span></div>
    <div>Application build host: <span class="label label-info"><spring:message code="project.buildhost"/></span></div>
    <div>Spring profiles: <span class="label label-info"><%= System.getProperty("spring.profiles.active") %></span></div>
  </div>
  <div class="muted">Server now time: <%= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) %></div>
</div>
</body>
</html>
