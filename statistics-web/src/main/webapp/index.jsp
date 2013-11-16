<%--

    Copyright (C) 2012 Inera AB (http://www.inera.se)

    This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).

    Inera Statistics is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Inera Statistics is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns:ng="http://angularjs.org" lang="sv" id="ng-app" ng-app>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<!--[if lte IE 8]>
  <script src="/js/app/json2.js"></script>
<![endif]-->

<!--[if lte IE 8]>
  <script>
    document.createElement('ng-include');
    document.createElement('ng-pluralize');
    document.createElement('ng-view');

    // Optionally these for CSS
    document.createElement('ng:include');
    document.createElement('ng:pluralize');
    document.createElement('ng:view');
  </script>
<![endif]-->
<title>Inera Statistics Service</title>
</head>
<body>
	<jsp:forward page="pages/shell.jsp" />
</body>
</html>
