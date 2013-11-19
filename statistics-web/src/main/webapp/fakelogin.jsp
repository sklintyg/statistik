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
            <form name='f' method='POST' action='/fake'>
			    <textarea name='userJsonDisplay' id='userJsonDisplay' placeholder="Ange JSON struktur" style="height: 150px;">{ 
  "fornamn":"Bengt", 
  "efternamn":"Siffersson", 
  "hsaId":"HSA-BS", 
  "lakare":true
}</textarea>
                <br>
				<button class="btn btn-success" name="Login" id="login_btn" type="submit">Logga in</button>
			    <button class="btn" name="Återställ" type="reset">Återställ</button>
			</form>
        </div>
    </div>
</div>
</body>
</html>
