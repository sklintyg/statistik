<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${!healthcheckUtil.overviewOk}">
	<%response.sendError(500, "Internal Error"); %>
</c:if>
<html>
<head>
	<title>Statistics Health Check</title>
</head>
<body>
	System OK
</body>
</html>
