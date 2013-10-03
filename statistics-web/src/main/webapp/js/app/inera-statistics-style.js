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
