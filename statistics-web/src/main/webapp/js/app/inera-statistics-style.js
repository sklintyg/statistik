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

$(document).ready(function(){
	var toggleHref = $('#business-statistics-toggle').attr("href"); 
	$("#business-statistics-toggle").removeAttr("href"); 
	$("#business-statistics-toggle").addClass("disabled");
	
	$('#business-login-btn').click(function(){
		$("#business-statistics-toggle").removeClass("disabled");
		$("#business-statistics-toggle").attr("href", toggleHref);
	});
});