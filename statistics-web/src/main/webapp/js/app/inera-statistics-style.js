$(document).ready(function(){
	var toggleHref = $('#business-statistics-toggle').attr("href"); 
	$("#business-statistics-toggle").removeAttr("href"); 
	$("#business-statistics-toggle").addClass("disabled");
	
	$('#business-login-btn').click(function(){
		$("#business-statistics-toggle").removeClass("disabled");
		$("#business-statistics-toggle").attr("href", toggleHref);
		$("#business-login-container").css("display","none");
		$("#business-logged-in-user-container").css("display","block");
	});
});