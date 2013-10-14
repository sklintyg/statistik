<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Login Page</title>
	<!-- Styles -->
	<link href="/css/inera-statistics.css" rel="stylesheet">
	<link href="/bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet">
	<link href="/css/inera-statistics-responsive.css" rel="stylesheet">
	<link href="/bootstrap/2.3.2/css/bootstrap-responsive.css" rel="stylesheet">

</head>
<body onload='document.f.j_username.focus();'>

<div class="container">
    <div class="row-fluid center">
        <div class="span12">
        	<legend>Logga in för verksamhetsstatistik - demo</legend>
            <form name='f' method='POST' action='/j_spring_security_check'>
			    <label>Användarnamn:</label>
			    <input type='text' name='j_username' value='' placeholder="Ange användanamn.." style="height: 30px;">
			    <label>Lösenord:</label>
                <input type='password' name='j_password' placeholder="Ange lösenord.." style="height: 30px;"/>
                <br>
				<button class="btn btn-success" name="Login" type="submit">Logga in</button>
			    <button class="btn" name="Återställ" type="reset">Återställ</button>
			</form>
        </div>
    </div>
</div>
</body>
</html>
