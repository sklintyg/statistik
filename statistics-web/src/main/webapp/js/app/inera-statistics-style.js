$(function() {

    $('.statistics.accordion').on('show', function (e) {
         $(e.target).prev('.accordion-heading.statistics-menu').find('.accordion-toggle.first-level-menu').addClass('active');
    });

    $('.statistics.accordion').on('hide', function (e) {
        $(this).find('.accordion-toggle.first-level-menu').not($(e.target)).removeClass('active');
    });

});

$(function() {

    $('.about.accordion').on('show', function (e) {
         $(e.target).prev('.accordion-heading.statistics-menu').find('.accordion-toggle.first-level-menu').addClass('active');
    });

    $('.about.accordion').on('hide', function (e) {
        $(this).find('.accordion-toggle.first-level-menu').not($(e.target)).removeClass('active');
    });

});
/*
$(document).ready(function(){
	$("#business-statistics-menu-group").hide();
	
	$("#log-out-container a").hide();
	
	$('#business-login-btn').click(function(){
		$("#national-statistics-toggle").removeClass('active');
		$("#national-statistics-collapse").removeClass('in'); 
		$("#business-login-container").hide();
		$("#business-statistics-menu-group").show();
		$("#log-out-container a").show();
		$("#business-statistics-toggle").addClass('active');
		$("#business-statistics-collapse").addClass('in'); 
	});
});
*/

$(document).ready(function(){
	var toggleHref = $('#business-statistics-toggle').attr("href"); // Saves href for later
	$("#business-statistics-toggle").removeAttr("href"); 
	$("#business-statistics-toggle").addClass("disabled");

    if(isLoggedIn) {
        $("#business-statistics-toggle").removeClass("disabled");
        $("#business-statistics-toggle").attr("href", toggleHref);
    }

	$('#business-login-btn').click(function(){
        window.location.replace("/login.jsp");
	});
});
