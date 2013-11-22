<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <title>Application Version</title>
  <link rel="stylesheet" type="text/css" href="bootstrap/2.3.2/css/bootstrap.min.css">
</head>
<body>
<div style="padding-left:20px">
  <div class="page-header">
    <h3>Statistiktj�nsten</h3>
  </div>
  <div class="alert alert-block alert-info" style="width:50%">
    <h4 style="padding-bottom:5px;">Configuration info</h4>

    <div>Application version: <span class="label label-info"><spring:message code="project.version"/></span></div>
    <div>Spring profiles: <span class="label label-info"><%= System.getProperty("spring.profiles.active") %></span></div>
  </div>
  <div class="muted">Server now time: <%= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) %></div>
</div>
</body>
</html>