<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"
         trimDirectiveWhitespaces="true"
         import="java.time.LocalDateTime" %>
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
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

  <title>Application Version</title>

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
<body>
<div style="padding-left:20px">
  <div class="page-header">
    <h1 style="margin-bottom: 10px;">Intygsstatistik</h1>
  </div>
  <div class="alert alert-block alert-info" style="width:50%">
    <h4 style="padding-bottom:5px;">Configuration info</h4>

    <div>Application version: <span class="label label-info">${versionUtil.projectVersion}</span></div>
    <div>Application build time: <span class="label label-info">${versionUtil.buildTime}</span></div>
    <div>
      Spring profiles:
      <div>
        <span style="margin-left: 20px;">From SYSTEM (Primary):</span> <span class="label label-info"><%= System.getProperty("spring.profiles.active") %></span><br/>
        <span style="margin-left: 20px;">From ENV (Secondary):</span> <span class="label label-info"><%= System.getenv("SPRING_PROFILES_ACTIVE") %></span>
      </div>
    </div>
  </div>
  <div class="muted">Server now time: <%= LocalDateTime.now() %></div>
</div>
</body>
</html>
