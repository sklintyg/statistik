$(document).ready(function(){
	var toggleHref = $('#business-statistics-toggle').attr("href"); 
	$("#business-statistics-toggle").removeAttr("href"); 
	$("#business-statistics-toggle").addClass("disabled");

    if(isLoggedIn) {
        $("#business-statistics-toggle").removeClass("disabled");
        $("#business-statistics-toggle").attr("href", toggleHref);
        $("#business-login-container").css("display","none");
        $("#business-logged-in-user-container").css("display","block");
    }

	$('#business-login-btn').click(function(){
        window.location.replace("/login.jsp");
	});
	
});
